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

import com.google.inject.Provider;

/**
 * For use in test cases where an {@link Provider} is required to provide an
 * object and the test case needs to provide a mock of the object.
 * <p>
 * Note that the same mock will be returned for every invocation of {{@link #get()}
 * (it behaves as a singleton) which may impact your tests, for example
 * if you rely on {@code ==}. If you're using mockito, consider using the
 * {@code com.gwtplatform.tester.mockito.MockProvider} instead.
 *
 * @param <T> The type of mock object provided
 */
public class MockProvider<T> implements Provider<T> {
    private final T mock;

    /**
     * Construct a {@link Provider} that will provide the mock object.
     *
     * @param mock The mock object to provide.
     */
    public MockProvider(T mock) {
        this.mock = mock;
    }

    public T get() {
        return this.mock;
    }
}
