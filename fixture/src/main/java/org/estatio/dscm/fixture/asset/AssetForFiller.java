package org.estatio.dscm.fixture.asset;

import org.estatio.dscm.fixture.publisher.PublisherForFillers;

public class AssetForFiller extends AssetAbstract {

    public static final String NAME = "hd_distributor_stage_6-DWEU.m2ts";

    @Override
    protected void execute(ExecutionContext executionContext) {
        execute(new PublisherForFillers(), executionContext);
        createAsset(NAME, PublisherForFillers.NAME);
    }

}
