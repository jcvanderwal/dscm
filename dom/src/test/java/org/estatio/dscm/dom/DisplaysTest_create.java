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
package org.estatio.dscm.dom;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dscm.dom.Display;
import org.estatio.dscm.dom.Displays;

public class DisplaysTest_create {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private DomainObjectContainer mockContainer;
    
    private Displays simpleObjects;

    @Before
    public void setUp() throws Exception {
        simpleObjects = new Displays();
        simpleObjects.container = mockContainer;
    }
    
    @Test
    public void happyCase() throws Exception {
        
        // given
        final Display simpleObject = new Display();
        final DisplayGroup displayGroup = new DisplayGroup();
        
        final Sequence seq = context.sequence("create");
        context.checking(new Expectations() {
            {
                oneOf(mockContainer).newTransientInstance(Display.class);
                inSequence(seq);
                will(returnValue(simpleObject));
                
                oneOf(mockContainer).persistIfNotAlready(simpleObject);
                inSequence(seq);
            }
        });
        
        // when
        final Display obj = simpleObjects.newDisplay("Foobar", displayGroup);
        
        // then
        assertThat(obj, is(simpleObject));
        assertThat(obj.getName(), is("Foobar"));
    }

}
