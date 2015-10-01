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

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.query.QueryDefault;

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

    @MemberOrder(sequence = "1")
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    public List<Playlist> allPlaylists() {
        // return getContainer().allInstances(Playlist.class);
        return getContainer().allMatches(new QueryDefault<Playlist>(Playlist.class, "findAll"));
    }

    // //////////////////////////////////////

    @Programmatic
    public List<Playlist> findByDisplayGroupAndStartDateTimeAndType(
            final DisplayGroup displayGroup,
            final LocalDate startDate,
            final LocalTime startTime,
            final PlaylistType type) {
        return allMatches("findByDisplayGroupAndStartDateTimeAndType",
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

    @Action(semantics = SemanticsOf.SAFE)
    @CollectionLayout(render = RenderType.EAGERLY)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<Playlist> mainPlaylists(DisplayGroup displayGroup) {
        return findByDisplayGroupAndType(displayGroup, PlaylistType.MAIN);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @CollectionLayout(render = RenderType.EAGERLY)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<Playlist> fillerPlaylists(DisplayGroup displayGroup) {
        return findByDisplayGroupAndType(displayGroup, PlaylistType.FILLERS);
    }

    // //////////////////////////////////////

    @Programmatic
    public List<Playlist> findByDisplayGroupAndStartTimeAndType(
            final DisplayGroup displayGroup,
            final LocalTime startTime,
            final PlaylistType type) {
        return allMatches("findByDisplayGroupAndStartTimeAndType",
                "displayGroup", displayGroup,
                "startTime", startTime,
                "type", type);
    }

    @Programmatic
    public List<Playlist> findByDisplayGroupAndTimeAndType(
            final DisplayGroup displayGroup,
            final LocalTime time,
            final PlaylistType type) {
        return allMatches("findByDisplayGroupAndTimeAndType",
                "displayGroup", displayGroup,
                "time", time,
                "type", type);
    }

    @MemberOrder(sequence = "2")
    public Playlist newPlaylist(
            final DisplayGroup displayGroup,
            final PlaylistType type,
            final @ParameterLayout(named = "Start date") LocalDate startDate,
            final @ParameterLayout(named = "Start time") Time startTime,
            final @ParameterLayout(named = "End date") @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate,
            final @ParameterLayout(named = "Monday") @Parameter(optionality = Optionality.OPTIONAL) boolean monday,
            final @ParameterLayout(named = "Tuesday") @Parameter(optionality = Optionality.OPTIONAL) boolean tuesday,
            final @ParameterLayout(named = "Wednesday") @Parameter(optionality = Optionality.OPTIONAL) boolean wednesday,
            final @ParameterLayout(named = "Thursday") @Parameter(optionality = Optionality.OPTIONAL) boolean thursday,
            final @ParameterLayout(named = "Friday") @Parameter(optionality = Optionality.OPTIONAL) boolean friday,
            final @ParameterLayout(named = "Saturday") @Parameter(optionality = Optionality.OPTIONAL) boolean saturday,
            final @ParameterLayout(named = "Sunday") @Parameter(optionality = Optionality.OPTIONAL) boolean sunday) {
        final Playlist obj = getContainer().newTransientInstance(Playlist.class);
        obj.setDisplayGroup(displayGroup);
        obj.setStartDate(startDate);
        obj.setStartTime(startTime.time());
        obj.setEndDate(endDate);
        String repeat = booleanToRepeatRule(monday, tuesday, wednesday, thursday, friday, saturday, sunday);
        if (repeat != null) {
            obj.setRepeatRule(repeat);
        } else {
            return null;
        }
        obj.setType(type);
        obj.setLoopDuration(new BigDecimal(60));
        getContainer().persistIfNotAlready(obj);
        return obj;
    }

    public String validateNewPlaylist(
            final DisplayGroup displayGroup,
            final PlaylistType type,
            final LocalDate startDate,
            final Time startTime,
            final LocalDate endDate,
            final boolean monday,
            final boolean tuesday,
            final boolean wednesday,
            final boolean thursday,
            final boolean friday,
            final boolean saturday,
            final boolean sunday) {
        if (!monday && !tuesday && !wednesday && !thursday && !friday && !saturday && !sunday) {
            return "At least one day must be selected";
        }

        String repeat = booleanToRepeatRule(monday, tuesday, wednesday, thursday, friday, saturday, sunday);
        List<Playlist> possibleCollisions = findByDisplayGroupAndTimeAndType(displayGroup, startTime.time(), type);

        for (Playlist playlist : possibleCollisions) {
            Weekdays day = playlist.repeatRuleCollidesWith(repeat);
            if (day != null) {
                return "A playlist of this type and start time is already active on ".concat(day.lowerCaseName());
            }
        }

        return null;
    }

    public LocalDate default2NewPlaylist() {
        return getClockService().now();
    }

    private String booleanToRepeatRule(boolean monday,
                                       boolean tuesday,
                                       boolean wednesday,
                                       boolean thursday,
                                       boolean friday,
                                       boolean saturday,
                                       boolean sunday) {

        if (!monday && !tuesday && !wednesday && !thursday && !friday && !saturday && !sunday) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("RRULE:FREQ=DAILY;BYDAY=");

        if (monday) builder.append("MO,");
        if (tuesday) builder.append("TU,");
        if (wednesday) builder.append("WE,");
        if (thursday) builder.append("TH,");
        if (friday) builder.append("FR,");
        if (saturday) builder.append("SA,");
        if (sunday) builder.append("SU");

        if (builder.length() > 0 && builder.charAt(builder.length() - 1) == ',') {
            builder.setLength(builder.length() - 1);
        }

        return builder.toString();
    }
}
