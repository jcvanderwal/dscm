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
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import org.apache.isis.applib.AbstractContainedObject;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.util.TitleBuffer;

import org.isisaddons.wicket.fullcalendar2.cpt.applib.CalendarEventable;
import org.isisaddons.wicket.fullcalendar2.cpt.applib.Calendarable;

import org.estatio.dscm.DscmDashboard;
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
        @javax.jdo.annotations.Query(name = "findByDisplayGroupAndTimeAndType", language = "JDOQL",
                value = "SELECT FROM org.estatio.dscm.dom.playlist.Playlist "
                        + "WHERE displayGroup == :displayGroup "
                        + "&& startTime == :time "
                        + "&& type == :type")
})
//@Unique(name = "Playlist_displayGroup_startDate_startTime_type_repeatRule_UNQ", members = {"displayGroup", "startDate", "startTime", "type", "repeatRule"})
@DomainObject(bounded = true, editing = Editing.DISABLED)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
public class Playlist extends AbstractContainedObject implements Comparable<Playlist>, Calendarable {

    public String title() {
        TitleBuffer tb = new TitleBuffer();

        if (this.getItems().size() == 0) {
            return tb
                    .append(getDisplayGroup().getName())
                    .append(" ", getStartDate().toString("yyyy-MM-dd"))
                    .append(" ", getStartTime().toString("HH:mm"))
                    .append(" - ", "WARNING: Playlist is empty!")
                    .toString();
        } else {
            return tb
                    .append(getDisplayGroup().getName())
                    .append(" ", getStartDate().toString("yyyy-MM-dd"))
                    .append(" ", getStartTime().toString("HH:mm"))
                    .append(" - ", getType())
                    .toString();
        }
    }

