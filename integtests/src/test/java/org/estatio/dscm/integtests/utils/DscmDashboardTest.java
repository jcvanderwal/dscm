package org.estatio.dscm.integtests.utils;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.estatio.dscm.dom.playlist.Playlists;
import org.estatio.dscm.fixture.DemoFixture;
import org.estatio.dscm.integtests.DscmIntegTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DscmDashboardTest extends DscmIntegTest {

    @Inject
    private Playlists playlists;

    @BeforeClass
    public static void setupTransactionalData() {
        scenarioExecution().install(new DemoFixture());

    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void findAllActive() throws Exception {
        assertThat(playlists.findAllActive(new LocalDate(2014, 1, 1)).size(), is(5));

    }

}
