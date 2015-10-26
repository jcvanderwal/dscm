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

import java.util.function.Function;

import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.Programmatic;

import org.isisaddons.wicket.fullcalendar2.cpt.applib.CalendarEvent;
import org.isisaddons.wicket.fullcalendar2.cpt.applib.CalendarEventable;

public class Occurrence implements CalendarEventable {

    public Occurrence(PlaylistType type, LocalDateTime dateTime, String title, boolean emptyPlaylist) {
        this.type = type;
        this.dateTime = dateTime;
        this.title = title;
        this.emptyPlaylist = emptyPlaylist;
    }

    private PlaylistType type;

    public PlaylistType getType() {
        return type;
    }

    private LocalDateTime dateTime;

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    private String title;

    public String getTitle() {
        return title;
    }

    private boolean emptyPlaylist;

    public boolean getEmptyPlaylist() {
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
        return new CalendarEvent(getDateTime().toDateTime(), getCalendarName(), getTitle(), getEmptyPlaylist());
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
}
