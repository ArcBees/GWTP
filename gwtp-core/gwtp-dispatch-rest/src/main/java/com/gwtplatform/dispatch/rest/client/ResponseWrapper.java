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

package com.gwtplatform.dispatch.rest.client;

import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Response;

/**
 * This class is used to wrap a {@link Response} object and normalize fields value across browser.
 *
 * @see #getStatusCode()
 */
class ResponseWrapper extends Response {
    private final Response response;

    ResponseWrapper(Response response) {
        this.response = response;
    }

    @Override
    public String getHeader(String header) {
        return response.getHeader(header);
    }

    @Override
    public Header[] getHeaders() {
        return response.getHeaders();
    }

    @Override
    public String getHeadersAsString() {
        return response.getHeadersAsString();
    }

    /**
     * Some IE versions will convert 204 NO_CONTENT to 1223. We normalize this behaviour and return 204 as it should be.
     *
     * @return the HTTP status code.
     */
    @Override
    public int getStatusCode() {
        int statusCode = response.getStatusCode();

        if (statusCode == 1223) {
            return SC_NO_CONTENT;
        } else {
            return statusCode;
        }
    }

    @Override
    public String getStatusText() {
        return response.getStatusText();
    }

    @Override
    public String getText() {
        return response.getText();
    }
}
