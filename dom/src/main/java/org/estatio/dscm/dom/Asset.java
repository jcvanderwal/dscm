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

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.value.Blob;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Bookmarkable
public class Asset implements Comparable<Asset> {

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

    private LocalDate startDate;

    @Persistent
    @Column(allowsNull = "false")
    @MemberOrder(sequence = "2")
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
    @MemberOrder(sequence = "3")
    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(final LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    // //////////////////////////////////////

    private BigDecimal duration;

    @Optional
    @MemberOrder(sequence = "4")
    public BigDecimal getDuration() {
        return duration;
    }

    public void setDuration(BigDecimal duration) {
        this.duration = duration;
    }

    // //////////////////////////////////////

    private Publisher publisher;

    @Column(name = "publisherId", allowsNull = "false")
    @MemberOrder(sequence = "5")
    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(defaultFetchGroup = "false")
    private Blob file;

    @Optional
    @MemberOrder(sequence = "6")
    @Hidden(where = Where.ALL_TABLES)
    public Blob getFile() {
        return file;
    }

    public void setFile(Blob file) {
        this.file = file;
    }

    // //////////////////////////////////////

    @Override
    public int compareTo(Asset other) {
        return ObjectContracts.compare(this, other, "name");
    }

    @javax.inject.Inject
    @SuppressWarnings("unused")
    private DomainObjectContainer container;

}
