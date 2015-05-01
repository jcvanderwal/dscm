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

import org.apache.isis.applib.AbstractService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.bookmark.BookmarkService;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.eventbus.EventBusService;
import org.apache.isis.applib.services.memento.MementoService;
import org.estatio.dscm.utils.StringExtensions;

import javax.inject.Inject;

public abstract class EstatioService<T> extends AbstractService {

    private final Class<? extends EstatioService<T>> serviceType;

    protected EstatioService(final Class<? extends EstatioService<T>> serviceType) {
        this.serviceType = serviceType;
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

    private ClockService clockService;

    protected ClockService getClockService() {
        return clockService;
    }

    public void injectClockService(final ClockService clockService) {
        this.clockService = clockService;
    }

    /**
     * a default value is used to prevent null pointers for objects being
     * initialized where the service has not yet been injected into.
     */
    @Inject
    private EventBusService eventBusService;

    protected EventBusService getEventBusService() {
        return eventBusService;
    }

    /**
     * Unlike domain objects, domain services ARE automatically registered with
     * the {@link EventBusService}; Isis guarantees that there will be an
     * instance of each domain service in memory when events are
     * {@link EventBusService#post(Object) post}ed.
     */

    private MementoService mementoService;

    protected MementoService getMementoService() {
        return mementoService;
    }

    final public void injectMementoService(final MementoService mementoService) {
        this.mementoService = mementoService;
    }

    private BookmarkService bookmarkService;

    protected BookmarkService getBookmarkService() {
        return bookmarkService;
    }

    public final void injectBookmarkService(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

}
