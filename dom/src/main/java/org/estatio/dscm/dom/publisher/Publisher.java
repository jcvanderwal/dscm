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

import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.util.ObjectContracts;
import org.estatio.dscm.DSCMDomainObject;
import org.estatio.dscm.dom.asset.Asset;

import javax.inject.Inject;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.VersionStrategy;
import java.util.List;
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
@Query(name = "findByName", language = "JDOQL",
        value = "SELECT FROM org.estatio.dscm.dom.publisher.Publisher "
                + "WHERE name.matches(:name)")
@DomainObject(bounded = true, editing = Editing.DISABLED)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
public class Publisher extends DSCMDomainObject<Publisher> implements Comparable<Publisher> {

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

    @Persistent(mappedBy = "publisher")
    private SortedSet<Asset> assets = new TreeSet<Asset>();

    @MemberOrder(sequence = "1")
    @CollectionLayout(render = RenderType.EAGERLY)
    public SortedSet<Asset> getAssets() {
        return assets;
    }

    public void setAssets(final SortedSet<Asset> assets) {
        this.assets = assets;
    }


    public List<Publisher> remove(Publisher publisher, @ParameterLayout(named = "Are you sure?") Boolean confirm) {
        getContainer().remove(publisher);
        getContainer().flush();

        return publishers.allPublishers();
    }

    public boolean hideRemove(Publisher publisher, Boolean confirm) {
        return !getContainer().getUser().hasRole(".*admin_role");
    }

    // //////////////////////////////////////

    @Override
    public int compareTo(Publisher other) {
        return ObjectContracts.compare(this, other, "name");
    }

    @Inject
    Publishers publishers;
}
