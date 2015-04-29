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

import com.danhaywood.isis.wicket.gmap3.applib.Location;
import com.danhaywood.isis.wicket.gmap3.service.LocationLookupService;
import com.google.common.collect.ComparisonChain;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Render.Type;

import javax.inject.Inject;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;
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
                value = "SELECT FROM org.estatio.dscm.dom.display.DisplayGroup "
                        + "ORDER BY name")
})
@Bookmarkable
@Bounded
@Immutable
public class DisplayGroup implements Comparable<DisplayGroup> {

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

    @javax.jdo.annotations.Persistent
    private Location location;

    @Disabled
    @Optional
    @Hidden(where = Where.ALL_TABLES)
    public Location getLocation() {
        return location;
    }

    public void setLocation(final Location location) {
        this.location = location;
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public DisplayGroup setLocation(
            final @Named("Address") @DescribedAs("Example: Herengracht 469, Amsterdam, NL") String address) {
        if (locationLookupService != null) {
            // TODO: service does not seem to be loaded in tests
            setLocation(locationLookupService.lookup(address));
        }
        return this;
    }

    // //////////////////////////////////////

    @Persistent(mappedBy = "displayGroup", dependentElement = "true")
    private SortedSet<Display> displays = new TreeSet<Display>();

    @MemberOrder(sequence = "1")
    @Render(Type.EAGERLY)
    public SortedSet<Display> getDisplays() {
        return displays;
    }

    public void setDisplays(final SortedSet<Display> displays) {
        this.displays = displays;
    }

    @Override
    public int compareTo(DisplayGroup other) {
        return ComparisonChain.start().compare(getName(), other.getName()).result();
    }

    // //////////////////////////////////////

    @Inject
    private LocationLookupService locationLookupService;
}
