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

import org.estatio.dscm.DomainObject;

import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.util.ObjectContracts;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByAsset", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dscm.dom.PlaylistItem "
                        + "WHERE asset == :asset"),
        @javax.jdo.annotations.Query(
                name = "findByPlaylist", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dscm.dom.PlaylistItem "
                        + "WHERE playlist == :playlist")
})
@Bookmarkable
@Immutable
public class PlaylistItem
        extends DomainObject<PlaylistItem>
        implements Comparable<PlaylistItem> {

    private Playlist playlist;

    @Column(name = "playlistId", allowsNull = "false")
    @Hidden(where = Where.PARENTED_TABLES)
    @MemberOrder(sequence = "1")
    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(final Playlist playlist) {
        this.playlist = playlist;
    }

    // //////////////////////////////////////

    private int sequence;

    @MemberOrder(sequence = "2")
    @Title(sequence = "1")
    public int getSequence() {
        return sequence;
    }

    public void setSequence(final int sequence) {
        this.sequence = sequence;
    }

    // //////////////////////////////////////

    private Asset asset;

    @Column(name = "assetId", allowsNull = "false")
    @Title(sequence = "2", prepend = ". ")
    @MemberOrder(sequence = "3")
    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    // //////////////////////////////////////

    @MemberOrder(sequence = "3.5")
    public BigDecimal getDuration() {
        return getAsset().getDuration();
    }

    // //////////////////////////////////////

    private PlaylistItem previous;

    @Persistent(mappedBy = "next")
    @MemberOrder(sequence = "4")
    @Hidden(where = Where.ALL_TABLES)
    @Optional
    public PlaylistItem getPrevious() {
        return previous;
    }

    public void setPrevious(final PlaylistItem previous) {
        this.previous = previous;
    }

    // //////////////////////////////////////

    private PlaylistItem next;

    @Column(name = "nextPlaylistItemId", allowsNull = "true")
    @MemberOrder(sequence = "5")
    @Hidden(where = Where.ALL_TABLES)
    public PlaylistItem getNext() {
        return next;
    }

    public void setNext(final PlaylistItem next) {
        this.next = next;
    }

    // //////////////////////////////////////

    public Object remove(
            @Named("Are you sure?") Boolean confirm) {
        if (confirm) {
            Playlist pl = getPlaylist();
            doRemove();
            return pl;
        }
        return this;
    }

    protected void doRemove() {
        if (getPrevious() != null) {
            getPrevious().setNext(getNext());
        }
        getContainer().remove(this);
        getContainer().flush();
    }

    // //////////////////////////////////////

    public PlaylistItem moveUp() {
        swap(getPrevious(), this);
        return this;
    }

    public String disableMoveUp() {
        return getPrevious() == null ? "This is the first item" : null;
    }

    public PlaylistItem moveDown() {
        swap(this, getNext());
        return this;
    }

    public String disableMoveDown() {
        return getNext() == null ? "This is the last item" : null;
    }

    private void swap(PlaylistItem first, PlaylistItem second) {
        PlaylistItem oldPrevious = first.getPrevious();
        PlaylistItem oldNext = second.getNext();
        final int firstSequence = first.getSequence();
        final int secondSequence = second.getSequence();
        second.setPrevious(oldPrevious);
        second.setNext(first);
        second.setSequence(firstSequence);
        first.setNext(oldNext);
        first.setSequence(secondSequence);
    }

    // //////////////////////////////////////

    @Override
    public int compareTo(PlaylistItem other) {
        return ObjectContracts.compare(this, other, "playlist,sequence,asset");
    }
}
