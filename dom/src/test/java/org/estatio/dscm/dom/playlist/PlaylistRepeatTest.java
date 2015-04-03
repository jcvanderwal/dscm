package org.estatio.dscm.dom.playlist;

import static org.junit.Assert.assertThat;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

public class PlaylistRepeatTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void happyCase() throws Exception {
		assertThat(PlaylistRepeat.stringToPlaylistRepeat("RRULE:FREQ=DAILY"), Matchers.is(PlaylistRepeat.DAILY));

	}
}
