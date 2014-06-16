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

import javax.inject.Inject;

import org.estatio.dscm.dom.asset.Asset;
import org.estatio.dscm.dom.display.Display;
import org.estatio.dscm.dom.display.DisplayGroup;
import org.estatio.dscm.dom.playlist.Playlist;
import org.estatio.dscm.dom.playlist.PlaylistItem;
import org.estatio.dscm.dom.publisher.Publisher;

import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;
import org.apache.isis.objectstore.jdo.applib.service.support.IsisJdoSupport;

public class TeardownFixture extends DiscoverableFixtureScript {

    public TeardownFixture() {
        super(null, "teardown");
    }

    @Override
    protected void execute(ExecutionContext executionContext) {
        delete(PlaylistItem.class);
        delete(Playlist.class);
        delete(Display.class);
        delete(DisplayGroup.class);
        delete(Asset.class);
        delete(Publisher.class);
    }

    private void delete(Class cls) {
        isisJdoSupport.executeUpdate(String.format("DELETE FROM \"%s\"", cls.getSimpleName()));
    }

    @Inject
    private IsisJdoSupport isisJdoSupport;

}
