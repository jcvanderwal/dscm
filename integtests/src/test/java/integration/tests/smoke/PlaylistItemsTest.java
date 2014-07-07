/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package integration.tests.smoke;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import integration.tests.DscmIntegTest;

import org.junit.Before;
import org.junit.Test;

import org.estatio.dscm.dom.asset.Asset;
import org.estatio.dscm.dom.asset.Assets;
import org.estatio.dscm.dom.playlist.Playlist;
import org.estatio.dscm.dom.playlist.PlaylistItem;
import org.estatio.dscm.dom.playlist.PlaylistItems;
import org.estatio.dscm.dom.playlist.Playlists;
import org.estatio.dscm.fixture.DemoFixture;

public class PlaylistItemsTest extends DscmIntegTest {

    private Playlists playlists;
    private PlaylistItems playlistItems;
    private Assets assets;

    @Before
    public void setUp() throws Exception {
        scenarioExecution().install(new DemoFixture());
        playlistItems = wrap(service(PlaylistItems.class));
        playlists = wrap(service(Playlists.class));
        assets = wrap(service(Assets.class));
    }

    @Test
    public void t1_listAll() throws Exception {
        assertThat(playlistItems.allPlaylistItems().size(), is(8));
    }

    @Test
    public void t2_new() throws Exception {

        Playlist playlist = playlists.allPlaylists().get(0);
        Asset asset = assets.allAssets().get(0);
        PlaylistItem lastItem = playlist.getItems().last();
        playlistItems.newPlaylistItem(playlist, asset);
        assertThat(playlist.getItems().last().getPrevious(), is(lastItem));

    }

}