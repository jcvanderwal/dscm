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

import java.util.List;

import javax.inject.Inject;

import org.estatio.dscm.dom.PlaylistItem;
import org.estatio.dscm.dom.PlaylistItems;
import org.estatio.dscm.dom.Playlists;

import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;

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
        return playlistItems.allInstances();
    }

    @Render(Type.EAGERLY)
    public List<PlaylistItem> getAfternoonCommercials() {
        return playlistItems.allInstances();
    }

    @Render(Type.EAGERLY)
    public List<PlaylistItem> getMorningFillers() {
        return playlistItems.allInstances();
    }

    @Render(Type.EAGERLY)
    public List<PlaylistItem> getAfternoonFillers() {
        return playlistItems.allInstances();
    }

    // //////////////////////////////////////

    @Inject
    private Playlists playlists;

    @Inject
    private PlaylistItems playlistItems;

}
