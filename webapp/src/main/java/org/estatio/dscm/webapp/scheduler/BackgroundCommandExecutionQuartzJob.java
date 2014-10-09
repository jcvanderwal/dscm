package org.estatio.dscm.webapp.scheduler;

import org.apache.isis.objectstore.jdo.service.BackgroundCommandExecutionFromBackgroundCommandServiceJdo;

public class BackgroundCommandExecutionQuartzJob extends AbstractIsisQuartzJob {

    public BackgroundCommandExecutionQuartzJob() {
        super(new BackgroundCommandExecutionFromBackgroundCommandServiceJdo());

    }

}
