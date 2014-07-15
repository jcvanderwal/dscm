package org.estatio.dscm.services;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.estatio.dscm.dom.display.Display;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

public class SyncServiceTest_createFilename {

    private SyncService syncService;

    private Display display;

    @Before
    public void setUp() throws Exception {
        display = new Display();
        display.setName("display1");
        syncService = new SyncService();
        Map<String, String> properties = new HashMap<String, String>();
        // TODO: Use temp dir of system
        properties.put("dscm.server.path", "/path/prefix");
        syncService.init(properties);
    }

    @Test
    public void test() throws Exception {
        assertThat(syncService.createPlaylistFilename(display, new LocalDateTime(2014, 5, 1, 14, 0)),
                is("/path/prefix/displays/display1/playlists/201405011400"));
    }
}
