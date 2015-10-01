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
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Blob;

import org.estatio.dscm.EstatioDomainService;
import org.estatio.dscm.dom.display.DisplayGroup;
import org.estatio.dscm.dom.publisher.Publisher;

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

    @MemberOrder(sequence = "1")
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    public List<Asset> allAssets() {
        return getContainer().allMatches(
                new QueryDefault<Asset>(Asset.class, "findAll"));
    }

    // //////////////////////////////////////
    // Create (action)
    // //////////////////////////////////////

    @MemberOrder(sequence = "2")
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public Asset newAsset(
            final @ParameterLayout(named = "File") Blob file,
            final @Parameter(optionality = Optionality.OPTIONAL) @ParameterLayout(named = "Description") String description,
            final @ParameterLayout(named = "Publisher") Publisher publisher,
            final @ParameterLayout(named = "Display Group") DisplayGroup displayGroup,
            final @ParameterLayout(named = "Start date") LocalDate startDate,
            final @ParameterLayout(named = "Duration (seconds)") BigDecimal duration) {
        final Asset obj = container.newTransientInstance(Asset.class);
        obj.setName(file.getName());
        obj.setDescription(description);
        obj.setPublisher(publisher);
        obj.setDisplayGroup(displayGroup);
        obj.setStartDate(startDate);
        obj.setDuration(duration);
        obj.setFile(file);
        container.persistIfNotAlready(obj);
        return obj;
    }

    @Programmatic
    public LocalDate default3NewAsset() {
        return clockService.now();
    }

    @Programmatic
    public String validateNewAsset(
            final Blob file,
            final Publisher publisher,
            final DisplayGroup displayGroup,
            final LocalDate startDate,
            final BigDecimal duration) {
        if (findAssetByName(file.getName()) != null) {
            return "A file with this name is already uploaded.";
        }
        return null;
    }

    // //////////////////////////////////////

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
