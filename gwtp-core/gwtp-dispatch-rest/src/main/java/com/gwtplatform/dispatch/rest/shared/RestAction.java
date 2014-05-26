/**
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

package com.gwtplatform.dispatch.rest.shared;

import java.util.List;

import com.gwtplatform.dispatch.shared.HasSecured;
import com.gwtplatform.dispatch.shared.TypedAction;

/**
 * An action used by {@link RestDispatch}.
 * <p/>
 * You will usually want to let GWTP generate your actions by creating services as explained
 * <a href="https://github.com/ArcBees/GWTP/wiki/Rest-Dispatch#write-services-and-actions">here</a>.
 *
 * @param <R> the result type.
 */
public interface RestAction<R> extends TypedAction<R>, HasSecured {
    /**
     * Returns the relative path for this action. It should not be prepended by the path annotated with
     * {@literal @}{@link com.gwtplatform.dispatch.rest.client.RestApplicationPath}.
     *
     * @return the relative path for this action.
     */
    String getPath();

    /**
     * @return the {@link HttpMethod} used to send this action over HTTP.
     */
    HttpMethod getHttpMethod();

    /**
     * @return a {@link List} of the {@literal @}{@link javax.ws.rs.Path} parameters for this action.
     */
    List<RestParameter> getPathParams();

    /**
     * @return a {@link List} of the {@literal @}{@link javax.ws.rs.QueryParam} parameters for this action.
     */
    List<RestParameter> getQueryParams();

    /**
     * @return a {@link List} of the {@literal @}{@link javax.ws.rs.FormParam} parameters for this action.
     * {@link #getBodyParam()} should return {@code null}.
     */
    List<RestParameter> getFormParams();

    /**
     * @return a {@link List} of the {@literal @}{@link javax.ws.rs.HeaderParam} parameters for this action.
     */
    List<RestParameter> getHeaderParams();

    /**
     * @return The object that will be serialized and used for the body of this action. {@link #getFormParams()} should
     * return an empty list.
     */
    Object getBodyParam();

    /**
     * Verify if this action contains FORM parameters. {@link #hasBodyParam()} should return {@code false}.
     *
     * @return {@code true} if this method contains form parameters, otherwise {@code false}.
     */
    Boolean hasFormParams();

    /**
     * Verify if this action a body object. {@link #hasFormParams()} should return {@code false}.
     *
     * @return {@code true} if this method contains body object, otherwise {@code false}.
     */
    Boolean hasBodyParam();
}
