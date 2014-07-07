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
package integration.tests.dom.display;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import integration.tests.DscmIntegTest;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.estatio.dscm.dom.display.Display;
import org.estatio.dscm.dom.display.DisplayGroups;
import org.estatio.dscm.dom.display.Displays;
import org.estatio.dscm.fixture.DemoFixture;

public class DisplaysTest_listAll_and_create extends DscmIntegTest {

    private Displays displays;
    private DisplayGroups displayGroups;

    @Before
    public void setUp() throws Exception {

        scenarioExecution().install(new DemoFixture());

        displays = wrap(service(Displays.class));
        displayGroups = wrap(service(DisplayGroups.class));
    }

    @Test
    public void step1_listAll() throws Exception {

        final List<Display> all = displays.allDisplays();
        assertThat(all.size(), is(3));

    }

    @Test
    public void step2_create() throws Exception {

        displays.newDisplay("Faz", displayGroups.allDisplayGroups().get(0));

        final List<Display> all = displays.allDisplays();
        assertThat(all.size(), is(4));
    }

}