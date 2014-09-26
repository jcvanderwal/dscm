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

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.estatio.dscm.dom.display.Display;
import org.estatio.dscm.dom.display.DisplayGroups;
import org.estatio.dscm.dom.display.Displays;
import org.estatio.dscm.fixture.DemoFixture;
import org.estatio.dscm.integtests.DscmIntegTest;

public class DisplaysTest extends DscmIntegTest {

    @Inject
    Displays displays;

    @Inject
    DisplayGroups displayGroups;

    @Before
    public void setUp() throws Exception {
        scenarioExecution().install(new DemoFixture());
    }

    public static class AllDisplays extends DisplaysTest {
        @Test
        public void sizeIsSix() throws Exception {
            final List<Display> all = displays.allDisplays();
            assertThat(all.size(), is(6));
        }
    }

    public static class NewDisplay extends DisplaysTest {

        private static final String FAZ = "Faz";

        @Test
        public void createSucceeds() throws Exception {
            displays.newDisplay(FAZ, displayGroups.allDisplayGroups().get(0));
            final List<Display> all = displays.allDisplays();
            assertThat(all.size(), is(7));
        }
    }

}