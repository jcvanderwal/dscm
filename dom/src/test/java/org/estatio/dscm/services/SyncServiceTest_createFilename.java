package org.estatio.dscm.services;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.estatio.dscm.dom.display.Display;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

public class SyncServiceTest_createFilename {

    private Display display;

    @Before
    public void setUp() throws Exception {
        display = new Display();
        display.setName("display1");
    }

    @Test
    public void test() throws Exception {
        assertThat(SyncService.createFilename("/path/prefix", display, new LocalDateTime(2014, 5, 1, 14, 0)), 
                is("/path/prefix/displays/display1/playlists/201405011400"));
    }
}
