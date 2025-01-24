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

package com.gwtplatform.dispatch.rest.client;

import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.gwtplatform.dispatch.rest.client.core.CookieManager;
import com.gwtplatform.dispatch.rest.client.core.RequestBuilderFactory;
import com.gwtplatform.dispatch.rest.client.core.ResponseDeserializer;
import com.gwtplatform.dispatch.rest.client.core.RestDispatchCall;
import com.gwtplatform.dispatch.rest.client.testutils.SecuredRestAction;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.shared.ActionException;
import com.gwtplatform.dispatch.shared.SecurityCookieAccessor;

import jakarta.inject.Inject;

import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

@RunWith(JukitoRunner.class)
public class RestDispatchCallTest {
    public static class SomeAction extends SecuredRestAction {
        @Inject
        SomeAction(
                HttpMethod httpMethod,
                String rawServicePath) {
            super(httpMethod, rawServicePath);
        }
    }

    @Inject
    private SecurityCookieAccessor securityCookieAccessor;
    @Inject
    private RequestBuilderFactory requestBuilderFactory;
    @Inject
    private CookieManager cookieManager;
    @Inject
    private ResponseDeserializer responseDeserializer;

    @Test
    public void someAction_cookieSavedBeforeExecution(RestCallback<Void> callback)
            throws ActionException, RequestException {
        // given
        SomeAction action = createAction();
        RequestBuilder requestBuilder = mock(RequestBuilder.class);
        RestDispatchCall<SomeAction, Void> call = createCall(action, callback);

        given(requestBuilderFactory.build(same(action), isNull())).willReturn(requestBuilder);

        // when
        call.execute();

        // then
        InOrder inOrder = inOrder(cookieManager, requestBuilder);
        inOrder.verify(cookieManager).saveCookiesFromAction(same(action));
        inOrder.verify(requestBuilder).send();
    }

    private SomeAction createAction() {
        return new SomeAction(HttpMethod.GET, "");
    }

    private <A extends RestAction<R>, R> RestDispatchCall<A, R> createCall(A action, RestCallback<R> callback) {
        return new RestDispatchCall<>(securityCookieAccessor, requestBuilderFactory, cookieManager,
                responseDeserializer, action, callback);
    }
}
