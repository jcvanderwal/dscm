package org.estatio.dscm.dom;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.services.clock.ClockService;

public class PlaylistTest_nextOccurences {

    private Playlist playlist;

    @Before
    public void setUp() throws Exception {
        playlist = new Playlist();
        playlist.setStartDate(new LocalDate(2014, 5, 1));
        playlist.setStartTime(new LocalTime("09:00"));
        playlist.setRepeatRule("RRULE:FREQ=DAILY");

    }

    @Test
    public void happyCase() throws Exception {
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
    public void happyCase2() throws Exception {
        playlist.clockService = new ClockService() {
            @Override
            public LocalDate now() {
                return new LocalDate(2014, 6, 1);
            }
        };
        final List<LocalDateTime> nextOccurences = playlist.nextOccurences(new LocalDate(2014, 6, 1));
        assertThat(nextOccurences.size(), is(1));
    }

}
