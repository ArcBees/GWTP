/**
 * Copyright 2011 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.gwtplatform.dispatch.annotation;

import org.junit.Test;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;
import com.gwtplatform.dispatch.annotation.proxy.AddressProxy;
import com.gwtplatform.dispatch.annotation.proxy.EmployeeProxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This test is being run by ant, but is not run in eclipse.
 * <p/>
 * TODO: Make a test suite with a couple of permutations (with/without Order, Optional, both...).
 *
 * @author Brendan Doherty
 * @author Florian Sauter
 */
public class ProxyAnnotationProcessingTest {

    @Test
    public void shouldGenerateEntityProxy() throws SecurityException, NoSuchMethodException {
        // If the proxy exists and we can access the class element, the class type generation was successful.
        Class<?> proxyClass = EmployeeProxy.class;
        ProxyFor proxyAnnotation = EmployeeProxy.class.getAnnotation(ProxyFor.class);

        // Check if all expected methods have been generated.
        proxyClass.getMethod("getDisplayName");
        proxyClass.getMethod("getSupervisorKey");
        proxyClass.getMethod("getId");
        proxyClass.getMethod("getSupervisor");
        proxyClass.getMethod("setSupervisorKey", Long.class);
        // Due to the @UseProxyName annotation on the supervisor field, setSupervisor
        // should take the given proxy (instead of the origin domain type) as argument.
        proxyClass.getMethod("setSupervisor", EmployeeProxy.class);
        proxyClass.getMethod("setVersion", Integer.class);
        // Since we use a EntityProxy this method must be present.
        proxyClass.getMethod("stableId");

        // Assert that methods that should be filtered have not been generated.
        boolean filteredFieldsWereNotGenerated = false;
        try {
            proxyClass.getMethod("setId", Long.class);
            proxyClass.getMethod("getVersion", Long.class);
        } catch (NoSuchMethodException e) {
            filteredFieldsWereNotGenerated = true;
        }

        assertTrue(filteredFieldsWereNotGenerated);
        assertTrue(EntityProxy.class.isAssignableFrom(EmployeeProxy.class));
        assertEquals(proxyAnnotation.value(), Employee.class);
        assertEquals(proxyAnnotation.locator(), EmployeeLocator.class);
    }

    @Test
    public void shouldGenerateValueProxy() throws SecurityException, NoSuchMethodException {
        ProxyFor proxyAnnotation = AddressProxy.class.getAnnotation(ProxyFor.class);
        assertTrue(ValueProxy.class.isAssignableFrom(AddressProxy.class));
        assertEquals(proxyAnnotation.value(), Address.class);
    }

}
