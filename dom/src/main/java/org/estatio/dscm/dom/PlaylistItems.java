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
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dscm.EstatioDomainService;

@Hidden
public class PlaylistItems extends EstatioDomainService<PlaylistItem> {

    public PlaylistItems() {
        super(PlaylistItems.class, PlaylistItem.class);
    }

    public String getId() {
        return "PlaylistItemItem";
    }

    public String iconName() {
        return "PlaylistItem";
    }

    @Bookmarkable
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public List<PlaylistItem> allPlaylistItems() {
        return container.allInstances(PlaylistItem.class);
    }

    // //////////////////////////////////////

    @Programmatic
    public List<PlaylistItem> findByAsset(final Asset asset) {
        return allMatches("findByAsset", "asset", asset);
    }

    // //////////////////////////////////////
    // Create (action)
    // //////////////////////////////////////

    @NotContributed
    public PlaylistItem newPlaylistItem(
            final Playlist playlist,
            final Asset asset) {
        final PlaylistItem obj = container.newTransientInstance(PlaylistItem.class);
        obj.setPlaylist(playlist);
        obj.setAsset(asset);
        obj.setSequence(1);
        container.persistIfNotAlready(obj);
        if (!playlist.getItems().isEmpty()) {
            final PlaylistItem last = playlist.getItems().last();
            last.setNext(obj);
            obj.setSequence(last.getSequence() + 1);
        }
        container.flush();
        return obj;
    }

    // //////////////////////////////////////
    // Injected services
    // //////////////////////////////////////

    @javax.inject.Inject
    DomainObjectContainer container;

}
