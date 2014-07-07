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
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import org.apache.isis.applib.AbstractContainedObject;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.MultiLine;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.util.TitleBuffer;

import org.estatio.dscm.dom.asset.Asset;
import org.estatio.dscm.dom.asset.Assets;
import org.estatio.dscm.dom.display.DisplayGroup;
import org.estatio.dscm.utils.CalendarUtils;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(name = "findByDisplayGroupAndStartDateTimeAndType", language = "JDOQL",
                value = "SELECT FROM org.estatio.dscm.dom.playlist.Playlist "
                        + "WHERE displayGroup == :displayGroup "
                        + "&& startDate == :startDate "
                        + "&& startTime == :startTime "
                        + "&& type == :type"),
        @javax.jdo.annotations.Query(name = "findByDisplayGroupAndType", language = "JDOQL",
                value = "SELECT FROM org.estatio.dscm.dom.playlist.Playlist "
                        + "WHERE displayGroup == :displayGroup "
                        + "&& type == :type")
})
@javax.jdo.annotations.Unique(name = "Playlist_displayGroup_startDate_startTime_type_UNQ", members = "displayGroup,startDate,startTime,type")
@Bookmarkable
@Bounded
@Immutable
public class Playlist extends AbstractContainedObject implements Comparable<Playlist> {

    public String title() {
        TitleBuffer tb = new TitleBuffer();
        return tb
                .append(getContainer().titleOf(getDisplayGroup()))
                .append(" ", getStartDate().toString("yyyy-MM-dd"))
                .append(" ", getStartTime().toString("hh:mm"))
                .toString();
    }

    private DisplayGroup displayGroup;

    @javax.jdo.annotations.Column(name = "displayGroupId", allowsNull = "false")
    @MemberOrder(sequence = "1")
    public DisplayGroup getDisplayGroup() {
        return displayGroup;
    }

    public void setDisplayGroup(final DisplayGroup displayGroup) {
        this.displayGroup = displayGroup;
    }

    // //////////////////////////////////////

    private LocalDate startDate;

    @MemberOrder(sequence = "3")
    @javax.jdo.annotations.Column(allowsNull = "false")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    private LocalTime startTime;

    @MemberOrder(sequence = "4")
    @javax.jdo.annotations.Column(allowsNull = "false")
    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    // //////////////////////////////////////

    private LocalDate endDate;

    @Optional
    @MemberOrder(sequence = "5")
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    // //////////////////////////////////////

    private PlaylistType type;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @MemberOrder(sequence = "6")
    public PlaylistType getType() {
        return type;
    }

    public void setType(PlaylistType type) {
        this.type = type;
    }

    // //////////////////////////////////////

    private BigDecimal loopDuration;

    @Column(allowsNull = "false")
    @MemberOrder(sequence = "7")
    public BigDecimal getLoopDuration() {
        return loopDuration;
    }

    public void setLoopDuration(BigDecimal duration) {
        this.loopDuration = duration;
    }

    public String validateLoopDuration(final BigDecimal duration) {
        if (duration.compareTo(BigDecimal.ZERO) > 0)
            return null;
        return "Duration can't be zero";
    }

    // //////////////////////////////////////

    @MemberOrder(sequence = "8")
    public BigDecimal getTotalDuration() {
        BigDecimal total = BigDecimal.ZERO;
        for (PlaylistItem item : getItems()) {
            total = total.add(item.getDuration());
        }
        return total;
    }

    // //////////////////////////////////////

    private String repeatRule;

    @Hidden
    @Optional
    public String getRepeatRule() {
        return repeatRule;
    }

    public void setRepeatRule(final String repeatRule) {
        this.repeatRule = repeatRule;
    }

    // //////////////////////////////////////

    @MultiLine(numberOfLines = 10)
    @MemberOrder(sequence = "9")
    public String getNextOccurences() {
        StringBuilder builder = new StringBuilder();
        for (LocalDateTime occurence : nextOccurences(clockService.now().plusDays(7))) {
            builder.append(occurence.toString("yyyy-MM-dd HH:mm"));
            builder.append("\n");
        }
        return builder.toString();
    }

    @Programmatic
    public List<LocalDateTime> nextOccurences(LocalDate endDate) {
        List<LocalDateTime> nextList = new ArrayList<LocalDateTime>();
        if (getEndDate() == null || getEndDate().compareTo(clockService.now()) >= 0) {
            final LocalDate start = getStartDate().isAfter(clockService.now()) ? getStartDate() : clockService.now();
            final LocalDate end = ObjectUtils.min(endDate, getEndDate());
            List<Interval> intervals = CalendarUtils.intervalsInRange(
                    start,
                    end,
                    getRepeatRule());
            for (Interval interval : intervals) {
                nextList.add(new LocalDateTime(
                        interval.getStartMillis()).
                        withHourOfDay(getStartTime().getHourOfDay()).
                        withMinuteOfHour(getStartTime().getMinuteOfHour()));
            }
        }
        return nextList;
    }

    // //////////////////////////////////////

    @Persistent(mappedBy = "playlist")
    private SortedSet<PlaylistItem> items = new TreeSet<PlaylistItem>();

    @Render(Type.EAGERLY)
    public SortedSet<PlaylistItem> getItems() {
        return items;
    }

    public void setItems(final SortedSet<PlaylistItem> items) {
        this.items = items;
    }

    // //////////////////////////////////////

    public Playlist newItem(Asset asset) {
        playlistItems.newPlaylistItem(this, asset);
        return this;
    }

    public List<Asset> choices0NewItem() {
        return assets.findAssetByDisplaygroup(getDisplayGroup());
    }

    // //////////////////////////////////////

    public Object remove(
            @Named("Are you sure?") Boolean confirm) {
        if (confirm) {
            doRemove();
            return null;
        }
        return this;
    }

    protected void doRemove() {
        removeAllItems();
        getContainer().remove(this);
        getContainer().flush();
    }

    @Programmatic
    public void removeAllItems() {
        for (PlaylistItem item : getItems()) {
            item.doRemove();
        }
    }

    // //////////////////////////////////////

    @Override
    public int compareTo(Playlist other) {
        return ObjectContracts.compare(this, other, "startDate,startTime");
    }

    // //////////////////////////////////////

    @Inject
    PlaylistItems playlistItems;

    @Inject
    ClockService clockService;

    @Inject
    Assets assets;

}