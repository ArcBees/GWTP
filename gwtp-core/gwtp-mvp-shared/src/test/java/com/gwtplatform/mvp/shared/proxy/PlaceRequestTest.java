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

package com.gwtplatform.mvp.shared.proxy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Simple unit tests for {@link com.gwtplatform.mvp.shared.proxy.PlaceRequest}, including
 * {@link com.gwtplatform.mvp.shared.proxy.PlaceRequest.Builder} tests.
 */
public class PlaceRequestTest {
    @Test
    public void shouldBuildEmptyRequest() {
        // when
        PlaceRequest request = new PlaceRequest.Builder().build();

        // then
        assertNotNull(request);
        assertNull(request.getNameToken());
        Set<String> emptySet = Collections.emptySet();
        assertEquals(request.getParameterNames(), emptySet);
    }

    @Test
    public void shouldBuildRequestWithSeveralParameters() {
        // when
        PlaceRequest request = new PlaceRequest.Builder().nameToken("nameToken").with("name1", "value1")
                .with("name2", "value2").build();

        // then
        assertNotNull(request);
        assertEquals("nameToken", request.getNameToken());
        assertEquals("value1", request.getParameter("name1", ""));
        assertEquals("value2", request.getParameter("name2", ""));
    }

    @Test
    public void shouldBuildRequestWithParameterMap() {
        // given
        Map<String, String> existingParameters = new HashMap<String, String>();
        existingParameters.put("name1", "value1");
        existingParameters.put("name2", "value2");

        // when
        PlaceRequest request = new PlaceRequest.Builder().nameToken("nameToken").with(existingParameters)
                .with("name3", "value3").build();

        // then
        assertNotNull(request);
        assertEquals("nameToken", request.getNameToken());
        assertEquals("value1", request.getParameter("name1", ""));
        assertEquals("value2", request.getParameter("name2", ""));
        assertEquals("value3", request.getParameter("name3", ""));
    }

    @Test
    public void shouldBuildRequestFromExistingRequest() {
        // given
        PlaceRequest request = new PlaceRequest.Builder().nameToken("nameToken").build();

        // when
        PlaceRequest copyOfRequest = new PlaceRequest.Builder(request).build();

        // then
        assertEquals(request, copyOfRequest);
    }

    @Test
    public void testToString() {
        // given
        PlaceRequest request = new PlaceRequest.Builder()
                .nameToken("nameToken")
                .with("name1", "value1")
                .with("name2", "value2")
                .build();

        // when
        String result = request.toString();

        // then
        assertNotNull(result);
        assertEquals("PlaceRequest(nameToken=nameToken, params={name1=value1, name2=value2})", result);
    }
}
