/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dscm.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDateTime;

import org.apache.isis.applib.AbstractContainedObject;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.background.ActionInvocationMemento;
import org.apache.isis.applib.services.background.BackgroundCommandService;
import org.apache.isis.applib.services.background.BackgroundService;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.command.Command;
import org.apache.isis.applib.services.memento.MementoService;
import org.apache.isis.applib.value.Blob;

import org.estatio.dscm.DscmDashboard;
import org.estatio.dscm.dom.asset.Asset;
import org.estatio.dscm.dom.asset.Assets;
import org.estatio.dscm.dom.display.Display;
import org.estatio.dscm.dom.display.DisplayGroup;
import org.estatio.dscm.dom.display.DisplayGroups;
import org.estatio.dscm.dom.playlist.Playlist;
import org.estatio.dscm.dom.playlist.PlaylistItem;
import org.estatio.dscm.dom.playlist.PlaylistType;
import org.estatio.dscm.dom.playlist.Playlists;
import org.estatio.dscm.dom.publisher.Publisher;
import org.estatio.dscm.dom.publisher.Publishers;

@DomainService
@Named("Administration")
public class SyncService extends AbstractContainedObject implements BackgroundCommandService {

    private Map<String, String> properties;

    @Programmatic
    public Map<String, String> getProperties() {
        return properties;
    }

    @Programmatic
    @PostConstruct
    public void init(final Map<String, String> properties) {
        this.properties = properties;
    }

    /*
    @Hidden
    public void synchronizeNowScheduled() {
        for (DisplayGroup displayGroup : displayGroups.allDisplayGroups()) {
            backgroundService.execute(this).synchronizeNow(displayGroup);
        }
    }
    */
    
    @Hidden
    public void quartzTester() {
        backgroundService.execute(this).printText();
    }

