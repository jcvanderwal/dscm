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
package integration.tests.services;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import integration.tests.DscmIntegTest;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.estatio.dscm.dom.display.DisplayGroup;
import org.estatio.dscm.dom.display.DisplayGroups;
import org.estatio.dscm.dom.playlist.Playlist;
import org.estatio.dscm.dom.playlist.PlaylistItem;
import org.estatio.dscm.dom.playlist.PlaylistType;
import org.estatio.dscm.dom.playlist.Playlists;
import org.estatio.dscm.fixture.DemoFixture;
import org.estatio.dscm.fixture.playlist.PlaylistsAndItems;
import org.estatio.dscm.services.SyncService;

public class SyncServiceTest_effectiveItems extends DscmIntegTest {

    @Inject
    private SyncService syncService;

    @Inject
    private Playlists playlists;

    @Inject
    private DisplayGroups displayGroups;

    private Playlist commercialPlaylist;

    private Playlist fillerPlaylist;

    @BeforeClass
    public static void setupTransactionalData() {
        scenarioExecution().install(new DemoFixture());
    }

    @Before
    public void setUp() throws Exception {
        final DisplayGroup displayGroup = displayGroups.allDisplayGroups().get(0);
        commercialPlaylist = playlists.findByDisplayGroupAndStartDateTimeAndType(
                displayGroup,
                PlaylistsAndItems.DATE,
                PlaylistsAndItems.MORNING.time(),
                PlaylistType.MAIN);
        fillerPlaylist = playlists.findByDisplayGroupAndType(
                displayGroup,
                PlaylistType.FILLERS).get(0);
    }

    @Test
    public void effectiveItems() throws Exception {
        final List<PlaylistItem> effectiveItems =
                syncService.effectiveItems(
                        commercialPlaylist,
                        new LocalDate(2014, 5, 1).toLocalDateTime(PlaylistsAndItems.MORNING.time()));
        // Each asset is 10 seconds so we should have six
        assertThat(effectiveItems.size(), is(2));
        // The commercial and filler are distributed evenly
        assertThat(effectiveItems.get(0), is(commercialPlaylist.getItems().first()));
        assertThat(effectiveItems.get(1), is(fillerPlaylist.getItems().first()));

    }
}
