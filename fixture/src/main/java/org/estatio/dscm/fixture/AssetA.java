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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.io.IOUtils;

import org.estatio.dscm.dom.asset.Assets;
import org.estatio.dscm.dom.publisher.Publishers;

import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Blob;

public class AssetA extends DiscoverableFixtureScript {

    protected static final String FILE_NAME = "hd_dolby_bit_harvest.m2ts";

    public AssetA() {
        super(null, "pdh");
    }

    @Override
    protected void execute(ExecutionContext executionContext) {

        execute(new PublisherA(), executionContext);

        assets.newAsset(
                resourceAsBlob(FILE_NAME),
                publishers.findByName(PublisherA.NAME),
                null,
                clockService.now(),
                null,
                new BigDecimal("20"));
    }

    private Blob resourceAsBlob(String fileName) {
        try {
            InputStream is;
            is = getClass().getResourceAsStream("/" + fileName);
            final String mimeType = new MimetypesFileTypeMap().getContentType(fileName);
            Blob blob = new Blob(fileName, mimeType, IOUtils.toByteArray(is));
            return blob;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    // //////////////////////////////////////

    @javax.inject.Inject
    private Publishers publishers;

    @javax.inject.Inject
    private Assets assets;

    @javax.inject.Inject
    private ClockService clockService;

}