    private void printText() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        System.out.println("Het is nu " + dateFormat.format(cal.getTime()));
    }

    public Object synchronizeNow(DisplayGroup displayGroup) {
        final String path = properties.get("dscm.server.path");
        path.toLowerCase();
        Runtime rt = Runtime.getRuntime();

        for (Display display : displayGroup.getDisplays()) {
            removePlaylists(display, path);
            removeDisplayAssets(display, path);
        }

        for (Playlist playlist : playlists.findByDisplayGroupAndType(displayGroup, PlaylistType.MAIN)) {
            for (Display display : displayGroup.getDisplays()) {
                for (LocalDateTime dateTime : playlist.nextOccurences(clockService.now().plusDays(7))) {
                    writePlaylist(display, dateTime, effectiveItems(playlist, dateTime), rt);
                }
            }
        }

        for (Display display : displayGroup.getDisplays()) {
            syncPlaylist(display, path);
        }

        return newViewModelInstance(DscmDashboard.class, "dashboard");
    }

    @Programmatic
    public void syncPlaylist(Display display, String path) {
        Runtime rt = Runtime.getRuntime();
        String[] syncCommand = createSyncSchedulePath(path, "sync", display);
        String[] scheduleCommand = createSyncSchedulePath(path, "schedule", display);

        try {
            Process p = rt.exec(syncCommand);
            p.waitFor();
            rt.exec(scheduleCommand);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Programmatic
    public String[] createSyncSchedulePath(String path, String task, Display display) {
        String[] rV = { path.concat("/scripts/watson"), display.getName(), task };
        return rV;
    }

    @Programmatic
    public void removePlaylists(Display display, String path) {

        String removePath = path.concat("/displays/").concat(display.getName()).concat("/playlists/");

        try {
            FileUtils.cleanDirectory(new File(removePath));
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    @Programmatic
    public void importAssetsAndCreatePlaylist() {
        final String path = properties.get("dscm.server.path");
        Publisher publisher = publishers.allPublishers().get(0);

        for (Playlist playlist : playlists.allPlaylists()) {
            playlist.removeAllItems();
        }

        for (Asset asset : assets.allAssets()) {
            asset.doRemove();
        }

        for (File file : filesForFolder(path.concat("/assets"))) {
            Asset asset = assets.findAssetByName(file.getName());
            if (asset == null) {
                try {
                    InputStream is;
                    is = new FileInputStream(file);
                    final String mimeType = new MimetypesFileTypeMap().getContentType(file);
                    Blob blob = new Blob(file.getName(), mimeType, IOUtils.toByteArray(is));
                    asset = assets.newAsset(blob, publisher, null, clockService.now(), null, null);
                    for (Playlist playlist : playlists.allPlaylists()) {
                        playlist.newItem(asset);
                    }
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    @Programmatic
    public List<File> filesForFolder(final String path) {
        return filesForFolder(new File(path));
    }

    private List<File> filesForFolder(final File folder) {
        List<File> fileList = new ArrayList<File>();
        for (final File file : folder.listFiles()) {
            if (file.isFile() && !file.isHidden()) {
                fileList.add(file);
            }
        }
        return fileList;
    }

    /**
     * The effective playlist items e.g. the commercials and fillers combined in
     * cycles of 60 seconds. All videos take 10 seconds each for the time being
     * 
     * @param playlist
     *            The main playlist e.g. commercial
     * @param dateTime
     *            The effective time
     * @return
     */
    @Programmatic
    public List<PlaylistItem> effectiveItems(Playlist playlist, LocalDateTime dateTime) {

        List<PlaylistItem> fillers = new ArrayList<PlaylistItem>();
        List<PlaylistItem> commercials = new ArrayList<PlaylistItem>();
        commercials.addAll(playlist.getItems());
        Playlist fillerPlaylist = playlists.findByDisplayGroupAndDateTimeAndType(
                playlist.getDisplayGroup(),
                dateTime.toLocalDate(),
                dateTime.toLocalTime(),
                PlaylistType.FILLERS);
        if (fillerPlaylist != null) {
            fillers.addAll(fillerPlaylist.getItems());
        }
        return PlaylistGenerator.generate(commercials, fillers, playlist.getLoopDuration());
    }

    @Programmatic
    public void writePlaylist(Display display, LocalDateTime dateTime, List<PlaylistItem> items, Runtime rt) {
        String filename = createPlaylistFilename(display, dateTime);
        try {
            File file = new File(filename);
            file.getParentFile().mkdirs();
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer;
            writer = new FileWriter(file);
            for (PlaylistItem item : items) {
                saveOriginAsset(item.getAsset());
                if (item.getAsset().getFile() != null) {
                    writer.write("../assets/".concat(item.getAsset().getFile().getName().concat("\n")));
                    saveDisplayAsset(display, item.getAsset(), rt);
                } else {
                    writer.write("../assets/".concat(item.getAsset().getName().concat(".broken\n")));
                }
            }
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void saveOriginAsset(Asset asset) {
        byte[] blobArray;
        File output = new File(properties.get("dscm.server.path").concat("/assets/" + asset.getName()));

        if (!output.isFile()) {
            output.getParentFile().mkdirs();
            if (asset.getFile() != null) {
                blobArray = asset.getFile().getBytes();

                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(output);
                    fos.write(blobArray);
                    fos.close();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                output = new File(properties.get("dscm.server.path").concat("/assets/" + asset.getName() + ".broken"));
                try {
                    output.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Creates a symbolic link to the personal folder of every playlist
    @Programmatic
    public void saveDisplayAsset(Display display, Asset asset, Runtime rt) {
        String origin = createOriginAssetFilename(asset);
        String destination = createAssetFilename(display, asset);

        File displayAssetFile = new File(destination);
        displayAssetFile.getParentFile().mkdirs();

        String[] execCommand = { "ln", "-s", origin, destination };

        try {
            rt.exec(execCommand);
        } catch (IOException IO) {
            // Print the stack trace in an error log
            File errLog = new File(destination.concat(".errorlog"));
            try {
                PrintStream ps = new PrintStream(errLog);
                IO.printStackTrace(ps);
                ps.close();
                errLog.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Programmatic
    public void removeDisplayAssets(Display display, String path) {
        String removePath = path.concat("/displays/").concat(display.getName()).concat("/assets/");

        try {
            FileUtils.cleanDirectory(new File(removePath));
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    @Programmatic
    public String createPlaylistFilename(Display display, LocalDateTime dateTime) {
        return properties.get("dscm.server.path")
                .concat("/displays")
                .concat("/" + display.getName())
                .concat("/playlists")
                .concat("/" + dateTime.toString("yyyyMMddHHmm"));
    }

    @Programmatic
    public String createAssetFilename(Display display, Asset asset) {
        return properties.get("dscm.server.path")
                .concat("/displays")
                .concat("/" + display.getName())
                .concat("/assets")
                .concat("/" + asset.getFile().getName());
    }

    @Programmatic
    public String createOriginAssetFilename(Asset asset) {
        return properties.get("dscm.server.path")
                .concat("/assets")
                .concat("/" + asset.getFile().getName());
    }

    // //////////////////////////////////////

    @Inject
    private Assets assets;

    @Inject
    private Publishers publishers;

    @Inject
    private ClockService clockService;

    @Inject
    private Playlists playlists;

    @Inject
    private DisplayGroups displayGroups;
    
    @Inject
    private BackgroundService backgroundService;

    @Override
    public void schedule(ActionInvocationMemento arg0, Command arg1, String arg2, String arg3, String arg4) {
        
    }
    
}