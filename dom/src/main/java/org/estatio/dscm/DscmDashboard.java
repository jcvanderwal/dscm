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
package org.estatio.dscm;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalTime;

import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;

import org.estatio.dscm.dom.display.DisplayGroups;
import org.estatio.dscm.dom.playlist.Playlist;
import org.estatio.dscm.dom.playlist.PlaylistConstants;
import org.estatio.dscm.dom.playlist.PlaylistItem;
import org.estatio.dscm.dom.playlist.PlaylistItems;
import org.estatio.dscm.dom.playlist.PlaylistType;
import org.estatio.dscm.dom.playlist.Playlists;

@Named("Dashboard")
public class DscmDashboard extends EstatioViewModel {

    public String title() {
        return "Dashboard";
    }

    public String iconName() {
        return "Dashboard";
    }

    // //////////////////////////////////////

    @Override
    public void viewModelInit(final String memento) {
    }

    @Override
    public String viewModelMemento() {
        return "dashboard";
    }

    // //////////////////////////////////////

    @Render(Type.EAGERLY)
    public List<PlaylistItem> getMorningCommercials() {
        return findItems(PlaylistConstants.MORNING_START_TIME, PlaylistType.MAIN);
    }

    @Render(Type.EAGERLY)
    public List<PlaylistItem> getAfternoonCommercials() {
        return findItems(PlaylistConstants.AFTERNOON_START_TIME, PlaylistType.MAIN);
    }

    @Render(Type.EAGERLY)
    public List<PlaylistItem> getMorningFillers() {
        return findItems(PlaylistConstants.MORNING_START_TIME, PlaylistType.FILLERS);
    }

    @Render(Type.EAGERLY)
    public List<PlaylistItem> getAfternoonFillers() {
        return findItems(PlaylistConstants.AFTERNOON_START_TIME, PlaylistType.FILLERS);
    }

    // //////////////////////////////////////
    
    private List<PlaylistItem> findItems(final LocalTime startTime, final PlaylistType type) {
        final Playlist playlist = playlists.findByStartDateAndStartTimeAndType(displayGroups.allDisplayGroups().get(0), PlaylistConstants.START_DATE, startTime, type);
        if (playlist == null) {
            return new ArrayList<PlaylistItem>();
        }
        return playlistItems.findByPlaylist(playlist);
    }

    // //////////////////////////////////////

    @Inject
    private Playlists playlists;

    @Inject
    private PlaylistItems playlistItems;

    @Inject
    private DisplayGroups displayGroups;

}
