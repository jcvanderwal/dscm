package org.estatio.dscm.fixture.asset;

import org.estatio.dscm.fixture.publisher.PublisherForFillers;

public class AssetForFiller extends AssetAbstract {

    public static final String NAME = "hd_distributor_stage_6-DWEU.m2ts";
    public static final String NAME2 = "hd_distributor_ems-DWEU.m2ts";
    public static final String NAME3 = "hd_distributor_walden_media-DWEU.m2ts";

    @Override
    protected void execute(ExecutionContext executionContext) {
        execute(new PublisherForFillers(), executionContext);
        createAsset(NAME, PublisherForFillers.NAME);
        createAsset(NAME2, PublisherForFillers.NAME);
        createAsset(NAME3, PublisherForFillers.NAME);
    }

}
