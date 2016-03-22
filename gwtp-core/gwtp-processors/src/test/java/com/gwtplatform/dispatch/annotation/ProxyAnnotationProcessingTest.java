/*
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

import org.junit.Test;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;
import com.gwtplatform.dispatch.annotation.proxy.AddressProxy;
import com.gwtplatform.dispatch.annotation.proxy.EmployeeProxy;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * This test is being run by ant, but is not run in eclipse.
 * <p/>
 */
public class ProxyAnnotationProcessingTest {

    @Test
    public void shouldGenerateEntityProxy() throws NoSuchMethodException {
        // If the proxy exists and we can access the class element, the class type generation was successful.
        Class<?> proxyClass = EmployeeProxy.class;

        // Check if all expected methods have been generated.
        assertNotNull(proxyClass.getMethod("getDisplayName"));
        assertNotNull(proxyClass.getMethod("getSupervisorKey"));
        assertNotNull(proxyClass.getMethod("getId"));
        assertNotNull(proxyClass.getMethod("getSupervisor"));
        assertNotNull(proxyClass.getMethod("setSupervisorKey", Long.class));
        // Due to the @UseProxyName annotation on the supervisor field, setSupervisor
        // should take the given proxy (instead of the origin domain type) as argument.
        assertNotNull(proxyClass.getMethod("setSupervisor", EmployeeProxy.class));
        assertNotNull(proxyClass.getMethod("setVersion", Integer.class));
        // Since we use a EntityProxy this method must be present.
        assertNotNull(proxyClass.getMethod("stableId"));

        assertTrue(EntityProxy.class.isAssignableFrom(EmployeeProxy.class));

        ProxyFor proxyAnnotation = EmployeeProxy.class.getAnnotation(ProxyFor.class);
        assertEquals(proxyAnnotation.value(), Employee.class);
        assertEquals(proxyAnnotation.locator(), EmployeeLocator.class);
    }

    @Test(expected = NoSuchMethodException.class)
    public void shouldNotGenerateFilteredMethods() throws NoSuchMethodException {
        Class<?> proxyClass = EmployeeProxy.class;

        // Assert that methods that should be filtered have not been generated.
        proxyClass.getMethod("setId", Long.class);
        proxyClass.getMethod("getVersion", Long.class);
    }

    @Test
    public void shouldGenerateArrayListProxy() throws NoSuchMethodException {
        // If the proxy exists and we can access the class element, the class type generation was successful.
        Class<?> proxyClass = EmployeeProxy.class;

        // Check if all methods for details parameter have been generated
        Method getDetailsMethod = proxyClass.getMethod("getDetails");
        assertNotNull(getDetailsMethod);
        Class<?> returnType = getDetailsMethod.getReturnType();
        assertNotNull(returnType);
        assertTrue(returnType.isAssignableFrom(ArrayList.class));

        Method setDetailsMethod = proxyClass.getMethod("setDetails", ArrayList.class);
        assertNotNull(setDetailsMethod);
        Class<?>[] parameterTypes = setDetailsMethod.getParameterTypes();
        assertNotNull(parameterTypes);
        assertThat(parameterTypes.length, equalTo(1));
        assertTrue(parameterTypes[0].isAssignableFrom(ArrayList.class));
    }

    @Test
    public void shouldGenerateSetProxy() throws NoSuchMethodException {
        // If the proxy exists and we can access the class element, the class type generation was successful.
        Class<?> proxyClass = PersonProxy.class;

        // Check if all methods for details parameter have been generated
        Method getDetailsMethod = proxyClass.getMethod("getDetails");
        assertNotNull(getDetailsMethod);
        Class<?> returnType = getDetailsMethod.getReturnType();
        assertNotNull(returnType);
        assertTrue(returnType.isAssignableFrom(Set.class));

        Method setDetailsMethod = proxyClass.getMethod("setDetails", Set.class);
        assertNotNull(setDetailsMethod);
        Class<?>[] parameterTypes = setDetailsMethod.getParameterTypes();
        assertNotNull(parameterTypes);
        assertThat(parameterTypes.length, equalTo(1));
        assertTrue(parameterTypes[0].isAssignableFrom(Set.class));
    }

    @Test
    public void shouldGenerateValueProxy() throws NoSuchMethodException {
        ProxyFor proxyAnnotation = AddressProxy.class.getAnnotation(ProxyFor.class);
        assertTrue(ValueProxy.class.isAssignableFrom(AddressProxy.class));
        assertEquals(proxyAnnotation.value(), Address.class);
    }
}
