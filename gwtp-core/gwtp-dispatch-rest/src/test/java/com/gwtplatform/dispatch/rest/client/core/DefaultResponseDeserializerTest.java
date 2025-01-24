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

package com.gwtplatform.dispatch.rest.client.core;

import javax.ws.rs.core.HttpHeaders;

import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.http.client.Response;
import com.google.inject.multibindings.Multibinder;
import com.gwtplatform.dispatch.rest.client.serialization.Serialization;
import com.gwtplatform.dispatch.rest.client.testutils.UnsecuredRestAction;
import com.gwtplatform.dispatch.rest.shared.ContentType;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.shared.ActionException;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(JukitoRunner.class)
public class DefaultResponseDeserializerTest {
    public static class Module extends JukitoModule {
        @Override
        protected void configureTest() {
            Serialization serialization = mock(Serialization.class);
            bind(Serialization.class).toInstance(serialization);

            Multibinder.newSetBinder(binder(), Serialization.class).addBinding().toInstance(serialization);
        }
    }

    @Inject
    private DefaultResponseDeserializer deserializer;
    @Inject
    private Serialization serialization;

    @Test(expected = ActionException.class)
    public void deserialize_errorCode_throws() throws ActionException {
        // given
        UnsecuredRestAction action = new UnsecuredRestAction(HttpMethod.GET, "");

        Response response = mock(Response.class);
        given(response.getStatusCode()).willReturn(404);

        // when
        deserializer.deserialize(action, response);
    }

    @Test(expected = ActionException.class)
    public void deserialize_resultClassNotKnown_throws() throws ActionException {
        // given
        UnsecuredRestAction action = new UnsecuredRestAction(HttpMethod.GET, "");

        Response response = mock(Response.class);
        given(response.getStatusCode()).willReturn(200);

        // when
        deserializer.deserialize(action, response);
    }

    @Test(expected = ActionException.class)
    public void deserialize_noCapableSerialization_throws() throws ActionException {
        // given
        UnsecuredRestAction action = new UnsecuredRestAction(HttpMethod.GET, "");
        action.setResultClass("MyClass<Hey<Ho>>>");

        Response response = mock(Response.class);
        given(response.getStatusCode()).willReturn(200);
        given(response.getHeader(HttpHeaders.CONTENT_TYPE)).willReturn("any");

        // when
        deserializer.deserialize(action, response);
    }

    @Test
    public void deserialize_serialization_delegates() throws ActionException {
        // given
        String resultType = "MyClass<Hey<Ho>>>";
        ContentType contentType = ContentType.valueOf("that-content-type");
        String serializedContent = "agakrybatgfkasfh";
        Object expectedResult = new Object();

        UnsecuredRestAction action = new UnsecuredRestAction(HttpMethod.GET, "");
        action.setResultClass(resultType);

        Response response = mock(Response.class);
        given(response.getStatusCode()).willReturn(200);
        given(response.getHeader(HttpHeaders.CONTENT_TYPE)).willReturn(contentType.toString());
        given(response.getText()).willReturn(serializedContent);

        given(serialization.canDeserialize(resultType, contentType)).willReturn(true);
        given(serialization.deserialize(resultType, contentType, serializedContent)).willReturn(expectedResult);

        // when
        Object result = deserializer.deserialize(action, response);

        // then
        assertThat(result).isSameAs(expectedResult);
    }
}
