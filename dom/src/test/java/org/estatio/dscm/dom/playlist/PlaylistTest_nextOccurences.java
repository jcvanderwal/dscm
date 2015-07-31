package org.estatio.dscm.dom.playlist;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.clock.ClockService;
import org.estatio.dscm.dom.display.DisplayGroup;
import org.estatio.dscm.utils.CalendarUtils;
import org.joda.time.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PlaylistTest_nextOccurences {

    private Playlist playlist;
    private Playlist playlist2;

    @Before
    public void setUp() throws Exception {
        DisplayGroup testGroup = new DisplayGroup();
        testGroup.setName("Testgroup");

        playlist = new Playlist();
        playlist.setStartDate(new LocalDate(2014, 5, 1));
        playlist.setStartTime(new LocalTime("00:00"));
        playlist.setRepeatRule("RRULE:FREQ=DAILY;BYDAY=MO,TU,WE,TH,FR,SA,SU");
        playlist.setType(PlaylistType.MAIN);
        playlist.setDisplayGroup(testGroup);

        playlist2 = new Playlist();
        playlist2.setStartDate(new LocalDate(2015, 4, 1));
        playlist2.setStartTime(new LocalTime("14:00"));
        playlist2.setRepeatRule("RRULE:FREQ=DAILY;BYDAY=MO");
        playlist2.setType(PlaylistType.MAIN);
        playlist2.setDisplayGroup(testGroup);
    }

    @Test
    public void fullMonth() throws Exception {
        playlist.clockService = new ClockService() {
            @Override
            public LocalDate now() {
                return new LocalDate(2014, 5, 1);
            }
        };
        final List<Occurrence> nextOccurences = nextOccurences(new LocalDate(2014, 6, 1), playlist);
        assertThat(nextOccurences.toString(), nextOccurences.size(), is(31));
    }

    @Test
    public void sameDay() throws Exception {
        playlist.clockService = new ClockService() {
            @Override
            public LocalDate now() {
                return new LocalDate(2014, 6, 1);
            }
        };
        final List<Occurrence> nextOccurences = nextOccurences(new LocalDate(2014, 6, 1), playlist);
        assertThat(nextOccurences.toString(), nextOccurences.size(), is(1));
    }

    @Test
    public void futurePlaylist() throws Exception {
        playlist.clockService = new ClockService() {
            @Override
            public LocalDate now() {
                return new LocalDate(2014, 4, 1);
            }
        };
        final List<Occurrence> nextOccurences = nextOccurences(new LocalDate(2014, 4, 1), playlist);
        assertThat(nextOccurences.size(), is(0));
    }

    @Test
    public void difference() throws Exception {
        LocalDate one = new LocalDate(2014, 1, 1);
        LocalDate two = new LocalDate(2014, 1, 4);
        Period p = Period.fieldDifference(one, two);
        assertThat(p.getDays(), is(3));
    }

    @Test
    public void xxx() throws Exception {
        playlist2.clockService = new ClockService() {
            @Override
            public LocalDate now() {
                return new LocalDate(2015, 4, 1);
            }
        };
        final List<Occurrence> nextOccurrences = nextOccurences(new LocalDate(2015, 4, 8), playlist2);
        assertThat(nextOccurrences.toString(), nextOccurrences.size(), is(1));
    }

    @Programmatic
    public List<Occurrence> nextOccurences(LocalDate endDate, Playlist playlist) {
        List<Occurrence> nextOccurrences = new ArrayList<Occurrence>();

        final LocalDate start = playlist.getStartDate().isBefore(playlist.clockService.now()) ? playlist.clockService.now() : playlist.getStartDate();
        final LocalDate end = ObjectUtils.min(endDate, playlist.getEndDate());

        if (end.compareTo(start) >= 0 && end.compareTo(playlist.clockService.now()) >= 0) {
            List<Interval> intervals;
            intervals = CalendarUtils.intervalsInRange(
                    start,
                    end,
                    playlist.getRepeatRule());

            for (Interval interval : intervals) {
                LocalDateTime intervalStart = new LocalDateTime(interval.getStart());
                if (intervalStart.compareTo(start.toLocalDateTime(new LocalTime("00:00"))) >= 0) {
                    nextOccurrences.add(new Occurrence(
                            playlist.getType(),
                            new LocalDateTime(
                                    interval.getStartMillis()).
                                    withHourOfDay(playlist.getStartTime().getHourOfDay()).
                                    withMinuteOfHour(playlist.getStartTime().getMinuteOfHour()),
                            "Test title",
                            false
                    ));
                }
            }
        }

        return nextOccurrences;
    }
}
