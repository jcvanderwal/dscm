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

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
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
import org.estatio.dscm.fixture.asset.AssetForCommercial;
import org.estatio.dscm.fixture.asset.AssetForFiller;
import org.estatio.dscm.fixture.playlist.PlaylistsAndItems;
import org.estatio.dscm.integtests.DscmIntegTest;
import org.estatio.dscm.services.SyncService;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

    public class SyncServiceTest extends DscmIntegTest {

    @Inject
    SyncService syncService;

    @Inject
    Playlists playlists;

    @Inject
    DisplayGroups displayGroups;

    Playlist commercialPlaylist;

    @BeforeClass
    public static void setupTransactionalData() {
        scenarioExecution().install(new DemoFixture());
    }

    @Before
    public void setUp() throws Exception {
        final DisplayGroup displayGroup = displayGroups.allDisplayGroups().get(0);
        List<Playlist> playlistResults = playlists.findByDisplayGroupAndStartDateTimeAndType(
                displayGroup,
                PlaylistsAndItems.DATE,
                PlaylistsAndItems.MORNING.time(),
                PlaylistType.MAIN);
        assertThat(playlistResults.size(), is(1));
        commercialPlaylist = playlistResults.get(0);
    }

    public static class EffectiveItems extends SyncServiceTest {

        @Test
        public void effectiveItems() throws Exception {
            final List<PlaylistItem> effectiveItems =
                    syncService.effectiveItems(
                            commercialPlaylist,
                            new LocalDate(1980, 1, 1).toLocalDateTime(PlaylistsAndItems.MORNING.time()));
            // Each asset is 10 seconds so we should have 6
            assertThat(effectiveItems.size(), is(6));
            // The commercial and filler are distributed evenly

            System.out.println("Commercial: " + AssetForCommercial.NAME);
            System.out.println("Filler: " + AssetForFiller.NAME);
            System.out.println(" ");
            for (int i = 0; i < effectiveItems.size(); i++) {
                System.out.println(effectiveItems.get(0).getAsset().getName());
            }

            assertThat(effectiveItems.get(0).getAsset().getName(), is(AssetForCommercial.NAME));
            assertThat(effectiveItems.get(1).getAsset().getName(), is(AssetForCommercial.NAME));
            assertThat(effectiveItems.get(2).getAsset().getName(), is(AssetForCommercial.NAME));
            assertThat(effectiveItems.get(3).getAsset().getName(), is(AssetForCommercial.NAME));
            assertThat(effectiveItems.get(4).getAsset().getName(), is(AssetForFiller.NAME));
            assertThat(effectiveItems.get(5).getAsset().getName(), is(AssetForFiller.NAME));
        }

        @Test
        public void findByDisplayGroupAndDateTimeAndTypeReturnsNull() throws Exception {
            final DisplayGroup displayGroup = new DisplayGroup();
            displayGroup.setName("Testgroup");
            LocalDate date = new LocalDate(2010, 7, 14);
            LocalTime time = new LocalTime("14:00");
            PlaylistType type = PlaylistType.MAIN;
            assertNull("RV is not null: ", playlists.findByDisplayGroupAndDateTimeAndType(displayGroup, date, time, type));
        }

    }
}
