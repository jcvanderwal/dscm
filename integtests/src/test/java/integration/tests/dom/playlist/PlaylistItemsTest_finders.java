/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package integration.tests.dom.playlist;

import static org.junit.Assert.assertNotNull;
import integration.tests.DscmIntegTest;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.estatio.dscm.dom.asset.Assets;
import org.estatio.dscm.dom.playlist.PlaylistItems;
import org.estatio.dscm.dom.playlist.Playlists;
import org.estatio.dscm.fixture.DemoFixture;

public class PlaylistItemsTest_finders extends DscmIntegTest {

    @Inject
    private PlaylistItems items;
    
    @Inject
    private Assets assets;
    
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
    public void findByAsset() throws Exception {
        assertNotNull(items.findByAsset(assets.allAssets().get(0)));
    }

    @Test
    public void findByPlaylist() throws Exception {
        assertNotNull(items.findByPlaylist(playlists.allPlaylists().get(0)));
    }


}
