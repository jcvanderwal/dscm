package org.estatio.dscm.dom.playlist;

import org.hamcrest.Matchers;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class PlaylistRepeatTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void happyCase() throws Exception {
        assertThat(PlaylistRepeat.stringToPlaylistRepeat("RRULE:FREQ=DAILY"), Matchers.is(PlaylistRepeat.DAILY));
    }

    @Test
    public void dayOfWeekCompareRepeatRule() throws Exception {
        LocalDateTime monday = new LocalDateTime(2015, 4, 27, 7, 0, 0);
        assertThat(monday.dayOfWeek().getAsText().toUpperCase(), Matchers.is(PlaylistRepeat.MONDAY.title()));
        assertThat(monday.plusDays(1).dayOfWeek().getAsText().toUpperCase(), Matchers.is(PlaylistRepeat.TUESDAY.title()));
        assertThat(monday.plusDays(2).dayOfWeek().getAsText().toUpperCase(), Matchers.is(PlaylistRepeat.WEDNESDAY.title()));
        assertThat(monday.plusDays(3).dayOfWeek().getAsText().toUpperCase(), Matchers.is(PlaylistRepeat.THURSDAY.title()));
        assertThat(monday.plusDays(4).dayOfWeek().getAsText().toUpperCase(), Matchers.is(PlaylistRepeat.FRIDAY.title()));
        assertThat(monday.plusDays(5).dayOfWeek().getAsText().toUpperCase(), Matchers.is(PlaylistRepeat.SATURDAY.title()));
        assertThat(monday.plusDays(6).dayOfWeek().getAsText().toUpperCase(), Matchers.is(PlaylistRepeat.SUNDAY.title()));
        assertTrue(monday.dayOfWeek().getAsText().toUpperCase().equals(PlaylistRepeat.MONDAY.title()));
    }
}
