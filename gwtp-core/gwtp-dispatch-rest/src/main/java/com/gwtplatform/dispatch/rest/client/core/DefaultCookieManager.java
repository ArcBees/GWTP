/**
 * Copyright 2015 ArcBees Inc.
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

import java.util.List;
import java.util.Map.Entry;

import com.gwtplatform.dispatch.rest.client.parameters.CookieParameter;
import com.gwtplatform.dispatch.rest.shared.HttpParameter;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;
import com.gwtplatform.dispatch.rest.shared.RestAction;

public class DefaultCookieManager implements CookieManager {
    @Override
    public void saveCookiesFromAction(RestAction<?> action) {
        List<HttpParameter> parameters = action.getParameters(Type.COOKIE);

        for (HttpParameter parameter : parameters) {
            assert parameter instanceof CookieParameter;
            saveCookiesFromParameter((CookieParameter) parameter);
        }
    }

    @Override
    public void saveCookiesFromParameter(CookieParameter parameter) {
        for (Entry<String, String> cookie : parameter.getEntries()) {
            saveCookie(cookie.getValue());
        }
    }

    private native void saveCookie(String value) /*-{
        $doc.cookie = value;
    }-*/;
}
