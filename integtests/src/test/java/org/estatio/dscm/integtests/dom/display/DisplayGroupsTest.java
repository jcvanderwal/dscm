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
package org.estatio.dscm.integtests.dom.display;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.estatio.dscm.dom.display.DisplayGroup;
import org.estatio.dscm.dom.display.DisplayGroups;
import org.estatio.dscm.fixture.DemoFixture;
import org.estatio.dscm.fixture.display.DisplayGroupsAndDisplays;
import org.estatio.dscm.integtests.DscmIntegTest;

import org.junit.Before;
import org.junit.Test;

public class DisplayGroupsTest extends DscmIntegTest {

    private DisplayGroups displayGroups;

    @Before
    public void setUp() throws Exception {

        scenarioExecution().install(new DemoFixture());

        displayGroups = wrap(service(DisplayGroups.class));
    }

    @Test
    public void step1_listAll() throws Exception {

        List<DisplayGroup> groups = displayGroups.allDisplayGroups();
        assertThat(groups.get(0).getName(), is(DisplayGroupsAndDisplays.DEMO_AMSTERDAM));

    }

}