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

package com.gwtplatform.mvp.client.proxy;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.jukito.TestSingleton;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.proxy.RouteTokenFormatter.UrlUtils;

/**
 * Unit tests for {@link RouteTokenFormatter}.
 */
@RunWith(JukitoRunner.class)
public class RouteTokenFormatterTest {
    public static class Module extends JukitoModule {
        @Override
        protected void configureTest() {
            bind(UrlUtils.class).to(UrlUtilsTestImpl.class).in(TestSingleton.class);
            bind(PlaceTokenRegistry.class).to(PlaceTokenRegistryTestImpl.class).in(TestSingleton.class);
        }
    }

    static class UrlUtilsTestImpl extends RouteTokenFormatter.UrlUtils {
        @Override
        public String decodeQueryString(String encodedUrlComponent) {
            return encodedUrlComponent;
        }

        @Override
        public String encodeQueryString(String decodedUrlComponent) {
            return decodedUrlComponent;
        }
    }

    static class PlaceTokenRegistryTestImpl implements PlaceTokenRegistry {
        @Override
        public Set<String> getAllPlaceTokens() {
            return ImmutableSet.<String> builder().add("/user/{userId}/groups/{groupId}")
                    .add("/user/{userId}/albums/{albumId}").add("/user/{userId}/albums/staticAlbumId")
                    .add("/user/staticUserId/albums/{albumId}").add("/user/staticUserId/albums/staticAlbumId")
                    .add("/{vanityId}").add("/privacy").add("/").build();
        }
    }

    @Inject
    RouteTokenFormatter tokenFormatter;

    @Test
    public void testToPlaceTokenWithoutQueryString() {
        // Given
        PlaceRequest placeRequest = new PlaceRequest("/user/{userId}/albums/{albumId}").with("userId", "0x42").with(
                "albumId", "0xAFFE");
        String expectedPlacePattern = "^\\/user\\/0x42\\/albums\\/0xAFFE$";
        Map<String, String> expectedQueryParameters = null;

        // When
        String placeToken = tokenFormatter.toPlaceToken(placeRequest);
        Map<String, String> queryParameters = (placeToken.indexOf('?') != -1) ? tokenFormatter.parseQueryString(
                placeToken.substring(placeToken.indexOf('?') + 1), null) : null;

        // Then
        assertTrue(placeToken.matches(expectedPlacePattern));
        assertEquals(expectedQueryParameters, queryParameters);
    }

    @Test
    public void testToPlaceTokenWithOneQueryStringParameter() {
        // Given
        PlaceRequest placeRequest = new PlaceRequest("/user/{userId}/albums/{albumId}").with("userId", "0x42")
                .with("albumId", "0xAFFE").with("start", "0");
        String expectedPlacePattern = "^\\/user\\/0x42\\/albums\\/0xAFFE\\?\\w*=\\d*$";
        Map<String, String> expectedQueryParameters = ImmutableMap.<String, String> builder().put("start", "0").build();

        // When
        String placeToken = tokenFormatter.toPlaceToken(placeRequest);
        Map<String, String> queryParameters = (placeToken.indexOf('?') != -1) ? tokenFormatter.parseQueryString(
                placeToken.substring(placeToken.indexOf('?') + 1), null) : null;

        // Then
        assertTrue(placeToken.matches(expectedPlacePattern));
        assertEquals(expectedQueryParameters, queryParameters);
    }

    @Test
    public void testToPlaceTokenWithSeveralQueryStringParameter() {
        // Given
        PlaceRequest placeRequest = new PlaceRequest("/user/{userId}/albums/{albumId}").with("userId", "0x42")
                .with("albumId", "0xAFFE").with("start", "15").with("limit", "20");
        String expectedPlacePattern = "^\\/user\\/0x42\\/albums\\/0xAFFE\\?\\w*=\\d*&\\w*=\\d*$";
        Map<String, String> expectedQueryParameters = ImmutableMap.<String, String> builder().put("start", "15")
                .put("limit", "20").build();

        // When
        String placeToken = tokenFormatter.toPlaceToken(placeRequest);
        Map<String, String> queryParameters = (placeToken.indexOf('?') != -1) ? tokenFormatter.parseQueryString(
                placeToken.substring(placeToken.indexOf('?') + 1), null) : null;

        // Then
        assertTrue(placeToken.matches(expectedPlacePattern));
        assertEquals(expectedQueryParameters, queryParameters);
    }

    @Test
    public void testToHistoryToken() {
        tokenFormatter = spy(tokenFormatter);

        // Given
        PlaceRequest placeRequest = new PlaceRequest("token");
        List<PlaceRequest> placeRequestHierarchy = ImmutableList.<PlaceRequest> builder().add(placeRequest).build();

        // When
        tokenFormatter.toHistoryToken(placeRequestHierarchy);

        // Then
        verify(tokenFormatter).toPlaceToken(placeRequest);
    }

