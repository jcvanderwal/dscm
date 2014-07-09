package org.estatio.dscm.fixture.asset;

import org.estatio.dscm.fixture.publisher.PublisherForCommercials;

public class AssetForCommercial extends AssetAbstract {

    public static final String NAME = "hd_dolby_bit_harvest.m2ts";

    @Override
    protected void execute(ExecutionContext executionContext) {
        execute(new PublisherForCommercials(), executionContext);
        createAsset(NAME, PublisherForCommercials.NAME);
    }

}
