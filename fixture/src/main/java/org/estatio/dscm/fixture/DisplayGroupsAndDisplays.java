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

import org.estatio.dscm.dom.display.DisplayGroup;
import org.estatio.dscm.dom.display.DisplayGroups;
import org.estatio.dscm.dom.display.Displays;

import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;

public class DisplayGroupsAndDisplays extends DiscoverableFixtureScript {

    public DisplayGroupsAndDisplays() {
        super(null, "pdh");
    }

    @Override
    protected void execute(ExecutionContext executionContext) {
        createDisplayAndDisplayGroups("Passage Du Havre, Paris", "ds-pduhavre", 3);
    }

    // //////////////////////////////////////

    private void createDisplayAndDisplayGroups(final String name, final String prefix, final int count) {
        DisplayGroup dg = displaysGroups.newDisplayGroup(name);
        for (int i = 0; i < count; i++) {
            displays.newDisplay(prefix.concat(String.format("%02d", count)), dg);
        }
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    private Displays displays;

    @javax.inject.Inject
    private DisplayGroups displaysGroups;

}
