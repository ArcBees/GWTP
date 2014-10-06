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

package com.gwtplatform.dispatch.rest.client.interceptor;

import com.gwtplatform.dispatch.rest.client.AbstractRestAction;
import com.gwtplatform.dispatch.rest.client.DateFormat;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;

/**
 * An exposed RestAction implementation.
 */
class InterceptRestAction extends AbstractRestAction<Object> {
    protected InterceptRestAction(HttpMethod httpMethod,
                                  String rawServicePath,
                                  int queryCount) {
        super(httpMethod, rawServicePath, DateFormat.DEFAULT);

        // Add fake query params
        for (int i = 0; i < queryCount; i++) {
            addQueryParam("param" + i, "value" + i);
        }
    }

    @Override
    public boolean isSecured() {
        return false;
    }
}
