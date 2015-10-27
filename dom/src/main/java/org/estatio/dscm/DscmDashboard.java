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

import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.clock.ClockService;

import org.estatio.dscm.dom.playlist.Occurrence;
import org.estatio.dscm.dom.playlist.Playlist;
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

    @MemberOrder(sequence = "1")
    @CollectionLayout(render = RenderType.EAGERLY, hidden = Where.ANYWHERE)
    public List<Playlist> getActivePlaylists() {
        return playlists.findAllActive(clockService.now());
    }

    @MemberOrder(sequence = "2")
    @CollectionLayout(named = "Schedule For Today", render = RenderType.EAGERLY, sortedBy = Occurrence.OccurrencesComparator.class)
    public List<Occurrence> getSchedule() {
        List<Occurrence> occ =  new ArrayList<>();
        for (Playlist pl : getActivePlaylists()){
            occ.addAll(pl.nextOccurrences(clockService.now().plusDays(1)));
        }
        return occ;
    }
    @MemberOrder(sequence = "3")
    @CollectionLayout(named = "Week Schedule", render = RenderType.EAGERLY, sortedBy = Occurrence.OccurrencesComparator.class)
    public List<Occurrence> getWeekSchedule() {
        List<Occurrence> weekOcc =  new ArrayList<>();
        for (Playlist pl : getActivePlaylists()){
            weekOcc.addAll(pl.nextOccurrences(clockService.now().plusDays(7)));
        }
        return weekOcc;
    }

    @Inject
    private Playlists playlists;

    @Inject
    private ClockService clockService;


}
