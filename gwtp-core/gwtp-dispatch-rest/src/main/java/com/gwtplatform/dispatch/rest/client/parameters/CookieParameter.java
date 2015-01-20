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

package com.gwtplatform.dispatch.rest.client.parameters;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;

public class CookieParameter extends CollectionSupportedParameter {
    public CookieParameter(
            String name,
            Object object,
            String dateFormat) {
        super(Type.COOKIE, name, object, dateFormat);
    }

    @Override
    protected String parseObject(Object object) {
        NewCookie cookie;

        if (object instanceof NewCookie) {
            cookie = (NewCookie) object;
        } else if (object instanceof Cookie) {
            cookie = new NewCookie((Cookie) object);
        } else if (getObject() == object) {
            // Root object is the same: allow other types.
            cookie = new NewCookie(getName(), super.parseObject(object));
        } else {
            throw new IllegalArgumentException(
                    "Cookie value must be a primitive, NewCookie or a collection of NewCookie");
        }

        return parseCookie(cookie);
    }

    private String parseCookie(NewCookie cookie) {
        String result = cookie.getName() + '=' + cookie.getValue();
        result += maxAgeOrEmpty(cookie);
        result += domainOrEmpty(cookie);
        result += pathOrEmpty(cookie);
        result += secureOrEmpty(cookie);

        return result;
    }

    private String maxAgeOrEmpty(NewCookie cookie) {
        int maxAge = cookie.getMaxAge();
        if (maxAge != -1) {
            return ";max-age=" + maxAge + expires(maxAge);
        }

        return "";
    }

    /**
     * Return the expires property based on <code>maxAge</code>. All browser use Max-Age over Expires, except for IE
     * which doesn't understand Max-Age.
     */
    private native String expires(int maxAge) /*-{
        var expires = new Date();
        expires.setTime(maxAge == 0 ? 0 : expires.getTime() + maxAge);

        return ";expires=" + expires.toUTCString();
    }-*/;

    private String domainOrEmpty(NewCookie cookie) {
        String domain = cookie.getDomain();
        if (domain != null) {
            return ";domain=" + domain;
        }

        return "";
    }

    private String pathOrEmpty(NewCookie cookie) {
        String path = cookie.getPath();
        if (path != null) {
            return ";path=" + path;
        }

        return "";
    }

    private String secureOrEmpty(NewCookie cookie) {
        if (cookie.isSecure()) {
            return ";secure";
        }

        return "";
    }
}
