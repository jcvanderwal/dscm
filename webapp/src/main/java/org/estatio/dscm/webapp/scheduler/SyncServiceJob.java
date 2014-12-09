package org.estatio.dscm.webapp.scheduler;

public class SyncServiceJob extends AbstractIsisQuartzJob {
    public SyncServiceJob() {
        super(new SyncServiceSession());
    }
}
