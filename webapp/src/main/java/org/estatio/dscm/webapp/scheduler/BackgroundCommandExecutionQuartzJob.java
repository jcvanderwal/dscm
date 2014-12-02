package org.estatio.dscm.webapp.scheduler;

import org.apache.isis.core.runtime.sessiontemplate.AbstractIsisSessionTemplate;

public class BackgroundCommandExecutionQuartzJob extends AbstractIsisQuartzJob {

    public BackgroundCommandExecutionQuartzJob() {
        super(new BackgroundCommandExecutionFromBackgroundCommandServiceJdo());   
    }
}