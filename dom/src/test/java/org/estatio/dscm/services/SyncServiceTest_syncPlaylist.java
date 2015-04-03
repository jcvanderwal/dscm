package org.estatio.dscm.services;

import org.junit.Before;
import org.junit.Test;

import org.estatio.dscm.dom.display.Display;

public class SyncServiceTest_syncPlaylist {

    @Before
    public void setUp() throws Exception {
        
    }

    @Test
    public void test() {
        SyncService syncServ = new SyncService();
        Display display = new Display();
        display.setName("Display01");
        syncServ.syncPlaylist(display, "/var/dscm");
    }

}
