/*
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

package com.gwtplatform.tester;

/**
 * Mocking factory that transfer the work of creating a mock object to any
 * Mocking framework. User should create an implementation to inject in their
 * {@link MockingBinder} implementation.
 * <p>
 * <pre>
 * public class MockitoMockFactory implements MockFactory {
 *   public &lt;T&gt; T mock(Class&lt;T&gt; classToMock) {
 *     return Mockito.mock(classToMock);
 *   }
 * }
 * </pre>
 */
public interface MockFactory {
    <T> T mock(Class<T> classToMock);
}
