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

package com.gwtplatform.dispatch.shared.rest;

import java.util.List;

import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

/**
 * TODO: Documentation.
 *
 * @param <R> The result type the RestAction will produce
 */
public interface RestAction<R extends Result> extends Action<R> {
    HttpMethod getHttpMethod();

    List<RestParameter> getPathParams();

    List<RestParameter> getQueryParams();

    List<RestParameter> getFormParams();

    List<RestParameter> getHeaderParams();

    Object getBodyParam();

    Boolean hasFormParams();

    Boolean hasBodyParam();
}
