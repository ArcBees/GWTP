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

package com.gwtplatform.dispatch.rest.rebind.parameter;

import javax.ws.rs.MatrixParam;

import org.junit.Test;

import com.google.gwt.core.ext.typeinfo.HasAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class MatrixParamValueResolverTest {
    private static final String VALUE = "tiger";

    private final MatrixParamValueResolver resolver = new MatrixParamValueResolver();

    @Test
    public void resolve_matrixParam() {
        // given
        MatrixParam param = mock(MatrixParam.class);
        given(param.value()).willReturn(VALUE);

        // when
        String value = resolver.resolve(param);

        // then
        assertThat(value).isEqualTo(VALUE);
    }

    @Test
    public void resolve_hasAnnotations_hasMatrixParam() {
        // given
        MatrixParam param = mock(MatrixParam.class);
        given(param.value()).willReturn(VALUE);

        HasAnnotations hasAnnotations = mock(HasAnnotations.class);
        given(hasAnnotations.getAnnotation(MatrixParam.class)).willReturn(param);

        // when
        String value = resolver.resolve(hasAnnotations);

        // then
        assertThat(value).isEqualTo(VALUE);
    }

    @Test(expected = NullPointerException.class)
    public void resolve_hasAnnotations_hasNoMatrixParams() {
        // given
        HasAnnotations hasAnnotations = mock(HasAnnotations.class);

        // when
        resolver.resolve(hasAnnotations);
    }
}
