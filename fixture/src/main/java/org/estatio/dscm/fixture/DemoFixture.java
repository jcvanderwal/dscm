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

package org.estatio.dscm.fixture;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixtures.AbstractFixture;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.objectstore.jdo.applib.service.support.IsisJdoSupport;

import org.estatio.dscm.dom.Asset;
import org.estatio.dscm.dom.Assets;
import org.estatio.dscm.dom.Display;
import org.estatio.dscm.dom.DisplayGroup;
import org.estatio.dscm.dom.DisplayGroups;
import org.estatio.dscm.dom.Displays;
import org.estatio.dscm.dom.Playlist;
import org.estatio.dscm.dom.PlaylistItems;
import org.estatio.dscm.dom.PlaylistRepeat;
import org.estatio.dscm.dom.Playlists;
import org.estatio.dscm.dom.Publisher;
import org.estatio.dscm.dom.Publishers;
import org.estatio.dscm.dom.Time;

public class DemoFixture extends AbstractFixture {

    @Override
    public void install() {
        isisJdoSupport.executeUpdate("delete from \"PlaylistItem\"");
        isisJdoSupport.executeUpdate("delete from \"Playlist\"");
        isisJdoSupport.executeUpdate("delete from \"Asset\"");
        isisJdoSupport.executeUpdate("delete from \"Display\"");
        isisJdoSupport.executeUpdate("delete from \"DisplayGroup\"");
        isisJdoSupport.executeUpdate("delete from \"Publisher\"");
        installObjects();
        getContainer().flush();
    }

    private void installObjects() {
        List<Publisher> publisherList = new ArrayList<Publisher>();
        publisherList.add(publishers.newPublisher("Eurocommercial Properties"));
        publisherList.add(publishers.newPublisher("Canal-Pub"));

        List<Asset> aList = new ArrayList<Asset>();
        aList.add(assets.newAsset("PDH General", publisherList.get(0), null, clockService.now(), null, null, new BigDecimal("20")));
        aList.add(assets.newAsset("PDH Activities", publisherList.get(0), null, clockService.now(), null, null, new BigDecimal("20")));
        aList.add(assets.newAsset("McDonalds spring deals", publisherList.get(1), null, clockService.now(), null, null, new BigDecimal("20")));
        aList.add(assets.newAsset("FNAC", publisherList.get(1), null, clockService.now(), null, null, new BigDecimal("20")));
        aList.add(assets.newAsset("Sephora", publisherList.get(1), null, clockService.now(), null, null, new BigDecimal("20")));
        aList.add(assets.newAsset("Celio", publisherList.get(1), null, clockService.now(), null, null, new BigDecimal("20")));

        DisplayGroup displayGroupT = createDisplayGroup("Tours");
        displayGroupT.setLocation("Les Atlantes, Tours, France");
        createDisplay("ds-tours01", displayGroupT);
        createDisplay("ds-tours02", displayGroupT);

        DisplayGroup displayGroupAms = createDisplayGroup("ECP Amsterdam");
        displayGroupT.setLocation("ECP Helpdesk, Amsterdam, Netherlands");
        createDisplay("ds-helpdesk01", displayGroupAms);

        DisplayGroup displayGroupPDH = createDisplayGroup("Passage Du Havre");
        displayGroupPDH.setLocation("Passage Du Havre, Paris, France");
        createDisplay("ds-pduhavre01", displayGroupPDH);
        createDisplay("ds-pduhavre02", displayGroupPDH);
        createDisplay("ds-pduhavre03", displayGroupPDH);
        createDisplay("ds-pduhavre04", displayGroupPDH);
        createDisplay("ds-pduhavre05", displayGroupPDH);

        DisplayGroup dg2 = createDisplayGroup("Les Atlantes, Tours");
        dg2.setLocation("Les Atlantes, Tours, France");

        Playlist p1 = playlists.newPlaylist(displayGroupAms, "Morning", new LocalDate(2014, 4, 14), Time.T0900, new LocalDate(2014, 5, 14), PlaylistRepeat.DAILY);

        for (Asset asset : aList) {
            playlistItems.newPlaylistItem(p1, asset);
        }

        Playlist p2 = playlists.newPlaylist(displayGroupAms, "Afternoon", new LocalDate(2014, 4, 14), Time.T1300, new LocalDate(2014, 5, 14), PlaylistRepeat.EVERY_WEEKDAY);

        for (Asset asset : aList) {
            playlistItems.newPlaylistItem(p2, asset);
        }
    }

    // //////////////////////////////////////

    private DisplayGroup createDisplayGroup(final String name) {
        return displaysGroups.newDisplayGroup(name);
    }

    private Display createDisplay(
            final String name,
            final DisplayGroup displayGroup) {
        return displays.newDisplay(name, displayGroup);
    }

    // //////////////////////////////////////
    // Injected services
    // //////////////////////////////////////

    @javax.inject.Inject
    private Displays displays;

    @javax.inject.Inject
    private DisplayGroups displaysGroups;

    @javax.inject.Inject
    private Publishers publishers;

    @javax.inject.Inject
    private Assets assets;

    @javax.inject.Inject
    private Playlists playlists;

    @javax.inject.Inject
    private PlaylistItems playlistItems;

    @javax.inject.Inject
    private IsisJdoSupport isisJdoSupport;

    @javax.inject.Inject
    private ClockService clockService;
}
