/**
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

package com.gwtplatform.dispatch.rest.test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.gwtplatform.dispatch.rest.client.ResourceDelegate;

/**
 * Utility methods to help mocking {@link com.gwtplatform.dispatch.rest.client.ResourceDelegate}s.
 * <p/>
 * <b>Important:</b> <ul> <li>{@link #init()} must be called before each test. Not doing it may cause unexpected
 * results. Using JUnit's {@link org.junit.Before @Before} is perfect for that.</li> <li>{@link
 * DelegateMocking#useResource(Object) useResource(Object)} must be called before any other methods <b>exactly once</b>
 * and <b>before</b> any other methods for the delegate is called. Assertion errors will be thrown otherwise.</li>
 * </ul>
 */
public class DelegateTestUtils {
    public static final Map<ResourceDelegate<?>, DelegateMocking<?>> delegateMockings
            = Collections.synchronizedMap(new HashMap<ResourceDelegate<?>, DelegateMocking<?>>());

    /**
     * Initializes the mocking utils. <b>Must</b> be called before each test.
     */
    public static void init() {
        delegateMockings.clear();
    }

    /**
     * Access or create the mocking context of <code>delegate</code>.
     *
     * @return the mocking context of <code>delegate</code>
     */
    @SuppressWarnings("unchecked")
    public static <R> DelegateMocking<R> givenDelegate(ResourceDelegate<R> delegate) {
        DelegateMocking<R> delegateMocking;

        if (delegateMockings.containsKey(delegate)) {
            delegateMocking = (DelegateMocking<R>) delegateMockings.get(delegate);
        } else {
            delegateMocking = new DelegateMocking<R>(delegate);
            delegateMockings.put(delegate, delegateMocking);
        }

        return delegateMocking;
    }
}
