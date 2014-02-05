/**
 * Copyright 2014 ArcBees Inc.
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

package com.gwtplatform.common.client;

import com.google.gwt.http.client.URL;
import com.gwtplatform.common.shared.UrlUtils;

/**
 * Helper implementation which wraps calls to code which require a running GWT environment and make testing slow.
 */
public class ClientUrlUtils implements UrlUtils {
    @Override
    public String decodeQueryString(String encodedUrlComponent) {
        return URL.decodeQueryString(encodedUrlComponent);
    }

    @Override
    public String encodeQueryString(String decodedUrlComponent) {
        return URL.encodeQueryString(decodedUrlComponent);
    }

    @Override
    public String decodePathSegment(String encodedPathSegment) {
        return URL.decodePathSegment(encodedPathSegment);
    }

    @Override
    public String encodePathSegment(String decodedPathSegment) {
        return URL.encodePathSegment(decodedPathSegment);
    }
}
