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
package org.estatio.dscm.dom.asset;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import com.google.inject.name.Named;

import org.joda.time.LocalDate;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.HomePage;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.value.Blob;

import org.estatio.dscm.DscmDashboard;
import org.estatio.dscm.dom.display.DisplayGroup;
import org.estatio.dscm.dom.playlist.PlaylistItems;
import org.estatio.dscm.dom.publisher.Publisher;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        // TODO: secondary ordening by publisher
        @javax.jdo.annotations.Query(name = "findAll", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dscm.dom.asset.Asset "
                        + "ORDER BY name"),
        @javax.jdo.annotations.Query(name = "findByName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dscm.dom.asset.Asset "
                        + "WHERE name == :name"),
        @javax.jdo.annotations.Query(name = "findByDisplayGroup", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dscm.dom.Asset "
                        + "WHERE (displayGroup == null || displayGroup == :displayGroup)")
})
@Bookmarkable
@Immutable
@Bounded
public class Asset extends AbstractDomainObject implements Comparable<Asset> {

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

    private String description;

    @MemberOrder(sequence = "2")
    @Optional
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    // //////////////////////////////////////

    private LocalDate startDate;

    @Persistent
    @Column(allowsNull = "false")
    @MemberOrder(sequence = "3")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    // //////////////////////////////////////

    private LocalDate expiryDate;

    @Persistent
    @Optional
    @Column(allowsNull = "true")
    @MemberOrder(sequence = "4")
    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(final LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }   

    // //////////////////////////////////////

    private BigDecimal duration;

    @Optional
    @MemberOrder(sequence = "5")
    public BigDecimal getDuration() {
        return duration == null ? BigDecimal.ZERO : duration;
    }

    public void setDuration(BigDecimal duration) {
        this.duration = duration;
    }

    // //////////////////////////////////////

    private Publisher publisher;

    @Column(name = "publisherId", allowsNull = "false")
    @MemberOrder(sequence = "6")
    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    // //////////////////////////////////////

    private DisplayGroup displayGroup;

    @Optional
    @javax.jdo.annotations.Column(name = "displayGroupId", allowsNull = "true")
    public DisplayGroup getDisplayGroup() {
        return displayGroup;
    }

    public void setDisplayGroup(final DisplayGroup displayGroup) {
        this.displayGroup = displayGroup;
    }
        
    // //////////////////////////////////////
    
    public Blob download() {
        return getFile();
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(defaultFetchGroup = "false")
    private Blob file;

    @Optional
    @MemberOrder(sequence = "7")
    @Hidden
    public Blob getFile() {
        return file;
    }

    public void setFile(Blob file) {
        this.file = file;
    }

    // //////////////////////////////////////
    public Object remove(
            @Named("Are you sure?") Boolean confirm) {
        if (confirm) {
            doRemove();
            return newViewModelInstance(DscmDashboard.class, "dashboard");
        }
        return this;
    }

    public String disableRemove(Boolean confirm) {
        return playlistItems.findByAsset(this).isEmpty() ? null : "Asset is used in a playlist";
    }
    
    
    @Programmatic
    public void doRemove() {
        getContainer().remove(this);
        getContainer().flush();
    }

    // //////////////////////////////////////

    @Override
    public int compareTo(Asset other) {
        return ObjectContracts.compare(this, other, "name");
    }

    // //////////////////////////////////////
    
    @Inject
    private PlaylistItems playlistItems;
    
}
