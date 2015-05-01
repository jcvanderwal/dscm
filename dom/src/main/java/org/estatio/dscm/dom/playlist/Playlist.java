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

import com.google.common.collect.ComparisonChain;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.isis.applib.AbstractContainedObject;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.util.TitleBuffer;
import org.estatio.dscm.DscmDashboard;
import org.estatio.dscm.dom.asset.Asset;
import org.estatio.dscm.dom.asset.Assets;
import org.estatio.dscm.dom.display.DisplayGroup;
import org.estatio.dscm.utils.CalendarUtils;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import javax.inject.Inject;
import javax.jdo.annotations.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(name = "findAll", language = "JDOQL",
                value = "SELECT FROM org.estatio.dscm.dom.playlist.Playlist "
                        + "ORDER BY displayGroup.name, startDate, startTime"),
        @javax.jdo.annotations.Query(name = "findAllActive", language = "JDOQL",
                value = "SELECT FROM org.estatio.dscm.dom.playlist.Playlist "
                        + "WHERE endDate == null || endDate <= :date "
                        + "ORDER BY displayGroup, startDate, startTime"),
        @javax.jdo.annotations.Query(name = "findByDisplayGroupAndStartDateTimeAndType", language = "JDOQL",
                value = "SELECT FROM org.estatio.dscm.dom.playlist.Playlist "
                        + "WHERE displayGroup == :displayGroup "
                        + "&& startDate == :startDate "
                        + "&& startTime == :startTime "
                        + "&& type == :type"),
        @javax.jdo.annotations.Query(name = "findByDisplayGroupAndDateTimeAndType", language = "JDOQL",
                value = "SELECT FROM org.estatio.dscm.dom.playlist.Playlist "
                        + "WHERE displayGroup == :displayGroup "
                        + "&& startDate <= :date "
                        + "&& (endDate == null || endDate >= :date) "
                        + "&& startTime <= :time "
                        + "&& type == :type "
                        + "ORDER BY startDate DESC, startTime DESC"),
        @javax.jdo.annotations.Query(name = "findByDisplayGroupAndType", language = "JDOQL",
                value = "SELECT FROM org.estatio.dscm.dom.playlist.Playlist "
                        + "WHERE displayGroup == :displayGroup "
                        + "&& type == :type"),
        @javax.jdo.annotations.Query(name = "findByDisplayGroupAndStartTimeAndType", language = "JDOQL",
                value = "SELECT FROM org.estatio.dscm.dom.playlist.Playlist "
                        + "WHERE displayGroup == :displayGroup "
                        + "&& startTime == :startTime "
                        + "&& type == :type"),
        @javax.jdo.annotations.Query(name = "findByDisplayGroupAndTimeAndTypeAndPlaylistRepeat", language = "JDOQL",
                value = "SELECT FROM org.estatio.dscm.dom.playlist.Playlist "
                        + "WHERE displayGroup == :displayGroup "
                        + "&& startTime == :time "
                        + "&& type == :type "
                        + "&& repeatRule == :repeatRule")
})
@Unique(name = "Playlist_displayGroup_startDate_startTime_type_repeatRule_UNQ", members = {"displayGroup", "startDate", "startTime", "type", "repeatRule"})
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

    @Column(allowsNull = "false")
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
        LocalDate fromDate = clockService.now().compareTo(this.getStartDate()) >= 0 ? clockService.now() : this.getStartDate();
        for (LocalDateTime occurence : nextOccurences(fromDate.plusDays(7), false)) {
            builder.append(occurence.toString("yyyy-MM-dd HH:mm"));
            builder.append("\n");
        }
        return builder.toString();
    }

    @Programmatic
    public List<LocalDateTime> nextOccurences(LocalDate endDate, boolean test) {
        List<LocalDateTime> nextList = new ArrayList<LocalDateTime>();
        final LocalDate start = getStartDate().isBefore(clockService.now()) ? clockService.now() : getStartDate();
        final LocalDate end = ObjectUtils.min(endDate, this.getEndDate());
        List<Playlist> individualDayPlaylists = null;

        if (this.getRepeatRule().equals(PlaylistRepeat.DAILY.rrule()) && test == false) {
            individualDayPlaylists = playlists.findByDisplayGroupAndStartTimeAndType(
                    this.getDisplayGroup(),
                    this.getStartTime(),
                    this.getType());
            List<Playlist> copyIndividual = new ArrayList<Playlist>(individualDayPlaylists);
            for (Playlist dailyPlaylist : copyIndividual) {
                if (dailyPlaylist.getRepeatRule().equals(PlaylistRepeat.DAILY.rrule())) {
                    individualDayPlaylists.remove(dailyPlaylist);
                }
            }
        }

        if (end.compareTo(start) >= 0 && end.compareTo(clockService.now()) >= 0) {
            List<Interval> intervals;
            intervals = CalendarUtils.intervalsInRange(
                    start,
                    end,
                    getRepeatRule());

            for (Interval interval : intervals) {
                LocalDateTime intervalStart = new LocalDateTime(interval.getStart());
                if (intervalStart.compareTo(start.toLocalDateTime(new LocalTime("00:00"))) >= 0) {
                    boolean add = true;
                    if (individualDayPlaylists != null && !individualDayPlaylists.isEmpty()) {
                        for (Playlist dailyPlaylist : individualDayPlaylists) {
                            if (intervalStart.dayOfWeek().getAsText().toUpperCase().equals(PlaylistRepeat.stringToPlaylistRepeat(dailyPlaylist.getRepeatRule()).title())) {
                                add = false;
                            }
                        }
                    }

                    if (add == true) {
                        nextList.add(new LocalDateTime(
                                interval.getStartMillis()).
                                withHourOfDay(getStartTime().getHourOfDay()).
                                withMinuteOfHour(getStartTime().getMinuteOfHour()));
                    }
                }
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

    public Playlist endAndCreateNewPlaylist(
            final @Named("Start date") LocalDate newDate) {
        DisplayGroup newDisplayGroup = this.getDisplayGroup();
        PlaylistType newType = this.getType();
        Time newTime = Time.localTimeToTime(this.getStartTime());
        PlaylistRepeat newRepeat = PlaylistRepeat.stringToPlaylistRepeat(this.getRepeatRule());

        Playlist newPlaylist = playlists.newPlaylist(
                newDisplayGroup,
                newType,
                newDate,
                newTime,
                null,
                newRepeat);

        this.setEndDate(newDate);

        return newPlaylist;
    }

    // //////////////////////////////////////

    public Object remove(
            @Named("Are you sure?") Boolean confirm) {
        if (confirm) {
            doRemove();
            return newViewModelInstance(DscmDashboard.class, "dashboard");
        } else {
            return this;
        }
    }

    public String disableRemove(
            @Named("Are you sure?") Boolean confirm) {
        if (playlists.findByDisplayGroupAndType(displayGroup, type).size() == 1 &&
                !getContainer().getUser().hasRole(".*admin_role")) {
            return "This is the only " + type.toString().toLowerCase() + " playlist of display group " + displayGroup.getName();
        } else {
            return null;
        }
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
        return ComparisonChain.start()
                .compare(getDisplayGroup(), other.getDisplayGroup())
                .compare(getStartDate(), other.getStartDate())
                .compare(getStartTime(), other.getStartTime())
                .result();
    }

    // //////////////////////////////////////

    @Inject
    PlaylistItems playlistItems;

    @Inject
    public ClockService clockService;

    @Inject
    Assets assets;

    @Inject
    Playlists playlists;

}