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
package org.estatio.dscm.dom.display;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class DisplayGroupTest_compareTo {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void happyCase() throws Exception {
        DisplayGroup a = new DisplayGroup();
        a.setName("a");
        DisplayGroup b = new DisplayGroup();
        b.setName("b");
        assertThat(a.compareTo(b), is(-1));
        assertThat(b.compareTo(a), is(1));
        assertThat(a.compareTo(a), is(0));
    }

}
