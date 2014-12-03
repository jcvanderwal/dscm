package org.estatio.dscm.webapp.scheduler;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import org.apache.isis.core.commons.authentication.AuthenticationSession;
import org.apache.isis.core.runtime.authentication.standard.SimpleSession;
import org.apache.isis.core.runtime.sessiontemplate.AbstractIsisSessionTemplate;
import org.apache.isis.core.runtime.system.session.IsisSession;

public class AbstractIsisQuartzJob implements Job {

    public static enum ConcurrentInstancesPolicy {
        SINGLE_INSTANCE_ONLY,
        MULTIPLE_INSTANCES
    }
    
    private final AbstractIsisSessionTemplate isisRunnable;

    private final ConcurrentInstancesPolicy concurrentInstancesPolicy;
    private boolean executing;

    public AbstractIsisQuartzJob(AbstractIsisSessionTemplate isisRunnable) {
        this(isisRunnable, ConcurrentInstancesPolicy.SINGLE_INSTANCE_ONLY);
    }
    public AbstractIsisQuartzJob(
            AbstractIsisSessionTemplate isisRunnable, 
            ConcurrentInstancesPolicy concurrentInstancesPolicy) {
        this.isisRunnable = isisRunnable;
        this.concurrentInstancesPolicy = concurrentInstancesPolicy;
    }

    /**
     * Sets up an {@link IsisSession} then delegates to the 
     * {@link #doExecute(JobExecutionContext) hook}. 
     */
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        final AuthenticationSession authSession = newAuthSession(context);
        try {
            if(concurrentInstancesPolicy == 
                   ConcurrentInstancesPolicy.SINGLE_INSTANCE_ONLY && 
               executing) {
                return;
            }
            executing = true;

            isisRunnable.execute(authSession, context);
        } finally {
            executing = false;
        }
    }

    AuthenticationSession newAuthSession(JobExecutionContext context) {
        String user = getKey(context, SchedulerConstants.USER_KEY);
        String rolesStr = getKey(context, SchedulerConstants.ROLES_KEY);
        String[] roles = Iterables.toArray(
                Splitter.on(",").split(rolesStr), String.class);
        return new SimpleSession(user, roles);
    }

    String getKey(JobExecutionContext context, String key) {
        return context.getMergedJobDataMap().getString(key);
    }
}