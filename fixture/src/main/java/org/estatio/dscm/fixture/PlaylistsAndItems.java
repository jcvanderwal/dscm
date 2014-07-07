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

package org.estatio.dscm.fixture;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;

import org.estatio.dscm.dom.asset.Asset;
import org.estatio.dscm.dom.asset.Assets;
import org.estatio.dscm.dom.display.DisplayGroups;
import org.estatio.dscm.dom.playlist.Playlist;
import org.estatio.dscm.dom.playlist.PlaylistItems;
import org.estatio.dscm.dom.playlist.PlaylistRepeat;
import org.estatio.dscm.dom.playlist.PlaylistType;
import org.estatio.dscm.dom.playlist.Playlists;
import org.estatio.dscm.dom.playlist.Time;

public class PlaylistsAndItems extends DiscoverableFixtureScript {

    public PlaylistsAndItems() {

        super(null, "playlistsAndItems");
    }

    @Override
    protected void execute(ExecutionContext executionContext) {

        execute(new DisplayGroupsAndDisplays(), executionContext);
        execute(new AssetFixture(), executionContext);

        create(Time.T0800, PlaylistType.MAIN, new BigDecimal(60));
        create(Time.T1300, PlaylistType.MAIN, new BigDecimal(60));
        create(Time.T0800, PlaylistType.FILLERS, BigDecimal.ZERO);
        create(Time.T1300, PlaylistType.FILLERS, BigDecimal.ZERO);

    }

    // //////////////////////////////////////

    private void create(Time time, PlaylistType type, BigDecimal loopDuration) {
        Playlist p1 = playlists.newPlaylist(
                displayGroups.allDisplayGroups().get(0),
                type,
                new LocalDate(1980, 1, 1),
                time,
                null,
                PlaylistRepeat.DAILY,
                loopDuration);
        for (Asset asset : assets.allAssets()) {
            playlistItems.newPlaylistItem(p1, asset);
        }
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    private Playlists playlists;

    @javax.inject.Inject
    private PlaylistItems playlistItems;

    @Inject
    private DisplayGroups displayGroups;

    @Inject
    private Assets assets;

}
