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
package org.estatio.dscm.dom.playlist;

import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dscm.EstatioDomainService;
import org.estatio.dscm.dom.asset.Asset;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class PlaylistItems extends EstatioDomainService<PlaylistItem> {

    public PlaylistItems() {
        super(PlaylistItems.class, PlaylistItem.class);
    }

    public String getId() {
        return "playlistItemItem";
    }

    public String iconName() {
        return "PlaylistItem";
    }

    @MemberOrder(sequence = "1")
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    public List<PlaylistItem> allPlaylistItems() {
        return getContainer().allInstances(PlaylistItem.class);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(named = "Playlists", contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<PlaylistItem> findByAsset(final Asset asset) {
        return allMatches("findByAsset", "asset", asset);
    }

    @Programmatic
    public List<PlaylistItem> findByPlaylist(Playlist playlist) {
        return allMatches("findByPlaylist", "playlist", playlist);
    }

    // //////////////////////////////////////

    @ActionLayout(contributed = Contributed.AS_NEITHER)
    public PlaylistItem newPlaylistItem(
            final Playlist playlist,
            final Asset asset) {
        final PlaylistItem obj = getContainer().newTransientInstance(PlaylistItem.class);
        obj.setPlaylist(playlist);
        obj.setAsset(asset);
        obj.setSequence(1);
        getContainer().persistIfNotAlready(obj);
        if (!playlist.getItems().isEmpty()) {
            final PlaylistItem last = playlist.getItems().last();
            last.setNext(obj);
            obj.setSequence(last.getSequence() + 1);
        }
        getContainer().flush();
        return obj;
    }

}
