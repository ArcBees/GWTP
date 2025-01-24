/*
 * Copyright 2015 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.client.core;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.gwtplatform.dispatch.rest.client.core.parameters.CookieParameter;
import com.gwtplatform.dispatch.rest.client.testutils.SecuredRestAction;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;

import static java.util.Arrays.asList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static com.google.common.collect.Maps.immutableEntry;

@RunWith(GwtMockitoTestRunner.class)
public class DefaultCookieManagerTest {
    private static final String SOME_NAME = "someName";
    private static final String SOME_VALUE = "someValue";
    private static final String SOME_VALUE_2 = "someValue";

    private DefaultCookieManager manager;

    @Before
    public void setUp() {
        manager = spy(new DefaultCookieManager());
    }

    @Test
    public void saveSingleCookie() {
        // given
        SecuredRestAction action = new SecuredRestAction(HttpMethod.GET, "");
        action.addParam(cookieParam(SOME_NAME, SOME_VALUE));

        // when
        manager.saveCookiesFromAction(action);

        // then
        verify(manager).saveCookie(SOME_VALUE);
    }

    @Test
    public void saveMultipleCookies() {
        // given
        SecuredRestAction action = new SecuredRestAction(HttpMethod.GET, "");
        action.addParam(cookieParam(SOME_NAME, SOME_VALUE));
        action.addParam(cookieParam(SOME_NAME, SOME_VALUE_2));

        // when
        manager.saveCookiesFromAction(action);

        // then
        InOrder inOrder = inOrder(manager);
        inOrder.verify(manager).saveCookie(SOME_VALUE);
        inOrder.verify(manager).saveCookie(SOME_VALUE_2);
    }

    private CookieParameter cookieParam(String name, String value) {
        CookieParameter cookieParam = mock(CookieParameter.class);
        given(cookieParam.getEncodedEntries()).willReturn(asList(immutableEntry(name, value)));
        given(cookieParam.getType()).willReturn(Type.COOKIE);
        given(cookieParam.getObject()).willReturn(value);
        return cookieParam;
    }
}
