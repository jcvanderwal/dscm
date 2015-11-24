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

import java.util.Comparator;

import com.google.common.base.Function;
import com.google.common.collect.ComparisonChain;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.ViewModel;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.wicket.fullcalendar2.cpt.applib.CalendarEvent;
import org.isisaddons.wicket.fullcalendar2.cpt.applib.CalendarEventable;

public class Occurrence implements CalendarEventable {

    public Occurrence() {
    }

    public Occurrence(PlaylistType type, LocalDateTime dateTime, String title, boolean emptyPlaylist) {
        this.type = type;
        this.dateTime = dateTime;
        this.title = title;
        this.emptyPlaylist = emptyPlaylist;
        this.date = dateTime.toLocalDate();
        this.time = dateTime.toLocalTime();
    }

    // /////////////////////////////////////

    private PlaylistType type;

    public PlaylistType getType() {
        return type;
    }

    public void setType(final PlaylistType type) {
        this.type = type;
    }

    // /////////////////////////////////////

    private LocalDateTime dateTime;

    @PropertyLayout(hidden = Where.EVERYWHERE)
    @Programmatic
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(final LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    // /////////////////////////////////////

    private LocalDate date;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(final LocalDate date) {
        this.date = date;
    }

    // /////////////////////////////////////

    private LocalTime time;

    public LocalTime getTime() {
        return time;
    }

    public void setTime(final LocalTime time) {
        this.time = time;
    }

    // /////////////////////////////////////

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    // /////////////////////////////////////

    private boolean emptyPlaylist;

    @Programmatic
    public boolean isEmptyPlaylist() {
        return emptyPlaylist;
    }

    @Override
    @Programmatic
    public String getCalendarName() {
        return this.getType().title();
    }

    @Override
    @Programmatic
    public CalendarEvent toCalendarEvent() {
        return new CalendarEvent(getDateTime().toDateTime(), getCalendarName(), getTitle(), isEmptyPlaylist());
    }

    public final static class Functions {
        private Functions() {
        }

        public final static Function<Occurrence, CalendarEvent> TO_CALENDAR_EVENT = new Function<Occurrence, CalendarEvent>() {
            @Override
            public CalendarEvent apply(final Occurrence input) {
                return input.toCalendarEvent();
            }
        };
        public final static Function<Occurrence, String> GET_CALENDAR_NAME = new Function<Occurrence, String>() {
            @Override
            public String apply(final Occurrence input) {
                return input.getCalendarName();
            }
        };
    }

    public void setEmptyPlaylist(final boolean emptyPlaylist) {
        this.emptyPlaylist = emptyPlaylist;
    }

    // /////////////////////////////////////

    public static class OccurrencesComparator implements Comparator<Occurrence> {
        @Override
        public int compare(Occurrence t, Occurrence o) {
            return ComparisonChain.start()
                    .compare(t.getDate(), o.getDate())
                    .compare(t.getTime(), o.getTime())
                    .compare(t.getType(), o.getType())
                    .result();
        }
    }
}
