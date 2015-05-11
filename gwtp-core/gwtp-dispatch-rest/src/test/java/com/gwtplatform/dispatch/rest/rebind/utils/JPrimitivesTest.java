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

package com.gwtplatform.dispatch.rest.rebind.utils;

import org.jukito.All;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(JukitoRunner.class)
public class JPrimitivesTest {
    private static class BoxedTestCase {
        private final Class<?> clazz;
        private final boolean result;

        private BoxedTestCase(
                Class<?> clazz,
                boolean result) {
            this.clazz = clazz;
            this.result = result;
        }
    }

    public static class Module extends JukitoModule {
        @Override
        protected void configureTest() {
            bindManyInstances(BoxedTestCase.class,
                    new BoxedTestCase(Boolean.class, true),
                    new BoxedTestCase(Byte.class, true),
                    new BoxedTestCase(Character.class, true),
                    new BoxedTestCase(Double.class, true),
                    new BoxedTestCase(Float.class, true),
                    new BoxedTestCase(Integer.class, true),
                    new BoxedTestCase(Long.class, true),
                    new BoxedTestCase(Object.class, false),
                    new BoxedTestCase(Short.class, true),
                    new BoxedTestCase(String.class, false),
                    new BoxedTestCase(Void.class, true)
            );
        }
    }

    @Test
    public void isPrimitive_primitive() {
        // given
        JType jType = mock(JType.class);

        // when
        boolean primitive = JPrimitives.isPrimitive(jType);

        assertThat(primitive).isFalse();
    }

    @Test
    public void isPrimitive_notAPrimitive() {
        // given
        JType jType = mock(JType.class);
        given(jType.isPrimitive()).willReturn(JPrimitiveType.BOOLEAN);

        // when
        boolean primitive = JPrimitives.isPrimitive(jType);

        assertThat(primitive).isTrue();
    }

    @Test
    public void isBoxed(@All BoxedTestCase testCase) {
        // given
        String className = testCase.clazz.getName();
        boolean result = testCase.result;

        JType jType = mock(JType.class);
        given(jType.getQualifiedSourceName()).willReturn(className);

        // when
        boolean boxed = JPrimitives.isBoxed(jType);

        // then
        assertThat(boxed)
                .describedAs("isBoxed(%s) should return %s.", className, result)
                .isEqualTo(result);
    }
}
