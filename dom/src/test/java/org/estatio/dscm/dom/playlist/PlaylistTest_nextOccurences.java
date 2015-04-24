package org.estatio.dscm.dom.playlist;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.services.clock.ClockService;

public class PlaylistTest_nextOccurences {

    private Playlist playlist;
    private Playlist playlist2;

    @Before
    public void setUp() throws Exception {
        playlist = new Playlist();
        playlist.setStartDate(new LocalDate(2014, 5, 1));
        playlist.setStartTime(new LocalTime("00:00"));
        playlist.setRepeatRule("RRULE:FREQ=DAILY");

        playlist2 = new Playlist();
        playlist2.setStartDate(new LocalDate(2015, 4, 1));
        playlist2.setStartTime(new LocalTime("14:00"));
        playlist2.setRepeatRule(PlaylistRepeat.MONDAY.rrule());
    }

    @Test
    public void fullMonth() throws Exception {
        playlist.clockService = new ClockService() {
            @Override
            public LocalDate now() {
                return new LocalDate(2014, 5, 1);
            }
        };
        final List<LocalDateTime> nextOccurences = playlist.nextOccurences(new LocalDate(2014, 6, 1), true);
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
        final List<LocalDateTime> nextOccurences = playlist.nextOccurences(new LocalDate(2014, 6, 1), true);
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
        final List<LocalDateTime> nextOccurences = playlist.nextOccurences(new LocalDate(2014, 4, 1), true);
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
        final List<LocalDateTime> nextOccurrences = playlist2.nextOccurences(new LocalDate(2015, 4, 8), true);
        assertThat(nextOccurrences.toString(), nextOccurrences.size(), is(1));
    }
}
