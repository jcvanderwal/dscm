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
package org.estatio.dscm.integtests.services;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.estatio.dscm.dom.display.Display;
import org.estatio.dscm.dom.display.DisplayGroup;
import org.estatio.dscm.dom.display.DisplayGroups;
import org.estatio.dscm.dom.playlist.PlaylistItem;
import org.estatio.dscm.dom.playlist.PlaylistType;
import org.estatio.dscm.dom.playlist.Playlists;
import org.estatio.dscm.fixture.DemoFixture;
import org.estatio.dscm.fixture.asset.AssetForCommercial;
import org.estatio.dscm.fixture.asset.AssetForFiller;
import org.estatio.dscm.integtests.DscmIntegTest;
import org.estatio.dscm.services.SyncService;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SyncServiceTest_writePlaylist extends DscmIntegTest {

    @Inject
    private SyncService syncService;

    @Inject
    private Playlists playlists;

    @Inject
    private DisplayGroups displayGroups;

    @BeforeClass
    public static void setupTransactionalData() {
        scenarioExecution().install(new DemoFixture());
    }

    @Before
    public void setUp() throws Exception {
        Map<String, String> properties = new HashMap<String, String>();
        // TODO: Use temp dir of system
        properties.put("dscm.player.path", "/tmp/dscm/player");
        properties.put("dscm.server.path", "/tmp/dscm/server");
        syncService.init(properties);
    }

    @Test
    public void testFileExists() throws Exception {

        // given
        DisplayGroup displayGroup = displayGroups.allDisplayGroups().get(0);
        Display display = displayGroup.getDisplays().first();
        List<PlaylistItem> itemsList = new ArrayList<PlaylistItem>();
        itemsList.addAll(playlists.findByDisplayGroupAndType(displayGroup, PlaylistType.MAIN).get(0).getItems());
        itemsList.addAll(playlists.findByDisplayGroupAndType(displayGroup, PlaylistType.FILLERS).get(0).getItems());
        LocalDateTime dateTime = new LocalDateTime(2014, 5, 1, 14, 0);
        
        // when
        syncService.writePlaylist(display, dateTime, itemsList, Runtime.getRuntime());

        // then the file exists
        String fileName = syncService.createPlaylistFilename(display, dateTime);
        File file = new File(fileName);
        assertTrue("Not found: " + fileName, file.exists());
        // and the file has two rows corresponding the current fixtures
        FileReader namereader = new FileReader(file);
        BufferedReader in = new BufferedReader(namereader);
        String first = "";
        String second = "";
        int nextChar = in.read();
        while (nextChar != -1 && (char)nextChar != '\n' && (char)nextChar != '\r') {
            first = first + String.valueOf((char)nextChar);
            nextChar = in.read();
        }
        nextChar = in.read();
        while (nextChar != -1 && (char)nextChar != '\n' && (char)nextChar != '\r') {
            second = second + String.valueOf((char)nextChar);
            nextChar = in.read();
        }
        
        assertThat(first, is("../assets/" + AssetForCommercial.NAME));
        assertThat(second, is("../assets/" + AssetForFiller.NAME));
        in.close();
        namereader.close();

        // now confirm if assets exist
        for (PlaylistItem item : itemsList) {
            File origin = new File(syncService.createOriginAssetFilename(item.getAsset()));
            assertTrue(origin.getPath(), origin.isFile());
            File target = new File(syncService.createAssetFilename(display, item.getAsset()));
            assertTrue(target.getPath(), target.isFile());
        }

    }

}
