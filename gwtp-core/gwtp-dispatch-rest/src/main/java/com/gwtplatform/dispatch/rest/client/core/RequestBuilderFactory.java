/*
 * Copyright 2013 ArcBees Inc.
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

import com.google.gwt.http.client.RequestBuilder;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.shared.ActionException;

/**
 * Factory used to create {@link RequestBuilder} objects for a given {@link RestAction}.
 */
public interface RequestBuilderFactory {
    /**
     * Build a {@link RequestBuilder} object for the given action.
     *
     * @param action        the {@link RestAction} for which to build the request.
     * @param securityToken the security token to add to the {@link RequestBuilder}. Usually the value bound to
     *                      {@code XsrfHeaderName}.
     * @param <A>           the action type.
     * @return a {@link RequestBuilder} object.
     * @throws ActionException if an exception occured while creating the {@link RequestBuilder}.
     */
    <A extends RestAction<?>> RequestBuilder build(A action, String securityToken) throws ActionException;
}
