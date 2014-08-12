package org.estatio.dscm.dom.playlist;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.services.clock.ClockService;

import org.estatio.dscm.dom.playlist.Playlist;

public class PlaylistTest_nextOccurences {

    private Playlist playlist;
    private Playlist playlist2;
    private Playlist playlist3;

    @Before
    public void setUp() throws Exception {
        playlist = new Playlist();
        playlist.setStartDate(new LocalDate(2014, 5, 1));
        playlist.setStartTime(new LocalTime("09:00"));
        playlist.setRepeatRule("RRULE:FREQ=DAILY");

        playlist2 = new Playlist();
        playlist2.setStartDate(new LocalDate(2014, 11, 8));
        playlist2.setStartTime(new LocalTime("07:00"));
        playlist2.setRepeatRule("RRULE:FREQ=DAILY");
        
        playlist3 = new Playlist();
        playlist3.setStartDate(new LocalDate(2014, 11, 8));
        playlist3.setStartTime(new LocalTime("14:00"));
        playlist3.setRepeatRule("RRULE:FREQ=DAILY");
    }

    @Test
    public void fullMonth() throws Exception {
        playlist.clockService = new ClockService() {
            @Override
            public LocalDate now() {
                return new LocalDate(2014, 5, 1);
            }
        };
        final List<LocalDateTime> nextOccurences = playlist.nextOccurences(new LocalDate(2014, 6, 1));
        assertThat(nextOccurences.size(), is(31));
    }

    @Test
    public void sameDay() throws Exception {
        playlist.clockService = new ClockService() {
            @Override
            public LocalDate now() {
                return new LocalDate(2014, 6, 1);
            }
        };
        final List<LocalDateTime> nextOccurences = playlist.nextOccurences(new LocalDate(2014, 6, 1));
        assertThat(nextOccurences.size(), is(1));
    }

    @Test
    public void futurePlaylist() throws Exception {
        playlist.clockService = new ClockService() {
            @Override
            public LocalDate now() {
                return new LocalDate(2014, 4, 1);
            }
        };
        final List<LocalDateTime> nextOccurences = playlist.nextOccurences(new LocalDate(2014, 4, 1));
        assertThat(nextOccurences.size(), is(0));
    }

}
