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

import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.util.ObjectContracts;
import org.estatio.dscm.DSCMDomainObject;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;
import java.util.List;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@DomainObject(editing = Editing.DISABLED)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
public class Display extends DSCMDomainObject<Display> implements Comparable<Display> {

    private String name;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Title(sequence = "1")
    @MemberOrder(sequence = "1")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // //////////////////////////////////////

    private DisplayGroup displayGroup;

    @Column(name = "displayGroupId", allowsNull = "false")
    public DisplayGroup getDisplayGroup() {
        return displayGroup;
    }

    public void setDisplayGroup(DisplayGroup displayGroup) {
        this.displayGroup = displayGroup;
    }

    public Display changeDisplayGroup(final @ParameterLayout(named = "Display Group") DisplayGroup displayGroup) {
        setDisplayGroup(displayGroup);
        return this;
    }

    public DisplayGroup default0ChangeDisplayGroup(final DisplayGroup displayGroup) {
        return getDisplayGroup();
    }

    public List<Display> remove(@ParameterLayout(named = "Are you sure?") Boolean confirm) {
        getContainer().remove(this);
        getContainer().flush();
        return displays.allDisplays();
    }

    public boolean hideRemove(Boolean confirm) {
        return !getContainer().getUser().hasRole(".*admin_role");
    }

    // //////////////////////////////////////

    @Override
    public int compareTo(Display other) {
        return ObjectContracts.compare(this, other, "name");
    }

    // //////////////////////////////////////

    @Inject
    Displays displays;

}
