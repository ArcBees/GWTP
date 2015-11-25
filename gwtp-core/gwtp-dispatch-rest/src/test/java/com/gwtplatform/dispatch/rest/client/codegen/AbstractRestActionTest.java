/*
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

package com.gwtplatform.dispatch.rest.client.codegen;

import org.junit.Before;
import org.junit.Test;

import com.gwtplatform.dispatch.rest.client.testutils.MockHttpParameterFactory;
import com.gwtplatform.dispatch.rest.client.testutils.SecuredRestAction;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.Assert.assertTrue;

public class AbstractRestActionTest {
    private static final String PARAM_NAME_1 = "someName";
    private static final String PARAM_NAME_2 = "someOtherName";
    private static final String PARAM_VALUE_1 = "someValue";
    private static final int PARAM_VALUE_2 = 7;

    private AbstractRestAction<Void> action;

    @Before
    public void setUp() {
        action = new SecuredRestAction(new MockHttpParameterFactory(), HttpMethod.POST, "");
    }

    @Test
    public void addParam_null_doesNotAddParam() {
        // When
        action.addParam(Type.QUERY, PARAM_NAME_1, null);

        // Then
        assertTrue(action.getParameters(Type.QUERY).isEmpty());
    }

    @Test
    public void addParam_string_addsParam() {
        // When
        action.addParam(Type.QUERY, PARAM_NAME_1, PARAM_VALUE_1);
        action.addParam(Type.QUERY, PARAM_NAME_2, PARAM_VALUE_2);

        // Then
        assertThat(action.getParameters(Type.QUERY)).hasSize(2).extracting("name", "object").containsExactly(tuple(
                PARAM_NAME_1, PARAM_VALUE_1), tuple(PARAM_NAME_2, PARAM_VALUE_2));
    }

    @Test
    public void getParams_stripsOutNulls() {
        // When
        action.addParam(Type.FORM, PARAM_NAME_1, null);
        action.addParam(Type.FORM, PARAM_NAME_2, PARAM_VALUE_2);

        // Then
        assertThat(action.getParameters(Type.FORM)).hasSize(1).extracting("name", "object").containsExactly(tuple(
                PARAM_NAME_2, PARAM_VALUE_2));
    }

    @Test
    public void regexInformationShouldNotBePartOfPath() {
        // When
        action = new SecuredRestAction(new MockHttpParameterFactory(), HttpMethod.POST,
                "{id: [0-9]*}/subpath/{sid:[0-9]{1,9}}");

        // Then
        assertThat(action.getPath()).isEqualTo("{id}/subpath/{sid}");
    }

    @Test
    public void pathParameterWithoutRegexShouldNotBeChanged() {
        // When
        action = new SecuredRestAction(new MockHttpParameterFactory(), HttpMethod.POST, "{id: [0-9]*}/subpath/{sid}");

        // Then
        assertThat(action.getPath()).isEqualTo("{id}/subpath/{sid}");
    }

    @Test
    public void regexInformationCanBeRetrievedForParameter() {
        // When
        action = new SecuredRestAction(new MockHttpParameterFactory(), HttpMethod.POST, "{id: [0-9]*}/subpath");

        // Then
        assertThat(action.getPathParameterRegex("id")).isEqualTo("[0-9]*");
    }

    @Test
    public void multipleRegexInformationCanBeRetrievedForParameter() {
        // When
        String idRegex = "[0-9]*";
        String sidRegex = "[0-9]{1,9}";
        String imeiRegex = "[0-9\\:\\/]{15}";
        StringBuilder paramBuilder = new StringBuilder();
        paramBuilder.append("{id: ").append(idRegex).append("}");
        paramBuilder.append("/subpath");
        paramBuilder.append("/{sid: ").append(sidRegex).append("}");
        paramBuilder.append("/mobile");
        paramBuilder.append("/{imei: ").append(imeiRegex).append("}");

        action = new SecuredRestAction(new MockHttpParameterFactory(), HttpMethod.POST, paramBuilder.toString());

        // Then
        assertThat(action.getPathParameterRegex("id")).isEqualTo(idRegex);
        assertThat(action.getPathParameterRegex("sid")).isEqualTo(sidRegex);
        assertThat(action.getPathParameterRegex("imei")).isEqualTo(imeiRegex);
        assertThat(action.getPathParameterRegex("subpath")).isNull();
        assertThat(action.getPathParameterRegex("mobile")).isNull();
    }

    @Test
    public void regexWithEscapedAngleBracketsCanBeResolved() {
        // When
        String regex = "[a-zA-Z0-9\\{](-[a-zA-Z0-9])-[a-zA-Z0-9]{12}";
        action = new SecuredRestAction(new MockHttpParameterFactory(), HttpMethod.POST,
                "/{id:" + regex + "}");

        // Then
        assertThat(action.getPathParameterRegex("id")).isEqualTo(regex);
    }

    @Test
    public void multipleAndMoreComplexRegexInformationCanBeRetrievedForParameter() {
        // When
        String nameRegex = "[0-9]*";
        String idRegex = "[a-zA-Z0-9\\{](-[a-zA-Z0-9])-[a-zA-Z0-9]{12}";
        String id2Regex = "[a-zA-Z0-9\\{](-[a-zA-Z0-9\\}])-[a-zA-Z0-9]{15}";
        String emailRegex =
                "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\""
                        + "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\["
                        + "\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+"
                        + "[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}"
                        + "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]"
                        + ":(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]"
                        + "|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

        StringBuilder paramBuilder = new StringBuilder();
        paramBuilder.append("/{name: ").append(nameRegex).append("}");
        paramBuilder.append("/x");
        paramBuilder.append("/{id: ").append(idRegex).append("}");
        paramBuilder.append("/{id2: ").append(id2Regex).append("}");
        paramBuilder.append("/{email:").append(emailRegex).append("}");

        action = new SecuredRestAction(new MockHttpParameterFactory(), HttpMethod.POST, paramBuilder.toString());
        // Then
        assertThat(action.getPathParameterRegex("name")).isEqualTo(nameRegex);
        assertThat(action.getPathParameterRegex("id")).isEqualTo(idRegex);
        assertThat(action.getPathParameterRegex("id2")).isEqualTo(id2Regex);
        assertThat(action.getPathParameterRegex("email")).isEqualTo(emailRegex);
    }

    @Test
    public void nullShouldBeReturnedIfNoRegexIsDefined() {
        // When
        action = new SecuredRestAction(new MockHttpParameterFactory(), HttpMethod.POST, "{id}/subpath/{sid}");

        // Then
        assertThat(action.getPathParameterRegex("id")).isNull();
        assertThat(action.getPathParameterRegex("sid")).isNull();
    }

    @Test
    public void nullShouldBeReturnedIfParameterIsNotDefined() {
        // When
        action = new SecuredRestAction(new MockHttpParameterFactory(), HttpMethod.POST, "{id}/subpath/{sid}");

        // Then
        assertThat(action.getPathParameterRegex("subpath")).isNull();
    }

}
