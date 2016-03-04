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

package com.gwtplatform.processors.tools.utils;

import org.jukito.All;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.base.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JukitoRunner.class)
public class PrimitivesTest {
    public static class Module extends JukitoModule {
        @Override
        protected void configureTest() {
            bindManyNamedInstances(PrimitivesTestCase.class, PRIMITIVES,
                    new PrimitivesTestCase(boolean.class, Primitives.BOOLEAN),
                    new PrimitivesTestCase(byte.class, Primitives.BYTE),
                    new PrimitivesTestCase(char.class, Primitives.CHAR),
                    new PrimitivesTestCase(double.class, Primitives.DOUBLE),
                    new PrimitivesTestCase(float.class, Primitives.FLOAT),
                    new PrimitivesTestCase(int.class, Primitives.INT),
                    new PrimitivesTestCase(long.class, Primitives.LONG),
                    new PrimitivesTestCase(Object.class, null),
                    new PrimitivesTestCase(short.class, Primitives.SHORT),
                    new PrimitivesTestCase(String.class, null),
                    new PrimitivesTestCase(void.class, Primitives.VOID));

            bindManyNamedInstances(PrimitivesTestCase.class, BOXED,
                    new PrimitivesTestCase(Boolean.class, Primitives.BOOLEAN),
                    new PrimitivesTestCase(Byte.class, Primitives.BYTE),
                    new PrimitivesTestCase(Character.class, Primitives.CHAR),
                    new PrimitivesTestCase(Double.class, Primitives.DOUBLE),
                    new PrimitivesTestCase(Float.class, Primitives.FLOAT),
                    new PrimitivesTestCase(Integer.class, Primitives.INT),
                    new PrimitivesTestCase(Long.class, Primitives.LONG),
                    new PrimitivesTestCase(Object.class, null),
                    new PrimitivesTestCase(Short.class, Primitives.SHORT),
                    new PrimitivesTestCase(String.class, null),
                    new PrimitivesTestCase(Void.class, Primitives.VOID));
        }
    }

    private static class PrimitivesTestCase {
        private final String name;
        private final Primitives result;

        private PrimitivesTestCase(
                Class<?> clazz,
                Primitives result) {
            this.name = clazz.getCanonicalName();
            this.result = result;
        }

        @Override
        public String toString() {
            return name + " should result with " + result;
        }
    }

    private static final String PRIMITIVES = "primitives";
    private static final String BOXED = "boxed";

    @Test
    public void findByPrimitive(@All(PRIMITIVES) PrimitivesTestCase testCase) {
        // when
        Optional<Primitives> primitive = Primitives.findByPrimitive(testCase.name);

        // then
        verifyTestCaseResult(testCase, primitive);
    }

    @Test
    public void findByBoxed(@All(BOXED) PrimitivesTestCase testCase) {
        // when
        Optional<Primitives> primitive = Primitives.findByBoxed(testCase.name);

        // then
        verifyTestCaseResult(testCase, primitive);
    }

    private void verifyTestCaseResult(PrimitivesTestCase testCase, Optional<Primitives> result) {
        if (testCase.result != null) {
            assertThat(result.isPresent()).describedAs(testCase.toString()).isTrue();
            assertThat(result.get()).describedAs(testCase.toString()).isEqualTo(testCase.result);
        } else {
            assertThat(result.isPresent()).describedAs(testCase.toString()).isFalse();
        }
    }
}
