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
package org.estatio.dscm.fixture;

import java.util.List;

import javax.inject.Inject;

import org.estatio.dscm.dom.asset.Assets;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.fixturescripts.FixtureResult;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;

@DomainService(menuOrder = "93")
public class DscmFixtureScripts extends FixtureScripts {

    public DscmFixtureScripts() {
        super("org.estatio.dscm");
    }

    @MemberOrder(name = "Administration", sequence = "9")
    @Override
    public List<FixtureResult> runFixtureScript(
            final FixtureScript fixtureScript,
            final String parameters) {
        return super.runFixtureScript(fixtureScript, parameters);
    }

    @Override
    public List<FixtureScript> choices0RunFixtureScript() {
        return super.choices0RunFixtureScript();
    }

    // //////////////////////////////////////

    @Prototype
    @MemberOrder(name = "Administration", sequence = "3")
    public List<FixtureResult> installDemoFixtures() {
        return runFixtureScript(new DemoFixture(), null);
    }

    public String disableInstallDemoFixtures() {
        return !assets.allAssets().isEmpty() ? "Demo fixtures already installed" : null;
    }

    // //////////////////////////////////////

    @Inject
    Assets assets;

}
