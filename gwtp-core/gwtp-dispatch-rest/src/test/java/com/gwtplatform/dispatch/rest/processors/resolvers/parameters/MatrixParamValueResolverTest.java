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

package com.gwtplatform.dispatch.rest.processors.resolvers.parameters;

import javax.lang.model.element.VariableElement;
import javax.ws.rs.MatrixParam;

import org.junit.Test;

import com.gwtplatform.dispatch.rest.processors.AnnotatedElementBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class MatrixParamValueResolverTest {
    private static final String VALUE = "tiger";

    private MatrixParamValueResolver resolver = new MatrixParamValueResolver();

    @Test
    public void resolve_matrixParam() {
        // given
        VariableElement variableElement = mock(VariableElement.class);

        AnnotatedElementBuilder matrixParamBuilder = new AnnotatedElementBuilder(variableElement, MatrixParam.class);
        matrixParamBuilder.setAnnotationValue("value", VALUE);

        // when
        String value = resolver.resolve(variableElement);

        // then
        assertThat(value).isEqualTo(VALUE);
    }
}
