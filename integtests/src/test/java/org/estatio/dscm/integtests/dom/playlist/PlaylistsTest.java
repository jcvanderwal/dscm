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

import org.apache.isis.applib.services.clock.ClockService;
import org.estatio.dscm.dom.display.DisplayGroup;
import org.estatio.dscm.dom.display.DisplayGroups;
import org.estatio.dscm.dom.playlist.*;
import org.estatio.dscm.fixture.playlist.PlaylistsAndItems;
import org.estatio.dscm.integtests.DscmIntegTest;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

//@Ignore("Causing (so far) inexplicable trouble with heap space on Jenkins, preventing deployment. See DSCM-20")
public class PlaylistsTest extends DscmIntegTest {

    @Inject
    private Playlists playlists;

    @Inject
    private DisplayGroups displayGroups;

    private DisplayGroup displayGroup;

    @Inject
    ClockService clockService;

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
        assertEquals(playlists.findByDisplayGroupAndStartDateTimeAndType(displayGroup, new LocalDate(1980, 1, 1), new LocalTime("14:00"), PlaylistType.MAIN), new ArrayList<Playlist>());
    }

    @Test
    public void findByDateTimeAndType_happyCase() throws Exception {
        assertThat(playlistFor(new LocalDate(2014, 7, 14), new LocalTime("14:00")).getStartTime(), is(new LocalTime("13:00")));
        assertThat(playlistFor(new LocalDate(2014, 7, 14), new LocalTime("10:00")).getStartTime(), is(new LocalTime("08:00")));
    }

    @Test
    public void testNextOccurrences() throws Exception {
        Playlist playlist = playlistFor(new LocalDate(2014, 7, 14), new LocalTime("14:00"));
        List<LocalDateTime> nextOccurrences = generateNextOccurrencesForTests(playlist.getStartDate(), playlist.getStartTime(), playlist.getEndDate());

        assertThat(nextOccurrences.size(), is(7));
        assertThat(nextOccurrences.get(0), is(new LocalDateTime(1980, 1, 1, 13, 0, 0, 0)));
        assertThat(nextOccurrences.get(1), is(new LocalDateTime(1980, 1, 2, 13, 0, 0, 0)));
    }

    @Test
    public void testNextOccurrencesDaily() throws Exception {
        List<Playlist> playlistResults = playlists.findByDisplayGroupAndStartDateTimeAndType(displayGroup, new LocalDate(2015, 4, 1), new LocalTime("08:00"), PlaylistType.FILLERS);
        Playlist playlist = null;
        for (Playlist playlistLoop : playlistResults) {
            if (playlistLoop.getRepeatRule().equals(PlaylistRepeat.MONDAY.rrule())) {
                playlist = playlistLoop;
                break;
            }
        }

        assertThat(PlaylistRepeat.stringToPlaylistRepeat(playlist.getRepeatRule()), is(PlaylistRepeat.MONDAY));

        playlist.clockService = new ClockService() {
            @Override
            public LocalDate now() {
                return new LocalDate(2014, 4, 1);
            }
        };

        List<LocalDateTime> nextOccurences = playlist.nextOccurences(playlist.getStartDate().plusDays(7), false);

        assertThat(nextOccurences.size(), is(1));
        assertThat(nextOccurences.get(0), is(new LocalDateTime(2015, 4, 6, 8, 0, 0, 0)));
    }

    @Test
    public void testOverlappingPlaylists() throws Exception {
        List<Playlist> playlistResults = playlists.findByDisplayGroupAndStartDateTimeAndType(displayGroup, new LocalDate(1980, 1, 1), new LocalTime("08:00"), PlaylistType.FILLERS);
        Playlist playlist = playlistResults.get(0);
        playlist.clockService = new ClockService() {
            @Override
            public LocalDate now() {
                return new LocalDate(2015, 4, 25);
            }
        };
        List<LocalDateTime> nextOccurrences = playlist.nextOccurences(playlist.clockService.now().plusDays(7), false);
        assertThat(nextOccurrences.size(), is(6));
    }

    @Test
    public void testEndPlaylist() throws Exception {
        // If current playlist ends and new one is created
        LocalDate newDate = new LocalDate().plusDays(5);
        BigDecimal newLoopDuration = new BigDecimal(60);
        Playlist oldPlaylist = playlists.findByDisplayGroupAndDateTimeAndType(displayGroup, new LocalDate(1980, 1, 1), new LocalTime("13:00"), PlaylistType.MAIN);
        Playlist newPlaylist = oldPlaylist.endAndCreateNewPlaylist(newDate, newLoopDuration);

        // Then assert old playlist end date
        assertThat(oldPlaylist.getEndDate(), is(newDate));

        // And assert new playlist start date
        assertThat(newPlaylist.getStartDate(), is(newDate));
        assertNull(newPlaylist.getEndDate());
        assertThat(newPlaylist.getStartTime(), is(oldPlaylist.getStartTime()));

        // And assert that next occurrences are correct
        List<LocalDateTime> nextOccurrences = generateNextOccurrencesForTests(newPlaylist.getStartDate(), newPlaylist.getStartTime(), newPlaylist.getEndDate());
        assertEquals(nextOccurencesToString(nextOccurrences), newPlaylist.getNextOccurences());
    }

    @Test
    public void testCompareTo() throws Exception {
        Playlist mainPlayList = playlists.findByDisplayGroupAndDateTimeAndType(displayGroup, new LocalDate(1980, 1, 1), new LocalTime("13:00"), PlaylistType.MAIN);
        Playlist compareTo = playlists.newPlaylist(displayGroup, PlaylistType.FILLERS,
                mainPlayList.getStartDate(),
                Time.T1300,
                mainPlayList.getEndDate(),
                PlaylistRepeat.DAILY);

        assertThat(mainPlayList.compareTo(compareTo), is(0));
    }

    private Playlist playlistFor(LocalDate date, LocalTime time) {
        return playlists.findByDisplayGroupAndDateTimeAndType(displayGroup, date, time, PlaylistType.FILLERS);
    }

    private List<LocalDateTime> generateNextOccurrencesForTests(LocalDate startDate, LocalTime startTime, LocalDate endDate) {
        List<LocalDateTime> nextOccurrences = new ArrayList<LocalDateTime>();
        int max = 7;

        if (endDate != null) {
            max = Period.fieldDifference(startDate, endDate).getDays() < 7 ? Period.fieldDifference(startDate, endDate).getDays() : 7;
        }

        for (int i = 0; i < max; i++) {
            nextOccurrences.add(startDate.plusDays(i).toLocalDateTime(startTime));
        }

        return nextOccurrences;
    }

    private String nextOccurencesToString(List<LocalDateTime> nextOccurrences) {
        StringBuilder builder = new StringBuilder();
        for (LocalDateTime occurence : nextOccurrences) {
            builder.append(occurence.toString("yyyy-MM-dd HH:mm"));
            builder.append("\n");
        }

        return builder.toString();
    }
}
