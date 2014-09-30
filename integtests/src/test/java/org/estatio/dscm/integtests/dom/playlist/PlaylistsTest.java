/*
 *
. *  Copyright 2012-2014 Eurocommercial Properties NV
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
package org.estatio.dscm.integtests.dom.playlist;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import org.estatio.dscm.dom.display.DisplayGroup;
import org.estatio.dscm.dom.display.DisplayGroups;
import org.estatio.dscm.dom.playlist.Playlist;
import org.estatio.dscm.dom.playlist.PlaylistRepeat;
import org.estatio.dscm.dom.playlist.PlaylistType;
import org.estatio.dscm.dom.playlist.Playlists;
import org.estatio.dscm.dom.playlist.Time;
import org.estatio.dscm.fixture.playlist.PlaylistsAndItems;
import org.estatio.dscm.integtests.DscmIntegTest;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

//@Ignore("Causing (so far) inexplicable trouble with heap space on Jenkins, preventing deployment. See DSCM-20")
public class PlaylistsTest extends DscmIntegTest {

    @Inject
    private Playlists playlists;

    @Inject
    private DisplayGroups displayGroups;

    private DisplayGroup displayGroup;

    @BeforeClass
    public static void setupTransactionalData() {
        scenarioExecution().install(new PlaylistsAndItems());
    }

    @Before
    public void setUp() throws Exception {
        displayGroup = displayGroups.allDisplayGroups().get(0);
    }

    @Test
    public void findByStartDateAndSTartTimeAndType_happyCase() throws Exception {
        assertNotNull(playlists.findByDisplayGroupAndStartDateTimeAndType(displayGroup, new LocalDate(1980, 1, 1), new LocalTime("13:00"), PlaylistType.MAIN));
    }

    @Test
    public void findByStartDateAndSTartTimeAndType_nothingFound() throws Exception {
        assertNull(playlists.findByDisplayGroupAndStartDateTimeAndType(displayGroup, new LocalDate(1980, 1, 1), new LocalTime("14:00"), PlaylistType.MAIN));
    }

    @Test
    public void findByDateTimeAndType_happyCase() throws Exception {
        assertThat(playlistFor(new LocalDate(2014, 7, 14), new LocalTime("14:00")).getStartTime(), is(new LocalTime("13:00")));
        assertThat(playlistFor(new LocalDate(2014, 7, 14), new LocalTime("10:00")).getStartTime(), is(new LocalTime("08:00")));
    }

    @Test
    public void testCompareTo() throws Exception {
        Playlist mainPlayList = playlists.findByDisplayGroupAndDateTimeAndType(displayGroup, new LocalDate(1980, 1, 1), new LocalTime("13:00"), PlaylistType.MAIN);
        Playlist compareTo = playlists.newPlaylist(displayGroup, PlaylistType.FILLERS,
                mainPlayList.getStartDate(),
                Time.T1300,
                mainPlayList.getEndDate(),
                PlaylistRepeat.DAILY,
                mainPlayList.getLoopDuration());

        assertThat(mainPlayList.compareTo(compareTo), is(0));
    }

    private Playlist playlistFor(LocalDate date, LocalTime time) {
        return playlists.findByDisplayGroupAndDateTimeAndType(displayGroup, date, time, PlaylistType.FILLERS);
    }

}
