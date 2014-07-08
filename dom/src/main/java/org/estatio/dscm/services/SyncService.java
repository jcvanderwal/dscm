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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Blob;

import org.estatio.dscm.dom.asset.Asset;
import org.estatio.dscm.dom.asset.Assets;
import org.estatio.dscm.dom.display.Display;
import org.estatio.dscm.dom.playlist.Playlist;
import org.estatio.dscm.dom.playlist.PlaylistItem;
import org.estatio.dscm.dom.playlist.PlaylistType;
import org.estatio.dscm.dom.playlist.Playlists;
import org.estatio.dscm.dom.publisher.Publisher;
import org.estatio.dscm.dom.publisher.Publishers;

@DomainService
@Named("Administration")
public class SyncService {

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

    public void synchronizeNow() {
        final String path = properties.get("dscm.player.path");
        path.toLowerCase();

        for (Playlist playlist : playlists.allPlaylists()) {
            if (playlist.getType() == PlaylistType.MAIN) {
                for (Display display : playlist.getDisplayGroup().getDisplays()) {
                    for (LocalDateTime dateTime : playlist.nextOccurences(clockService.now().plusDays(7))) {
                        writePlaylist(display, dateTime, effectiveItems(playlist, dateTime));
                    }
                }
            }
        }
    }

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
    public List<PlaylistItem> effectiveItems(Playlist playlist, LocalDateTime dateTime) {
        List<PlaylistItem> items = new ArrayList<PlaylistItem>();
        List<PlaylistItem> fillers = new ArrayList<PlaylistItem>();

        BigDecimal commercialDuration, cycleDuration, timeLeftInCycle;

        cycleDuration = new BigDecimal(60);
        commercialDuration = playlist.getTotalDuration();

        // add commercial(s) to the effective playlist called items
        items.addAll(playlist.getItems());

        // finds all available fillers e.g. effective fillers
        Playlist fillerPlaylist = playlists.findByDisplayGroupAndStartDateTimeAndType(
                playlist.getDisplayGroup(),
                playlist.getStartDate(),
                playlist.getStartTime(),
                PlaylistType.FILLERS);

        // add available fillers to (play)list called fillers
        fillers.addAll(fillerPlaylist.getItems());

        /*
         * calculates time left in a cycle (also in case commercial is longer
         * than 60 sec0: timeLeftInCycle = 60 - (commercialDuration % 60)
         * MathContext mc = new MathContext(2); // precision of 2
         * timeLeftInCycle =
         * cycleDuration.subtract(commercialDuration.remainder(cycleDuration,
         * mc));
         */
        timeLeftInCycle = cycleDuration.subtract(commercialDuration);

        /*
         * If condition is 1, timeLeftInCycle is positive, if 0 timeLeftInCycle
         * is 0, if -1 timeLeftInCycle is negative
         */
        while (timeLeftInCycle.signum() >= 0) {
            /* if there's no time left in cycle and fillers unused */
            if (timeLeftInCycle == BigDecimal.ZERO && fillers.size() > 1) {
                /* TODO 1 cycle is filled, but there are fillers unused. 
                 * What to do?
                 */
            }
            /* if time left is bigger of equal than 10 seconds */
            if (timeLeftInCycle == BigDecimal.TEN) {
                for (int i = 0; i < fillers.size(); i++) {
                    if (fillers.get(i).getDuration() == BigDecimal.TEN) {
                        items.add(fillers.get(i));
                        fillers.remove(fillers.get(i));
                        timeLeftInCycle = timeLeftInCycle.subtract(fillers.get(i).getDuration());
                    }
                }
            }
            /* if time left is bigger than or equal to 20 seconds */
            if (timeLeftInCycle.compareTo(new BigDecimal("20")) >= 0) {
                for (int j = 0; j < fillers.size(); j++) {
                    if (fillers.get(j).getDuration().compareTo(new BigDecimal("20")) >= 0) {
                        items.add(fillers.get(j));
                        fillers.remove(fillers.get(j));
                        timeLeftInCycle = timeLeftInCycle.subtract(fillers.get(j).getDuration());
                    }
                }
            }
        }

        return items;
    }

    private void writePlaylist(Display display, LocalDateTime dateTime, List<PlaylistItem> items) {
        String filename = properties.get("dscm.server.path").concat("/displays").concat("/" + display.getName()).concat("/playlists").concat("/" + dateTime.toString("yyyyMMddhhmm"));
        try {
            File file = new File(filename);
            file.getParentFile().mkdirs();
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer;
            writer = new FileWriter(file);
            for (PlaylistItem item : items) {
                writer.write("asset/".concat(item.getAsset().getFile().getName().concat("\n")));
            }
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

}
