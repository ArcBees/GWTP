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

import com.gwtplatform.common.shared.UrlUtils;
import com.gwtplatform.dispatch.rest.client.core.parameters.FormParameter;
import com.gwtplatform.dispatch.rest.client.core.parameters.QueryParameter;
import com.gwtplatform.dispatch.rest.client.testutils.SecuredRestAction;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class AbstractRestActionTest {
    private static final String PARAM_NAME_1 = "someName";
    private static final String PARAM_NAME_2 = "someOtherName";
    private static final String PARAM_VALUE_1 = "someValue";
    private static final int PARAM_VALUE_2 = 7;

    private AbstractRestAction<Void> action;
    private UrlUtils urlUtils;

    @Before
    public void setUp() {
        urlUtils = mock(UrlUtils.class);
        action = new SecuredRestAction(HttpMethod.POST, "");
    }

    @Test
    public void addParam_null_doesNotAddParam() {
        // When
        action.addParam(new QueryParameter(PARAM_NAME_1, null, null, urlUtils));

        // Then
        assertTrue(action.getParameters(Type.QUERY).isEmpty());
    }

    @Test
    public void addParam_string_addsParam() {
        // When
        action.addParam(new QueryParameter(PARAM_NAME_1, PARAM_VALUE_1, null, urlUtils));
        action.addParam(new QueryParameter(PARAM_NAME_2, PARAM_VALUE_2, null, urlUtils));

        // Then
        assertThat(action.getParameters(Type.QUERY))
                .hasSize(2)
                .extracting("name", "object")
                .containsExactly(
                        tuple(PARAM_NAME_1, PARAM_VALUE_1),
                        tuple(PARAM_NAME_2, PARAM_VALUE_2));
    }

    @Test
    public void getParams_stripsOutNulls() {
        // When
        action.addParam(new FormParameter(PARAM_NAME_1, null, null, urlUtils));
        action.addParam(new FormParameter(PARAM_NAME_2, PARAM_VALUE_2, null, urlUtils));

        // Then
        assertThat(action.getParameters(Type.FORM))
                .hasSize(1)
                .extracting("name", "object")
                .containsExactly(tuple(PARAM_NAME_2, PARAM_VALUE_2));
    }
}
