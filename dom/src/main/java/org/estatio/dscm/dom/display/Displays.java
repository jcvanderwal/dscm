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
package org.estatio.dscm.dom.display;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotInServiceMenu;
import org.apache.isis.applib.value.Blob;

import org.estatio.dscm.EstatioDomainService;
import org.estatio.dscm.dom.publisher.Publisher;

@DomainService
public class Displays extends EstatioDomainService<Display> {

    public Displays() {
        super(Displays.class, Display.class);
    }

    public String getId() {
        return "display";
    }

    public String iconName() {
        return "Display";
    }

    @Bookmarkable
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public List<Display> allDisplays() {
        return getContainer().allInstances(Display.class);
    }

    // //////////////////////////////////////
    // Create (action)
    // //////////////////////////////////////

    @MemberOrder(sequence = "2")
    public Display newDisplay(
            final @Named("Name") String name,
            final DisplayGroup displayGroup) {
        final Display obj = getContainer().newTransientInstance(Display.class);
        obj.setName(name);
        obj.setDisplayGroup(displayGroup);
        getContainer().persistIfNotAlready(obj);
        return obj;
    }
    
    
    public boolean hideNewDisplay(final String name, final DisplayGroup displayGroup) {
        return !getContainer().getUser().hasRole(".*admin_role");
    }

    @NotInServiceMenu
    public void remove(Display display, @Named("Are you sure?") Boolean confirm) {
        getContainer().remove(display);
        getContainer().flush();
    }

    public boolean hideRemove(Display display, Boolean confirm) {
        return !getContainer().getUser().hasRole(".*admin_role");
    }

}
