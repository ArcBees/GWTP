/*
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

package com.gwtplatform.dispatch.rest.client.core.parameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.gwtplatform.dispatch.rest.client.testutils.RuntimeDelegateStub;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(GwtMockitoTestRunner.class)
public class CookieParameterTest {
    private static final String COOKIE_NAME = "someName";
    private static final String PARAM_NAME = "someName2";
    private static final String SOME_VALUE = "someValue";
    private static final String SOME_VALUE_2 = "someValue2";

    static {
        RuntimeDelegateStub.register();
    }

    @Test
    public void getType_returnsCookie() {
        // given
        CookieParameter parameter = new CookieParameter("", null, "");

        // when
        Type type = parameter.getType();

        // then
        assertThat(type).isSameAs(Type.COOKIE);
    }

    @Test
    public void getName_returnsProvidedName() {
        // given
        CookieParameter parameter = new CookieParameter(COOKIE_NAME, null, "");

        // when
        String name = parameter.getName();

        // then
        assertThat(name).isEqualTo(COOKIE_NAME);
    }

    @Test
    public void getObject_returnsProvidedObject() {
        // given
        Object value = new Object();
        CookieParameter parameter = new CookieParameter(COOKIE_NAME, value, "");

        // when
        Object object = parameter.getObject();

        // then
        assertThat(object).isSameAs(value);
    }

    @Test
    public void getEntries_string_returnsOneParsedCookie() {
        // given
        CookieParameter parameter = new CookieParameter(COOKIE_NAME, SOME_VALUE, "");

        // when
        List<Entry<String, String>> entries = parameter.getEncodedEntries();

        // then
        assertThat(entries)
                .extracting("value")
                .containsOnly(COOKIE_NAME + "=" + SOME_VALUE);
    }

    @Test
    public void getEntries_newCookie_returnsOneParsedCookie() {
        // given
        String path = "/path";
        String domain = "domain.com";
        int maxAge = 5000;

        NewCookie newCookie = new NewCookie(COOKIE_NAME, SOME_VALUE, path, domain, 1, "", maxAge, true);
        CookieParameter parameter = new CookieParameter(PARAM_NAME, newCookie, "");

        // when
        List<Entry<String, String>> entries = parameter.getEncodedEntries();

        // then
        assertThat(entries)
                .extracting("value")
                .containsOnly(
                        PARAM_NAME + "=" + SOME_VALUE + ";max-age=" + maxAge + ";domain=" + domain + ";path=" + path
                                + ";secure");
    }

    @Test
    public void getEntries_newCookieWithDefaultValues_returnsOneParsedCookie() {
        // given
        NewCookie newCookie = new NewCookie(COOKIE_NAME, SOME_VALUE);
        CookieParameter parameter = new CookieParameter(PARAM_NAME, newCookie, "");

        // when
        List<Entry<String, String>> entries = parameter.getEncodedEntries();

        // then
        assertThat(entries)
                .extracting("value")
                .containsOnly(PARAM_NAME + "=" + SOME_VALUE);
    }

    @Test
    public void getEntries_cookie_returnsOneParsedCookie() {
        // given
        Cookie cookie = new Cookie(COOKIE_NAME, SOME_VALUE);
        CookieParameter parameter = new CookieParameter(PARAM_NAME, cookie, "");

        // when
        List<Entry<String, String>> entries = parameter.getEncodedEntries();

        // then
        assertThat(entries)
                .extracting("value")
                .containsOnly(PARAM_NAME + "=" + SOME_VALUE);
    }

    /**
     * Collections of cookies may be sued to specify the same cookie on different path. For simplicity here, I'm gonna
     * skip that.
     */
    @Test
    public void getEntries_collectionOfCookie_returnsMultipleParsedCookies() {
        // given
        List<Cookie> cookies = new ArrayList<Cookie>();
        cookies.add(new Cookie(COOKIE_NAME, SOME_VALUE));
        cookies.add(new Cookie(COOKIE_NAME, SOME_VALUE_2));

        CookieParameter parameter = new CookieParameter(PARAM_NAME, cookies, "");

        // when
        List<Entry<String, String>> entries = parameter.getEncodedEntries();

        // then
        assertThat(entries)
                .extracting("value")
                .containsOnly(
                        PARAM_NAME + "=" + SOME_VALUE,
                        PARAM_NAME + "=" + SOME_VALUE_2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getEntries_collectionOfObjects_throws() {
        // given
        List<Object> cookies = new ArrayList<Object>();
        cookies.add(SOME_VALUE);

        CookieParameter parameter = new CookieParameter(PARAM_NAME, cookies, "");

        // when
        parameter.getEncodedEntries();
    }
}