    @Test
    public void testToPlaceRequestStaticVsParameterMatch() {
        // When
        PlaceRequest placeRequest01 = tokenFormatter.toPlaceRequest("/user/0x42/albums/0xAFFE");
        PlaceRequest placeRequest02 = tokenFormatter.toPlaceRequest("/user/staticUserId/albums/staticAlbumId");
        PlaceRequest placeRequest03 = tokenFormatter.toPlaceRequest("/user/0x42/albums/staticAlbumId");
        PlaceRequest placeRequest04 = tokenFormatter.toPlaceRequest("/user/staticUserId/albums/0xAFFE");
        PlaceRequest placeRequest05 = tokenFormatter.toPlaceRequest("/privacy");
        PlaceRequest placeRequest06 = tokenFormatter.toPlaceRequest("/vanity");
        PlaceRequest placeRequest07 = tokenFormatter.toPlaceRequest("/user/0x42/albums/0xAFFE?start=0");
        PlaceRequest placeRequest08 = tokenFormatter.toPlaceRequest("/vanity?a=valueA&b=valueB");
        PlaceRequest placeRequest09 = tokenFormatter.toPlaceRequest("vanity?a=valueA&b=valueB");

        // Then
        assertEquals("/user/{userId}/albums/{albumId}", placeRequest01.getNameToken());
        assertEquals(2, placeRequest01.getParameterNames().size());
        assertEquals("0x42", placeRequest01.getParameter("userId", null));
        assertEquals("0xAFFE", placeRequest01.getParameter("albumId", null));

        assertEquals("/user/staticUserId/albums/staticAlbumId", placeRequest02.getNameToken());
        assertEquals(0, placeRequest02.getParameterNames().size());

        assertEquals("/user/{userId}/albums/staticAlbumId", placeRequest03.getNameToken());
        assertEquals(1, placeRequest03.getParameterNames().size());
        assertEquals("0x42", placeRequest03.getParameter("userId", null));

        assertEquals("/user/staticUserId/albums/{albumId}", placeRequest04.getNameToken());
        assertEquals(1, placeRequest04.getParameterNames().size());
        assertEquals("0xAFFE", placeRequest04.getParameter("albumId", null));

        assertEquals("/privacy", placeRequest05.getNameToken());
        assertEquals(0, placeRequest05.getParameterNames().size());

        assertEquals("/{vanityId}", placeRequest06.getNameToken());
        assertEquals(1, placeRequest06.getParameterNames().size());
        assertEquals("vanity", placeRequest06.getParameter("vanityId", null));

        assertEquals("/user/{userId}/albums/{albumId}", placeRequest07.getNameToken());
        assertEquals(3, placeRequest07.getParameterNames().size());
        assertEquals("0x42", placeRequest07.getParameter("userId", null));
        assertEquals("0xAFFE", placeRequest07.getParameter("albumId", null));
        assertEquals("0", placeRequest07.getParameter("start", null));

        assertEquals("/{vanityId}", placeRequest08.getNameToken());
        assertEquals(3, placeRequest08.getParameterNames().size());
        assertEquals("vanity", placeRequest08.getParameter("vanityId", null));
        assertEquals("valueA", placeRequest08.getParameter("a", null));
        assertEquals("valueB", placeRequest08.getParameter("b", null));

        assertEquals("/{vanityId}", placeRequest09.getNameToken());
        assertEquals(3, placeRequest09.getParameterNames().size());
        assertEquals("vanity", placeRequest09.getParameter("vanityId", null));
        assertEquals("valueA", placeRequest09.getParameter("a", null));
        assertEquals("valueB", placeRequest09.getParameter("b", null));
    }

    @Test
    public void testToPlaceRequestEmptyRoute() {
        // When
        PlaceRequest placeRequest10 = tokenFormatter.toPlaceRequest("/");
        PlaceRequest placeRequest11 = tokenFormatter.toPlaceRequest("/?a=valueA&b=valueB");
        PlaceRequest placeRequest12 = tokenFormatter.toPlaceRequest("");
        PlaceRequest placeRequest13 = tokenFormatter.toPlaceRequest("?a=valueA&b=valueB");

        // Then
        assertEquals("/", placeRequest10.getNameToken());
        assertEquals(0, placeRequest10.getParameterNames().size());

        assertEquals("/", placeRequest11.getNameToken());
        assertEquals(2, placeRequest11.getParameterNames().size());
        assertEquals("valueA", placeRequest11.getParameter("a", null));
        assertEquals("valueB", placeRequest11.getParameter("b", null));

        assertEquals("/", placeRequest12.getNameToken());
        assertEquals(0, placeRequest12.getParameterNames().size());

        assertEquals("/", placeRequest13.getNameToken());
        assertEquals(2, placeRequest13.getParameterNames().size());
        assertEquals("valueA", placeRequest13.getParameter("a", null));
        assertEquals("valueB", placeRequest13.getParameter("b", null));
    }

    public void testToPlaceRequestNotExistingRoute() {
        // When
        PlaceRequest placeRequest13 = tokenFormatter.toPlaceRequest("/not/existing");
        PlaceRequest placeRequest14 = tokenFormatter.toPlaceRequest("/not/existing?a=valueA&b=valueB");
        PlaceRequest placeRequest15 = tokenFormatter.toPlaceRequest("not/existing");

        // Then
        assertEquals("/not/existing", placeRequest13.getNameToken());
        assertEquals(0, placeRequest13.getParameterNames().size());

        assertEquals("/not/existing", placeRequest14.getNameToken());
        assertEquals(2, placeRequest14.getParameterNames().size());
        assertEquals("valueA", placeRequest14.getParameter("a", null));
        assertEquals("valueB", placeRequest14.getParameter("b", null));

        assertEquals("/not/existing", placeRequest15.getNameToken());
        assertEquals(0, placeRequest15.getParameterNames().size());
    }

    @Test
    public void testToPlaceRequestHierarchy() {
        tokenFormatter = spy(tokenFormatter);

        // Given
        String historyToken = "token";

        // When
        tokenFormatter.toPlaceRequestHierarchy(historyToken);

        // Then
        verify(tokenFormatter).toPlaceRequest(historyToken);
    }
}
