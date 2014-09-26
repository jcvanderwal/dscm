/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
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
package org.estatio.dscm.integtests.dom.asset;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.estatio.dscm.dom.asset.Assets;
import org.estatio.dscm.fixture.DemoFixture;
import org.estatio.dscm.integtests.DscmIntegTest;

@Ignore("Causing (so far) inexplicable trouble with heap space on Jenkins, preventing deployment. See DSCM-20")
public class AssetsTest extends DscmIntegTest {

    @Inject
    private Assets assets;

    @BeforeClass
    public static void setupTransactionalData() {
        scenarioExecution().install(new DemoFixture());

    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void findAssets_byReference() throws Exception {
        assertThat(assets.findAssetByName("hd_dolby_bit_harvest.m2ts").getName(), is("hd_dolby_bit_harvest.m2ts"));

    }

    @Test
    public void findAll() throws Exception {
        assertThat(assets.allAssets().size(), is(4));
    }

}
