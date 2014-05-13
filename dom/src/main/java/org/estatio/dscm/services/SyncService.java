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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Blob;

import org.estatio.dscm.dom.Asset;
import org.estatio.dscm.dom.Assets;
import org.estatio.dscm.dom.Display;
import org.estatio.dscm.dom.Playlist;
import org.estatio.dscm.dom.PlaylistItem;
import org.estatio.dscm.dom.Playlists;
import org.estatio.dscm.dom.Publisher;
import org.estatio.dscm.dom.Publishers;

@Named("Administration")
public class SyncService {

    private Map<String, String> properties;

    @Programmatic
    @PostConstruct
    public void init(final Map<String, String> properties) {
        this.properties = properties;
    }

    public void synchronizeNow() {
        final String path = properties.get("dscm.player.path");
        path.toLowerCase();

        // copy assets
        // remove all playlists

        for (Playlist playlist : playlists.allPlaylists()) {
            for (Display display : playlist.getDisplayGroup().getDisplays()) {
                writePlaylist(display, playlist);
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

    private void writePlaylist(Display display, Playlist playlist) {
        for (LocalDateTime time : playlist.nextOccurences(null)) {
            String filename = properties.get("dscm.server.path")
                    .concat("/displays")
                    .concat("/" + display.getName())
                    .concat("/playlists")
                    .concat("/" + time.toString("yyyyMMddhhmm"));
            File file = new File(filename);
            file.getParentFile().mkdirs();
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileWriter writer;
                writer = new FileWriter(file);
                for (PlaylistItem item : playlist.getItems()) {
                    writer.write("asset/".concat(item.getAsset().getFile().getName().concat("\n")));
                }
                writer.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
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
