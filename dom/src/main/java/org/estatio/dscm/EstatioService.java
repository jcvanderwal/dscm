/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
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
package org.estatio.dscm;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.isis.applib.AbstractService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.bookmark.BookmarkService;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.eventbus.EventBusService;
import org.apache.isis.applib.services.memento.MementoService;

import org.estatio.dscm.utils.StringExtensions;

public abstract class EstatioService<T> extends AbstractService {

    private final Class<? extends EstatioService<T>> serviceType;

    protected EstatioService(final Class<? extends EstatioService<T>> serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * Domain services ARE automatically registered with the
     * {@link EventBusService};
     * Isis guarantees that there will be an instance of each domain service in
     * memory when events are {@link EventBusService#post(Object) post}ed.
     */
    @Programmatic
    @PostConstruct
    public void init(final Map<String, String> properties) {
        getEventBusService().register(this);
    }

    @Programmatic
    @PreDestroy
    public void shutdown() {
        getEventBusService().unregister(this);
    }

    @Override
    public String getId() {
        // eg "agreementRoles";
        return StringExtensions.asCamelLowerFirst(serviceType.getSimpleName());
    }

    public String iconName() {
        // eg "AgreementRole";
        return serviceType.getSimpleName();
    }

    // //////////////////////////////////////

    protected Class<? extends EstatioService<T>> getServiceType() {
        return serviceType;
    }

    // //////////////////////////////////////

    @Inject
    private ClockService clockService;

    @Programmatic
    public ClockService getClockService() {
        return clockService;
    }

    @Inject
    private EventBusService eventBusService;

    @Programmatic
    protected EventBusService getEventBusService() {
        return eventBusService;
    }

    @Inject
    private MementoService mementoService;

    @Programmatic
    protected MementoService getMementoService() {
        return mementoService;
    }

    @Inject
    private BookmarkService bookmarkService;

    @Programmatic
    protected BookmarkService getBookmarkService() {
        return bookmarkService;
    }

}
