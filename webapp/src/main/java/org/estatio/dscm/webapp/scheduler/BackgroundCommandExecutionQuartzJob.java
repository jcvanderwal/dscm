package org.estatio.dscm.webapp.scheduler;

import org.isisaddons.module.command.dom.BackgroundCommandExecutionFromBackgroundCommandServiceJdo;

public class BackgroundCommandExecutionQuartzJob extends AbstractIsisQuartzJob {

    public BackgroundCommandExecutionQuartzJob() {
        super(new BackgroundCommandExecutionFromBackgroundCommandServiceJdo());   
    }
}