    public boolean isValid() {
        return this.getItems().size() > 0;
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

    @MemberOrder(sequence = "5")
    @Property(optionality = Optionality.OPTIONAL)
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
    @PropertyLayout(hidden = Where.ALL_TABLES)
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

    @Property(hidden = Where.EVERYWHERE)
    public String getRepeatRule() {
        return repeatRule;
    }

    public void setRepeatRule(final String repeatRule) {
        this.repeatRule = repeatRule;
    }

    @PropertyLayout(named = "Repeat Rule")
    @MemberOrder(sequence = "9")
    public String getRepeatRuleReadable() {
        String[] updateOldRepeatRule = getRepeatRule().split("=");
        // Old RRULE format for daily, change it to new one
        if (updateOldRepeatRule.length == 2) {
            this.setRepeatRule("RRULE:FREQ=DAILY;BYDAY=MO,TU,WE,TH,FR,SA,SU");
        }

        String[] splitDays = getRepeatRule().split("=")[2].split(",");
        String returnString = "";
        for (String eachDay : splitDays) {
            switch (eachDay) {
            case "MO":
                returnString += "Mon, ";
                break;
            case "TU":
                returnString += "Tue, ";
                break;
            case "WE":
                returnString += "Wed, ";
                break;
            case "TH":
                returnString += "Thu, ";
                break;
            case "FR":
                returnString += "Fri, ";
                break;
            case "SA":
                returnString += "Sat, ";
                break;
            case "SU":
                returnString += "Sun";
                break;
            }
        }

        return returnString.endsWith(" ") ? returnString.substring(0, returnString.length() - 2) : returnString;
    }

    // //////////////////////////////////////

    @MemberOrder(sequence = "10")
    @PropertyLayout(multiLine = 10, hidden = Where.ALL_TABLES)
    public String getNextOccurrences() {
        StringBuilder builder = new StringBuilder();
        LocalDate fromDate = clockService.now().compareTo(this.getStartDate()) >= 0 ? clockService.now() : this.getStartDate();
        for (Occurrence occurrence : nextOccurrences(fromDate.plusDays(7))) {
            builder.append(occurrence.getDateTime().toString("E\tdd-MM-yyyy\tHH:mm"));
            builder.append("\n");
        }

        return builder.toString();
    }

    // Creates nextOccurrences until the (end)date given as parameter is reached.
    @Programmatic
    public List<Occurrence> nextOccurrences(LocalDate endDate) {
        List<Occurrence> nextOccurrences = new ArrayList<Occurrence>();

        final LocalDate start = getStartDate().isBefore(clockService.now()) ? clockService.now() : getStartDate();
        final LocalDate end = ObjectUtils.min(endDate, this.getEndDate());

        // When the end date is after or equal to the start date, and the end date is in the future (e.g. later than today) a week of intervals is generated.
        // example interval: 2015-03-12T00:00:00.000+01:00/2015-03-13T00:00:00.000+01:00 which is equal to a time period of 1 day.
        // almost 365 (1 year) periods/interval are generated. why???
        if (end.compareTo(start) >= 0 && end.compareTo(clockService.now()) >= 0) {
            List<Interval> intervals;
            intervals = CalendarUtils.intervalsInRange(
                    start,
                    end,
                    getRepeatRule());

            // For every interval ...????
            for (Interval interval : intervals) {
                LocalDateTime intervalStart = new LocalDateTime(interval.getStart());
                intervalStart = intervalStart.withTime(getStartTime().getHourOfDay(), getStartTime().getMinuteOfHour(), getStartTime().getSecondOfMinute(), getStartTime().getMillisOfSecond());
                LocalTime time = getStartDate().isBefore(clockService.now()) ? clockService.nowAsLocalDateTime().toLocalTime() : getStartTime();

                // hier zat ie........................

                // As long as the intervalstart is after (or equal) the start time of the current playlist
                // OR
                // if the intervalstart is before the start time of the current playlist AND the Current playlist is the most recent playlist
                // Then
                // A new occurence is added to the nextOccurrences
                // So:
                // Goal of this scope: Keep adding new occurences until the same day is reached. e.g. Today is Monday, keep scheduling next occurrences until you reach Monday next week. Or isn't it?
                int comp = intervalStart.compareTo(start.toLocalDateTime(time));

                if (comp >= 0 || (intervalStart.toLocalDate().compareTo(start) == 0 && isMostRecent(interval))) {
                    nextOccurrences.add(new Occurrence(
                            this.getType(),
                            new LocalDateTime(
                                    interval.getStartMillis()).
                                    withHourOfDay(getStartTime().getHourOfDay()).
                                    withMinuteOfHour(getStartTime().getMinuteOfHour()),
                            this.title(),
                            getItems().isEmpty()
                    ));
                }
            }
        }

        return nextOccurrences;
    }

    // //////////////////////////////////////

    @Programmatic
    public boolean isMostRecent (Interval interval) {
        //boolean mostRecent = true;

        //List all playlists that have the same type, displaygroup. These playlists could be deprecated or have another repeatrule since there is no check for date and/or time
        List<Playlist> otherPlaylists = playlists.findByDisplayGroupAndType(getDisplayGroup(), getType());

        //Loop through all playlists. When a playlist starts later then the current playlist, and earlier than the actual time (now), then the current playlist is up to date (and should be replaced by the more recent playlist.
        for(Playlist playlist :otherPlaylists) {
            if (playlist.repeatRuleToBooleans(playlist.getRepeatRule())[interval.getStart().getDayOfWeek() - 1] && playlist.getStartTime().isAfter(this.getStartTime()) && playlist.getStartTime().isBefore(clockService.nowAsLocalDateTime().toLocalTime())) {
                return false;
            }
        }

        return true;

    }

    // //////////////////////////////////////

    @Persistent(mappedBy = "playlist")
    private SortedSet<PlaylistItem> items = new TreeSet<PlaylistItem>();

    @CollectionLayout(render = RenderType.EAGERLY)
    public SortedSet<PlaylistItem> getItems() {
        return items;
    }

    public void setItems(final SortedSet<PlaylistItem> items) {
        this.items = items;
    }

    // //////////////////////////////////////

    @MemberOrder(name = "items", sequence = "1")
    public Playlist newItem(Asset asset) {
        playlistItems.newPlaylistItem(this, asset);
        return this;
    }

    public List<Asset> choices0NewItem() {
        return assets.findAssetByDisplaygroup(getDisplayGroup());
    }

    // //////////////////////////////////////

    public Playlist endAndCreateNewPlaylist(
            final @ParameterLayout(named = "Start date") LocalDate newDate) {
        DisplayGroup newDisplayGroup = this.getDisplayGroup();
        PlaylistType newType = this.getType();
        Time newTime = Time.localTimeToTime(this.getStartTime());
        boolean[] newRepeat = repeatRuleToBooleans(this.getRepeatRule());

        Playlist newPlaylist = playlists.newPlaylist(
                newDisplayGroup,
                newType,
                newDate,
                newTime,
                null,
                newRepeat[0],
                newRepeat[1],
                newRepeat[2],
                newRepeat[3],
                newRepeat[4],
                newRepeat[5],
                newRepeat[6]);

        this.setEndDate(newDate);

        return newPlaylist;
    }

    public static boolean[] repeatRuleToBooleans(String rule) {
        boolean[] days = new boolean[7];
        String[] splitDays = rule.split("=")[2].split(",");

        for (String eachDay : splitDays) {
            switch (eachDay) {
            case "MO":
                days[0] = true;
                break;
            case "TU":
                days[1] = true;
                break;
            case "WE":
                days[2] = true;
                break;
            case "TH":
                days[3] = true;
                break;
            case "FR":
                days[4] = true;
                break;
            case "SA":
                days[5] = true;
                break;
            case "SU":
                days[6] = true;
                break;
            }
        }

        return days;
    }

    // //////////////////////////////////////

    public Object remove(
            @ParameterLayout(named = "Are you sure?") Boolean confirm) {
        if (confirm) {
            doRemove();
            return new DscmDashboard();
        } else {
            return this;
        }
    }

    public String disableRemove(
            @ParameterLayout(named = "Are you sure?") Boolean confirm) {
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

    @Programmatic
    public Weekdays repeatRuleCollidesWith(String repeatRule) {
        String[] thisRepeatSplit = this.getRepeatRule().split("=")[2].split(",");
        String[] otherRepeatSplit = repeatRule.split("=")[2].split(",");

        for (String thisRepeatDay : thisRepeatSplit) {
            for (String otherRepeatDay : otherRepeatSplit) {
                if (thisRepeatDay.equals(otherRepeatDay))
                    return Weekdays.stringToWeekdays(thisRepeatDay);
            }
        }

        return null;
    }

    @Override
    @Programmatic
    public Set<String> getCalendarNames() {
        return Sets.newHashSet(getType().title());
    }

    @Override
    @Programmatic
    public ImmutableMap<String, List<? extends CalendarEventable>> getCalendarEvents() {
        LocalDate fromDate = clockService.now().compareTo(this.getStartDate()) >= 0 ? clockService.now() : this.getStartDate();
        List<? extends CalendarEventable> nextOccurrences = nextOccurrences(fromDate.plusDays(7));
        final ImmutableMap<String, List<? extends CalendarEventable>> eventsByCalendarName = ImmutableMap.<String, List<? extends CalendarEventable>>builder().put(this.getType().title(), nextOccurrences).build();
        System.out.println(eventsByCalendarName);
        return eventsByCalendarName;
    }

    // //////////////////////////////////////

    @MemberOrder(name = "nextOccurrences", sequence = "1")
    public Playlist changeValues(
            @ParameterLayout(named = "Start Date") LocalDate startDate,
            @ParameterLayout(named = "Start Time") Time startTime,
            @Parameter(optionality = Optionality.OPTIONAL) @ParameterLayout(named = "End Date") LocalDate endDate
    ) {
        setStartDate(startDate);
        setStartTime(startTime.time());
        setEndDate(endDate);

        return this;
    }

    @Programmatic
    public LocalDate default0ChangeValues() {
        return getStartDate();
    }

    @Programmatic
    public Time default1ChangeValues() {
        return Time.localTimeToTime(getStartTime());
    }

    @Programmatic
    public LocalDate default2ChangeValues() {
        return getEndDate();
    }

    @Programmatic
    public String validateChangeValues(
            LocalDate startDate,
            Time startTime,
            LocalDate endDate
    ) {
        if (endDate != null) {
            if (startDate.isAfter(endDate)) {
                return "Invalid input: The start date is after the end date";
            } else if (endDate != null && endDate.isBefore(startDate)) {
                return "Invalid input: The end date is before the start date";
            }
        }

        return null;
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

    @Inject
    Time time;

}