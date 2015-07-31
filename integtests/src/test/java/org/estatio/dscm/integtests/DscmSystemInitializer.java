/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.estatio.dscm.integtests;

import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.commons.config.IsisConfigurationDefault;
import org.apache.isis.core.integtestsupport.IsisSystemForTest;
import org.apache.isis.objectstore.jdo.applib.service.exceprecog.ExceptionRecognizerCompositeForJdoObjectStore;
import org.apache.isis.objectstore.jdo.datanucleus.DataNucleusObjectStore;
import org.apache.isis.objectstore.jdo.datanucleus.DataNucleusPersistenceMechanismInstaller;
import org.apache.isis.objectstore.jdo.datanucleus.IsisConfigurationForJdoIntegTests;
import org.apache.isis.objectstore.jdo.datanucleus.service.eventbus.EventBusServiceJdo;
import org.apache.isis.objectstore.jdo.datanucleus.service.support.IsisJdoSupportImpl;
import org.apache.log4j.Level;

/**
 * Holds an instance of an {@link IsisSystemForTest} as a {@link ThreadLocal} on
 * the current thread, initialized with ToDo app's domain services.
 */
public class DscmSystemInitializer {

    private DscmSystemInitializer() {
    }

    public static IsisSystemForTest initIsft() {
        IsisSystemForTest isft = IsisSystemForTest.getElseNull();
        if (isft == null) {
            isft = new SimpleAppSystemBuilder().build().setUpSystem();
            IsisSystemForTest.set(isft);
        }
        return isft;
    }

    private static class SimpleAppSystemBuilder extends IsisSystemForTest.Builder {

//        public SimpleAppSystemBuilder() {
//            withLoggingAt(org.apache.log4j.Level.INFO);
//            with(testConfiguration());
//            with(new DataNucleusPersistenceMechanismInstaller());
//
//            withServicesIn("org.estatio"
//                    , "org.apache.isis.core.wrapper"
//                    , "org.apache.isis.applib"
//                    , "org.apache.isis.core.metamodel.services"
//                    , "org.apache.isis.core.runtime.services");
//
//            withServices(
//                    new IsisJdoSupportImpl(),
//                    new EventBusServiceJdo());
//        }
//
//        private IsisConfiguration testConfiguration() {
//            final IsisConfigurationDefault testConfiguration = new IsisConfigurationDefault();
//            testConfiguration.add("isis.persistor.datanucleus.RegisterEntities.packagePrefix", "dom");
//            testConfiguration.add("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionURL", "jdbc:hsqldb:mem:test");
//            testConfiguration.add("isis.persistor.datanucleus.impl.datanucleus.defaultInheritanceStrategy", "TABLE_PER_CLASS");
//            testConfiguration.add(DataNucleusObjectStore.INSTALL_FIXTURES_KEY, "true");
//            testConfiguration.add("isis.persistor.datanucleus.impl.datanucleus.cache.level2.type", "none");
//            testConfiguration.add("isis.persistor.datanucleus.impl.datanucleus.identifier.case", "PreserveCase");
//            return testConfiguration;
//        }

        public SimpleAppSystemBuilder() {

            // no need to add, because each test will set up its own test fixtures
            // anyway.
            withLoggingAt(Level.DEBUG);
            with(testConfiguration());
            with(new DataNucleusPersistenceMechanismInstaller());

            withServicesIn(
                    "org.estatio",
                    "org.isisaddons"
            );

//            withServices(
//                    new ExceptionRecognizerCompositeForJdoObjectStore());
        }

        private static IsisConfiguration testConfiguration() {
            final IsisConfigurationForJdoIntegTests testConfiguration = new IsisConfigurationForJdoIntegTests();
            testConfiguration.addRegisterEntitiesPackagePrefix("org.estatio");

            // uncomment to use log4jdbc instead
            // testConfiguration.put("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionDriverName",
            // "net.sf.log4jdbc.DriverSpy");

            // testConfiguration.put("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionURL",
            // "jdbc:hsqldb:mem:test;sqllog=3");

            //
            // testConfiguration.put("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionURL",
            // "jdbc:sqlserver://localhost:1433;instance=.;databaseName=estatio");
            // testConfiguration.put("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionDriverName",
            // "com.microsoft.sqlserver.jdbc.SQLServerDriver");
            // testConfiguration.put("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionUserName",
            // "estatio");
            // testConfiguration.put("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionPassword",
            // "estatio");

            return testConfiguration;
        }

    }

}
