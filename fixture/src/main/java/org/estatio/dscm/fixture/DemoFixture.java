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

import org.apache.isis.applib.fixtures.AbstractFixture;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.objectstore.jdo.applib.service.support.IsisJdoSupport;

import org.estatio.dscm.dom.Assets;
import org.estatio.dscm.dom.Display;
import org.estatio.dscm.dom.DisplayGroup;
import org.estatio.dscm.dom.DisplayGroups;
import org.estatio.dscm.dom.Displays;
import org.estatio.dscm.dom.Publisher;
import org.estatio.dscm.dom.Publishers;

public class DemoFixture extends AbstractFixture {

    @Override
    public void install() {

        isisJdoSupport.executeUpdate("delete from \"Display\"");
        isisJdoSupport.executeUpdate("delete from \"DisplayGroup\"");

        installObjects();

        getContainer().flush();
    }

    private void installObjects() {

        Publisher p1 = publishers.newPublisher("Eurocommercial Properties");
        Publisher p2 = publishers.newPublisher("Some French Publisher");

        assets.newAsset("PDH General", new BigDecimal("20"), p1, clockService.now(), null, null);
        assets.newAsset("PDH Activities", new BigDecimal("20"), p1, clockService.now(), null, null);
        assets.newAsset("McDonalds spring deals", new BigDecimal("20"), p2, clockService.now(), null, null);
        assets.newAsset("FNAC", new BigDecimal("20"), p2, clockService.now(), null, null);
        assets.newAsset("Sephora", new BigDecimal("20"), p2, clockService.now(), null, null);
        assets.newAsset("Celio", new BigDecimal("20"), p2, clockService.now(), null, null);
        
        DisplayGroup displayGroup = createDisplayGroup("Passage Du Havre");
        displayGroup.setLocation("Passage Du Havre, Paris, France");
        createDisplay("Display 1", displayGroup);
        createDisplay("Display 2", displayGroup);
        createDisplay("Display 3", displayGroup);
        createDisplay("Display 4", displayGroup);

        DisplayGroup dg2 = createDisplayGroup("Les Atlantes, Tours");
        dg2.setLocation("Les Atlantes, Tours, France");
        
    }

    // //////////////////////////////////////

    private DisplayGroup createDisplayGroup(final String name){
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
    private IsisJdoSupport isisJdoSupport;

    @javax.inject.Inject
    private ClockService clockService;
}
