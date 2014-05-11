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
        playlist.clockService = new ClockService();

    }

    @Test
    public void happyCase() throws Exception {
        final List<LocalDateTime> nextOccurences = playlist.nextOccurences(new LocalDate(2014, 6, 1));
        assertThat(nextOccurences.size(), is(7));
    }
}
