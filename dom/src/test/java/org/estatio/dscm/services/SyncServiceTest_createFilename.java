package org.estatio.dscm.services;

import org.estatio.dscm.dom.display.Display;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

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
        properties.put("dscm.server.path", "/var/dscm");
        syncService.init(properties);
    }

    @Test
    public void test14() throws Exception {
        assertThat(syncService.createPlaylistFilename(display, new LocalDateTime(2014, 5, 1, 14, 0)),
                is("/var/dscm/displays/display1/playlists/201405011400"));
    }

    @Test
    public void syncPlaylistTest() throws Exception {
        assertArrayEquals(syncService.createSyncSchedulePath(syncService.getProperties().get("dscm.server.path"),"sync", display), new String[]{"/var/dscm/scripts/watson", "display1", "sync"});
        assertArrayEquals(syncService.createSyncSchedulePath(syncService.getProperties().get("dscm.server.path"),"schedule", display), new String[]{"/var/dscm/scripts/watson", "display1", "schedule"});
    }
}
