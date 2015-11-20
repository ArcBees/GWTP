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

package com.gwtplatform.dispatch.rest.shared;

import java.util.List;

import com.gwtplatform.dispatch.shared.HasSecured;
import com.gwtplatform.dispatch.shared.TypedAction;

/**
 * An action used by {@link com.gwtplatform.dispatch.rest.client.RestDispatch RestDispatch}.
 * <p/>
 * You will usually want to let GWTP generate your actions by creating services as explained
 * <a href="https://github.com/ArcBees/GWTP/wiki/Rest-Dispatch#write-services-and-actions">here</a>.
 *
 * @param <R> the result type.
 */
public interface RestAction<R> extends TypedAction<R>, HasSecured {

    /**
     * Returns the raw value of the @Path annotation. This path may also contain regular expression values for the
     * specific variables.
     * 
     * @see JSR-311 section 3.4
     * @return the raw service path for this action
     */
    String getRawServicePath();

    /**
     * Returns the relative path for this action. It should not be prepended by the path annotated with
     * {@link com.gwtplatform.dispatch.rest.client.RestApplicationPath @RestApplicationPath}. This path does not contain
     * regular expressions for the URI templates anymore.
     *
     * @see #getRawServicePath()
     * @return the relative path for this action.
     */
    String getPath();

    /**
     * Returns the regular expression which should be used to validate the given parameter value. In case no regular
     * expression is specified this method will return <code>null</code>
     * 
     * @param pathParameter The parameter to get the regular expression for.
     * @return The regular expression for the given parameter or <code>null</code> in case no regular expression is
     *         defined for this path parameter
     */
    String getPathParameterRegex(String pathParameter);

    /**
     * @return the {@link com.gwtplatform.dispatch.rest.shared.HttpMethod} designator used to send this action over
     *         HTTP.
     */
    HttpMethod getHttpMethod();

    /**
     * @return a {@link List} of the parameters of {@code type} for this action.
     */
    List<HttpParameter> getParameters(HttpParameter.Type type);

    /**
     * @return The object that will be serialized and used for the body of this action. There are no
     *         {@link com.gwtplatform.dispatch.rest.shared.HttpParameter.Type#FORM @FormParam} parameters.
     */
    Object getBodyParam();

    /**
     * Returns the parameterized qualified class name of the body type or <code>null</code> if there are no body
     * parameters. The type returned is a <code>String</code> because it is easier to compare the parameter type
     * information without reflection. This may change later on.
     */
    String getBodyClass();

    /**
     * Returns the parameterized qualified class name of the return type or <code>null</code> if there are no body
     * parameters. The type returned is a <code>String</code> because it is easier to compare the parameter type
     * information without reflection. This may change later on.
     */
    String getResultClass();

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

    /**
     * Get a list of content types the client is allowed to produce. These content types are usually enumerated in the
     * {@link javax.ws.rs.Consumes @Consumes} annotation.
     */
    List<ContentType> getClientProducedContentTypes();

    /**
     * Get a list of content types the client is allowed to produce. These content types are usually enumerated in the
     * {@link javax.ws.rs.Produces @Produces} annotation.
     */
    List<ContentType> getClientConsumedContentTypes();
}
