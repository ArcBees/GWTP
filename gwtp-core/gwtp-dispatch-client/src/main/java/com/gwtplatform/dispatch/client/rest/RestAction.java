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

package com.gwtplatform.dispatch.client.rest;

import java.util.Map;

import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

/**
 * TODO: Documentation
 */
public interface RestAction<R extends Result> extends Action<R> {
    HttpMethod getHttpMethod();

    Map<String, Object> getPathParams();

    Map<String, Object> getQueryParams();

    Map<String, Object> getFormParams();

    Map<String, Object> getHeaderParams();
}
