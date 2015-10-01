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

import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dscm.EstatioDomainService;

@DomainService
public class    Displays extends EstatioDomainService<Display> {

    public Displays() {
        super(Displays.class, Display.class);
    }

    public String getId() {
        return "display";
    }

    public String iconName() {
        return "Display";
    }

    @MemberOrder(sequence = "1")
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    public List<Display> allDisplays() {
        return getContainer().allInstances(Display.class);
    }

    // //////////////////////////////////////
    // Create (action)
    // //////////////////////////////////////

    @MemberOrder(name = "Displays", sequence = "2")
    public Display newDisplay(
            final @ParameterLayout(named = "Name") String name,
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

}
