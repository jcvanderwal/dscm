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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.apache.isis.applib.clock.Clock;
import org.apache.isis.applib.fixtures.FixtureClock;
import org.apache.isis.applib.services.clock.ClockService;
import org.estatio.dscm.dom.display.DisplayGroup;
import org.estatio.dscm.dom.display.DisplayGroups;
import org.estatio.dscm.dom.playlist.*;
import org.estatio.dscm.fixture.playlist.PlaylistsAndItems;
import org.estatio.dscm.integtests.DscmIntegTest;
import org.isisaddons.wicket.fullcalendar2.cpt.applib.CalendarEventable;
import org.joda.time.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

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

    public void tearDown() throws Exception {
        ((FixtureClock) Clock.getInstance()).reset();
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
        ((FixtureClock) Clock.getInstance()).setDate(2014, 4, 1);

        List<Occurrence> nextOccurrences = playlist.nextOccurences(playlist.clockService.now().plusDays(7));

        assertThat(nextOccurrences.size(), is(7));
        assertThat(nextOccurrences.get(0).getDateTime(), is(new LocalDateTime(2014, 4, 1, 13, 0, 0, 0)));
        assertThat(nextOccurrences.get(1).getDateTime(), is(new LocalDateTime(2014, 4, 2, 13, 0, 0, 0)));
    }

    @Test
    public void testNextOccurrencesDaily() throws Exception {
        List<Playlist> playlistResults = playlists.findByDisplayGroupAndStartDateTimeAndType(displayGroup, new LocalDate(2015, 4, 1), new LocalTime("08:00"), PlaylistType.FILLERS);
        Playlist playlist = null;
        for (Playlist playlistLoop : playlistResults) {
            if (playlistLoop.getRepeatRule().equals("RRULE:FREQ=DAILY;BYDAY=MO")) {
                playlist = playlistLoop;
                break;
            }
        }

        assertThat(playlist.getRepeatRule(), is("RRULE:FREQ=DAILY;BYDAY=MO"));

        ((FixtureClock) Clock.getInstance()).setDate(2014, 4, 1);

        List<Occurrence> nextOccurences = playlist.nextOccurences(playlist.getStartDate().plusDays(7));

        assertThat(nextOccurences.size(), is(1));
        assertThat(nextOccurences.get(0).getDateTime(), is(new LocalDateTime(2015, 4, 6, 8, 0, 0, 0)));
    }

    @Test
    public void testEndPlaylist() throws Exception {
        // If current playlist ends and new one is created
        LocalDate newDate = new LocalDate().plusDays(5);
        BigDecimal newLoopDuration = new BigDecimal(60);
        Playlist oldPlaylist = playlists.findByDisplayGroupAndDateTimeAndType(displayGroup, new LocalDate(1980, 1, 1), new LocalTime("13:00"), PlaylistType.MAIN);
        Playlist newPlaylist = oldPlaylist.endAndCreateNewPlaylist(newDate);

        // Then assert old playlist end date
        assertThat(oldPlaylist.getEndDate(), is(newDate));

        // And assert new playlist start date
        assertThat(newPlaylist.getStartDate(), is(newDate));
        assertNull(newPlaylist.getEndDate());
        assertThat(newPlaylist.getStartTime(), is(oldPlaylist.getStartTime()));

        // And assert that next occurrences are correct
        List<Occurrence> nextOccurrences = newPlaylist.nextOccurences(newPlaylist.getStartDate().plusDays(7));
        assertEquals(nextOccurencesToString(nextOccurrences), newPlaylist.getNextOccurences());
    }

    @Test
    public void testCompareTo() throws Exception {
        Playlist mainPlayList = playlists.findByDisplayGroupAndDateTimeAndType(displayGroup, new LocalDate(1980, 1, 1), new LocalTime("13:00"), PlaylistType.MAIN);
        Playlist compareTo = playlists.newPlaylist(displayGroup, PlaylistType.FILLERS,
                mainPlayList.getStartDate(),
                Time.T1300,
                mainPlayList.getEndDate(),
                true,
                true,
                true,
                true,
                true,
                true,
                true);

        assertThat(mainPlayList.compareTo(compareTo), is(0));
    }

    @Test
    public void testGetCalendarEvents() throws Exception {
        Playlist playlist = playlistFor(new LocalDate(2014, 7, 14), new LocalTime("14:00"));
        ((FixtureClock) Clock.getInstance()).setDate(2014, 4, 1);
        ImmutableMap<String, List<? extends CalendarEventable>> calendarEvents = playlist.getCalendarEvents();

        assertNotNull(calendarEvents);
        assertFalse(calendarEvents.isEmpty());

        ImmutableSet<String> keys = calendarEvents.keySet();
        for (Iterator<String> it = keys.iterator(); it.hasNext();) {
            String s = it.next();
            if (s.equals(PlaylistType.FILLERS.title())) {
                assertTrue(s.equals(PlaylistType.FILLERS.title()));
                assertTrue(!calendarEvents.get(s).isEmpty());
                Occurrence occ = (Occurrence)calendarEvents.get(s).get(0);
                assertThat(occ.getDateTime(), is(new LocalDateTime(2014, 4, 1, 13, 0, 0, 0)));
                assertThat(occ.toCalendarEvent().getDateTime(), is(new DateTime(2014, 4, 1, 13, 0, 0, 0)));
                Occurrence occ2 = (Occurrence)calendarEvents.get(s).get(1);
                assertThat(occ2.getDateTime(), is (new LocalDateTime(2014, 4, 2, 13, 0, 0, 0)));
                assertThat(occ2.toCalendarEvent().getDateTime(), is(new DateTime(2014, 4, 2, 13, 0, 0, 0)));
            }
        }
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

    private String nextOccurencesToString(List<Occurrence> nextOccurrences) {
        StringBuilder builder = new StringBuilder();
        for (Occurrence occurence : nextOccurrences) {
            LocalDateTime dateTime = occurence.getDateTime();
            builder.append(dateTime.toString("E\tdd-MM-yyyy\tHH:mm"));
            builder.append("\n");
        }

        return builder.toString();
    }

}
