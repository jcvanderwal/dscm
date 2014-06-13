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
package org.estatio.dscm.dom;

import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotInServiceMenu;

@DomainService
public class DisplayGroups {

    public String getId() {
        return "displayGroup";
    }

    public String iconName() {
        return "DisplayGroup";
    }

    @Bookmarkable
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public List<DisplayGroup> allDisplayGroups() {
        return container.allInstances(DisplayGroup.class);
    }

    // //////////////////////////////////////
    // Create (action)
    // //////////////////////////////////////

    @MemberOrder(sequence = "2")
    public DisplayGroup newDisplayGroup(
            final @Named("Name") String name) {
        final DisplayGroup obj = container.newTransientInstance(DisplayGroup.class);
        obj.setName(name);
        container.persistIfNotAlready(obj);
        return obj;
    }

    @NotInServiceMenu
    public void remove(DisplayGroup displayGroup, @Named("Are you sure?") Boolean confirm) {
        container.remove(displayGroup);
        container.flush();
    }

    public boolean hideRemove(DisplayGroup displayGroup, Boolean confirm) {
        return !container.getUser().hasRole(".*admin_role");
    }

    // //////////////////////////////////////
    // Injected services
    // //////////////////////////////////////

    @javax.inject.Inject
    DomainObjectContainer container;

}
