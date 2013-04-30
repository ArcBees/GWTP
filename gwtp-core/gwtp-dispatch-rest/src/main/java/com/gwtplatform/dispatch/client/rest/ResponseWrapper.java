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

package com.gwtplatform.dispatch.client.rest;

import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Response;

class ResponseWrapper extends Response {
    private final Response wrapped;

    ResponseWrapper(Response wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String getHeader(String header) {
        return wrapped.getHeader(header);
    }

    @Override
    public Header[] getHeaders() {
        return wrapped.getHeaders();
    }

    @Override
    public String getHeadersAsString() {
        return wrapped.getHeadersAsString();
    }

    @Override
    public int getStatusCode() {
        int statusCode = wrapped.getStatusCode();

        if (statusCode == 1223) {
            return 204;
        } else {
            return statusCode;
        }
    }

    @Override
    public String getStatusText() {
        return wrapped.getStatusText();
    }

    @Override
    public String getText() {
        return wrapped.getText();
    }
}
