package org.estatio.dscm.webapp.scheduler;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.core.runtime.sessiontemplate.AbstractIsisSessionTemplate;
import org.apache.isis.core.runtime.system.transaction.TransactionalClosureAbstract;

import org.estatio.dscm.services.SyncService;

public class SyncServiceSession extends AbstractIsisSessionTemplate{
    
    @Inject
    SyncService syncService;
    
    private final static Logger logger = LoggerFactory
            .getLogger(SyncServiceSession.class);
    
    @Override
    protected void doExecute(Object context) {
        
         this.getTransactionManager(this.getPersistenceSession()).executeWithinTransaction(new
                   TransactionalClosureAbstract() {
                               @Override
                               public void execute() {
                                   scheduleSync();
                               }
                           });
    }

    public void scheduleSync() {
        syncService.synchronizeNowScheduled();
    }
}
