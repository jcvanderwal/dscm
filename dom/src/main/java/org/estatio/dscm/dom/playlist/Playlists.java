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
import java.rmi.dgc.DGC;
import java.util.List;

import org.estatio.dscm.EstatioDomainService;
import org.estatio.dscm.dom.display.DisplayGroup;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.query.QueryDefault;

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
        // return getContainer().allInstances(Playlist.class);
        return getContainer().allMatches(new QueryDefault<Playlist>(Playlist.class, "findAll"));
    }

    // //////////////////////////////////////

    @Programmatic
    public Playlist findByDisplayGroupAndStartDateTimeAndType(
            final DisplayGroup displayGroup,
            final LocalDate startDate,
            final LocalTime startTime,
            final PlaylistType type) {
        return firstMatch("findByDisplayGroupAndStartDateTimeAndType",
                "displayGroup", displayGroup,
                "startDate", startDate,
                "startTime", startTime,
                "type", type);
    }

    @Programmatic
    public Playlist findByDisplayGroupAndDateTimeAndType(
            final DisplayGroup displayGroup,
            final LocalDate date,
            final LocalTime time,
            final PlaylistType type) {
        return firstMatch("findByDisplayGroupAndDateTimeAndType",
                "displayGroup", displayGroup,
                "date", date,
                "time", time,
                "type", type);
    }

    // //////////////////////////////////////

    @Programmatic
    public List<Playlist> findByDisplayGroupAndType(
            final DisplayGroup displayGroup,
            final PlaylistType type) {
        return allMatches("findByDisplayGroupAndType",
                "displayGroup", displayGroup,
                "type", type);
    }

    @ActionSemantics(Of.SAFE)
    @Render(Type.EAGERLY)
    public List<Playlist> mainPlaylists(DisplayGroup displayGroup) {
    	return findByDisplayGroupAndType(displayGroup, PlaylistType.MAIN);
    }
    
    @ActionSemantics(Of.SAFE)
    @Render(Type.EAGERLY)
    public List<Playlist> fillerPlaylists(DisplayGroup displayGroup) {
    	return findByDisplayGroupAndType(displayGroup, PlaylistType.FILLERS);
    }

    // //////////////////////////////////////
    
    @Programmatic
    public Playlist findByDisplayGroupAndTimeAndType(
            final DisplayGroup displayGroup,
            final LocalTime time,
            final PlaylistType type) {
        return firstMatch("findByDisplayGroupAndTimeAndType", 
                "displayGroup", displayGroup,
                "time", time,
                "type", type);
    }

    @MemberOrder(sequence = "2")
    public Playlist newPlaylist(
            final DisplayGroup displayGroup,
            final PlaylistType type,
            final @Named("Start date") LocalDate startDate,
            final @Named("Start time") Time startTime,
            final @Named("End date") @Optional LocalDate endDate,
            final @Named("Repeat") PlaylistRepeat repeat,
            final @Named("Loop duration") BigDecimal loopDuration) {
        final Playlist obj = getContainer().newTransientInstance(Playlist.class);
        obj.setDisplayGroup(displayGroup);
        obj.setStartDate(startDate);
        obj.setStartTime(startTime.time());
        obj.setEndDate(endDate);
        obj.setRepeatRule(repeat.rrule());
        obj.setType(type);
        obj.setLoopDuration(loopDuration);
        getContainer().persistIfNotAlready(obj);
        return obj;
    }

    public String validateNewPlaylist(
            final DisplayGroup displayGroup,
            final PlaylistType type,
            final LocalDate startDate,
            final Time startTime,
            final LocalDate endDate,
            final PlaylistRepeat repeat,
            final BigDecimal loopDuration) {
        final Boolean exists = findByDisplayGroupAndTimeAndType(displayGroup, startTime.time(), type) == null ? false : true;
        return exists ? "The selected display group already has a playlist of this type with this start time" : null;
    }
    
    public LocalDate default2NewPlaylist() {
        return getClockService().now();
    }

    public BigDecimal default6NewPlaylist() {
        return new BigDecimal(60);
    }

}
