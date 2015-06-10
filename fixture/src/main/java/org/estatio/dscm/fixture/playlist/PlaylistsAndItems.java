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

package org.estatio.dscm.fixture.playlist;

import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;
import org.estatio.dscm.dom.asset.Assets;
import org.estatio.dscm.dom.display.DisplayGroup;
import org.estatio.dscm.dom.display.DisplayGroups;
import org.estatio.dscm.dom.playlist.*;
import org.estatio.dscm.fixture.asset.AssetForCommercial;
import org.estatio.dscm.fixture.asset.AssetForFiller;
import org.estatio.dscm.fixture.display.DisplayGroupsAndDisplays;
import org.joda.time.LocalDate;

import javax.inject.Inject;
import java.math.BigDecimal;

public class PlaylistsAndItems extends DiscoverableFixtureScript {

    public static final LocalDate DATE = new LocalDate(1980, 1, 1);

    public static final Time AFTERNOON = Time.T1300;

    public static final Time MORNING = Time.T0800;

    @Override
    protected void execute(ExecutionContext executionContext) {

        execute(new DisplayGroupsAndDisplays(), executionContext);
        execute(new AssetForFiller(), executionContext);
        execute(new AssetForCommercial(), executionContext);

        create(MORNING, PlaylistType.MAIN, new BigDecimal(60), AssetForCommercial.NAME, true, true, true, true, true, true, true, DATE);
        create(AFTERNOON, PlaylistType.MAIN, new BigDecimal(60), AssetForCommercial.NAME, true, true, true, true, true, true, true, DATE);
        create(MORNING, PlaylistType.FILLERS, BigDecimal.ZERO, AssetForFiller.NAME, true, true, true, true, true, true, true, DATE);
        create(AFTERNOON, PlaylistType.FILLERS, BigDecimal.ZERO, AssetForFiller.NAME, true, true, true, true, true, true, true, DATE);
        create(MORNING, PlaylistType.FILLERS, BigDecimal.ZERO, AssetForFiller.NAME, true, false, false, false, false, false, false, new LocalDate(2015, 4, 1));
    }

    // //////////////////////////////////////

    private void create(
            Time time,
            PlaylistType type,
            BigDecimal loopDuration,
            String assetName,
            boolean monday,
            boolean tuesday,
            boolean wednesday,
            boolean thursday,
            boolean friday,
            boolean saturday,
            boolean sunday,
            LocalDate playlistDate) {
        for (DisplayGroup displayGroup : displayGroups.allDisplayGroups()) {
            Playlist p1 = playlists.newPlaylist(
                    displayGroup,
                    type,
                    playlistDate,
                    time,
                    null,
                    monday,
                    tuesday,
                    wednesday,
                    thursday,
                    friday,
                    saturday,
                    sunday);
            playlistItems.newPlaylistItem(p1, assets.findAssetByName(assetName));
            break;
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
