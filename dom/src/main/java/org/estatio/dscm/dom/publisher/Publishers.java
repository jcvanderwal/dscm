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
package org.estatio.dscm.dom.publisher;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.query.QueryDefault;

import java.util.List;

@DomainService
public class Publishers extends AbstractFactoryAndRepository {

    public String getId() {
        return "publisher";
    }

    public String iconName() {
        return "Publisher";
    }

    @MemberOrder(sequence = "1")
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    public List<Publisher> allPublishers() {
        return getContainer().allInstances(Publisher.class);
    }

    // //////////////////////////////////////
    // Create (action)
    // //////////////////////////////////////

    @MemberOrder(sequence = "2")
    public Publisher newPublisher(
            final @ParameterLayout(named = "Name") String name) {
        final Publisher obj = getContainer().newTransientInstance(Publisher.class);
        obj.setName(name);
        getContainer().persistIfNotAlready(obj);
        return obj;
    }

    public boolean hideNewPublisher(final String name) {
        return !getContainer().getUser().hasRole(".*admin_role");
    }

    public Publisher findByName(@ParameterLayout(named = "Name") String name) {
        return firstMatch(new QueryDefault<Publisher>(Publisher.class, "findByName", "name", name));
    }

}
