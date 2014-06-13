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
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Blob;

import org.estatio.dscm.EstatioDomainService;

@DomainService
public class Assets extends EstatioDomainService<Asset> {

    public Assets() {
        super(Assets.class, Asset.class);
    }

    public String getId() {
        return "asset";
    }

    public String iconName() {
        return "Asset";
    }

    @Bookmarkable
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public List<Asset> allAssets() {
        return allInstances(Asset.class);
    }

    // //////////////////////////////////////
    // Create (action)
    // //////////////////////////////////////

    @MemberOrder(sequence = "2")
    public Asset newAsset(
            final @Named("File") Blob file,
            final Publisher publisher,
            final DisplayGroup displayGroup,
            final @Named("Start date") LocalDate startDate,
            final @Named("Expiry date") @Optional LocalDate expiryDate,
            final @Named("Duration (seconds)") BigDecimal duration) {
        final Asset obj = container.newTransientInstance(Asset.class);
        obj.setName(file.getName());
        obj.setDuration(duration);
        obj.setPublisher(publisher);
        obj.setStartDate(startDate);
        obj.setExpiryDate(expiryDate);
        obj.setFile(file);
        container.persistIfNotAlready(obj);
        return obj;
    }

    public LocalDate default3NewAsset() {
        return clockService.now();
    }

    @Programmatic
    public Asset findAssetByName(final String name) {
        return firstMatch("findByName", "name", name);
    }

    @Programmatic
    public List<Asset> findAssetByDisplaygroup(final DisplayGroup displayGroup) {
        return allMatches("findByDisplayGroup", "displayGroup", displayGroup);
    }

    // //////////////////////////////////////
    // Injected services
    // //////////////////////////////////////

    @javax.inject.Inject
    DomainObjectContainer container;

    @javax.inject.Inject
    ClockService clockService;
}
