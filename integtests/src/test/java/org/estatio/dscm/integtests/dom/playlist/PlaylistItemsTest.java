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
package org.estatio.dscm.integtests.dom.playlist;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.estatio.dscm.dom.asset.Asset;
import org.estatio.dscm.dom.asset.Assets;
import org.estatio.dscm.dom.playlist.Playlist;
import org.estatio.dscm.dom.playlist.PlaylistItem;
import org.estatio.dscm.dom.playlist.PlaylistItems;
import org.estatio.dscm.dom.playlist.Playlists;
import org.estatio.dscm.fixture.DemoFixture;
import org.estatio.dscm.integtests.DscmIntegTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PlaylistItemsTest extends DscmIntegTest {

    @Inject
    Playlists playlists;

    @Inject
    PlaylistItems playlistItems;

    @Inject
    Assets assets;

    @Before
    public void setUp() throws Exception {
        scenarioExecution().install(new DemoFixture());
    }

    public static class AllPlaylistItems extends PlaylistItemsTest {

        @Test
        public void sizeDoesMatter() throws Exception {
            assertThat(playlistItems.allPlaylistItems().size(), is(5));
        }

    }

    public static class NewPlaylistItem extends PlaylistItemsTest {

        @Test
        public void createdSuccesfully() throws Exception {

            Playlist playlist = playlists.allPlaylists().get(0);
            Asset asset = assets.allAssets().get(0);
            PlaylistItem lastItem = playlist.getItems().last();
            playlistItems.newPlaylistItem(playlist, asset);
            assertThat(playlist.getItems().last().getPrevious(), is(lastItem));

        }

    }

}