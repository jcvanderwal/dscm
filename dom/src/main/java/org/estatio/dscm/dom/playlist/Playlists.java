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
package org.estatio.dscm.dom.playlist;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;

import org.estatio.dscm.EstatioDomainService;
import org.estatio.dscm.dom.display.DisplayGroup;

@DomainService
public class Playlists extends EstatioDomainService<Playlist> {

    public Playlists() {
        super(Playlists.class, Playlist.class);
    }

    public String getId() {
        return "playlist";
    }

    public String iconName() {
        return "Playlist";
    }

    @Bookmarkable
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public List<Playlist> allPlaylists() {
        return getContainer().allInstances(Playlist.class);
    }

    // //////////////////////////////////////

    public Playlist findByStartDateAndStartTimeAndType(
            final DisplayGroup displayGroup,
            final LocalDate startDate,
            final LocalTime startTime,
            final PlaylistType type) {
        return firstMatch("findByStartDateAndStartTimeAndType",
                "displayGroup", displayGroup,
                "startDate", startDate,
                "startTime", startTime,
                "type", type);
    }

    // //////////////////////////////////////

    @MemberOrder(sequence = "2")
    public Playlist newPlaylist(
            final DisplayGroup displayGroup,
            final PlaylistType type,
            final @Named("Start date") LocalDate startDate,
            final @Named("Start time") Time startTime,
            final @Named("End date") @Optional LocalDate endDate,
            final @Named("Repeat") PlaylistRepeat repeat, final @Named("Duration") @Optional BigDecimal duration) {
        final Playlist obj = getContainer().newTransientInstance(Playlist.class);
        obj.setDisplayGroup(displayGroup);
        obj.setStartDate(startDate);
        obj.setStartTime(startTime.time());
        obj.setEndDate(endDate);
        obj.setRepeatRule(repeat.rrule());
        obj.setType(type);
        getContainer().persistIfNotAlready(obj);
        return obj;
    }

}
