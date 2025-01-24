/*
 * Copyright 2016 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.client.filter;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;
import com.gwtplatform.dispatch.client.ExecuteCommand;
import com.gwtplatform.dispatch.rest.client.RestCallback;
import com.gwtplatform.dispatch.rest.client.context.RestContext;
import com.gwtplatform.dispatch.rest.client.testutils.UnsecuredRestAction;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.shared.DispatchRequest;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@SuppressWarnings("rawtypes")
@RunWith(JukitoRunner.class)
public class DefaultRestFilterChainTest {
    @Inject
    private Provider<DefaultRestFilterChain> filterChainProvider;
    @Inject
    private RestFilterRegistry filterRegistry;

    @Test
    public void doFilter_noFilter_executesCommand() throws Exception {
        // given
        RestAction action = mock(RestAction.class);
        RestCallback callback = mock(RestCallback.class);
        ExecuteCommand command = mock(ExecuteCommand.class);

        Iterator iterator = mock(Iterator.class);
        given(filterRegistry.iterator()).willReturn(iterator);
        given(iterator.hasNext()).willReturn(false);

        DispatchRequest expected = mock(DispatchRequest.class);
        given(command.execute(same(action), same(callback))).willReturn(expected);

        // when
        DispatchRequest result = filterChainProvider.get().doFilter(action, callback, command);

        // then
        assertThat(result).isSameAs(expected);
    }

    @Test
    public void doFilter_matchingFilter_delegatesToFilter() throws Exception {
        // given
        RestAction action = createRestAction("");
        RestCallback callback = mock(RestCallback.class);
        ExecuteCommand command = mock(ExecuteCommand.class);
        DispatchRequest expected = mock(DispatchRequest.class);

        RestFilter filter = createFilter(createRestContext(action), expected);
        ArrayList<Entry<RestContext, RestFilter>> filters =
                Lists.newArrayList(new SimpleEntry<>(filter.getRestContext(), filter));
        given(filterRegistry.iterator()).willReturn(filters.iterator());

        // when
        DispatchRequest result = filterChainProvider.get().doFilter(action, callback, command);

        // then
        assertThat(result).isSameAs(expected);
    }

    @Test
    public void doFilter_noMatchingFilter_executesCommand() throws Exception {
        // given
        RestAction action = createRestAction("/path1");
        RestAction subjectAction = createRestAction("/path2");
        RestCallback callback = mock(RestCallback.class);
        ExecuteCommand command = mock(ExecuteCommand.class);
        DispatchRequest expected = mock(DispatchRequest.class);

        RestFilter filter = createFilter(createRestContext(subjectAction), mock(DispatchRequest.class));
        ArrayList<Entry<RestContext, RestFilter>> filters =
                Lists.newArrayList(new SimpleEntry<>(filter.getRestContext(), filter));
        given(filterRegistry.iterator()).willReturn(filters.iterator());
        given(command.execute(same(action), same(callback))).willReturn(expected);

        // when
        DispatchRequest result = filterChainProvider.get().doFilter(action, callback, command);

        // then
        assertThat(result).isSameAs(expected);
        verify(filter, never()).filter(any(RestAction.class), any(RestCallback.class), any(ExecuteCommand.class),
                any(RestFilterChain.class));
    }

    private RestAction createRestAction(String path) {
        return new UnsecuredRestAction(HttpMethod.GET, path);
    }

    private RestContext createRestContext(RestAction action) {
        return new RestContext.Builder(action)
                .transcendent(true)
                .anyHttpMethod(true)
                .anyQueryCount(true)
                .build();
    }

    private RestFilter createFilter(
            RestContext restContext,
            DispatchRequest expected) {
        return spy(new RestFilter() {
            @Override
            public <R> DispatchRequest filter(RestAction<R> action, RestCallback<R> callback,
                    ExecuteCommand<RestAction<R>, RestCallback<R>> command, RestFilterChain chain) {
                return expected;
            }

            @Override
            public RestContext getRestContext() {
                return restContext;
            }
        });
    }
}
