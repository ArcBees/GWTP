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

package com.gwtplatform.dispatch.rpc.shared;

/**
 * A common use-case is returning a single value from an action. This provides a simple, type-safe superclass for such
 * results.
 * <p>
 * <b>Note:</b> Subclasses should provide both an empty constructor for serialization and a constructor with a single
 * value for normal use. It is recommended that the empty constructor is private or package-private.
 *
 * @param <T> The value type.
 */
public abstract class AbstractSimpleResult<T> implements Result {
    private static final long serialVersionUID = 1L;

    private T value;

    public AbstractSimpleResult(T value) {
        this.value = value;
    }

    protected AbstractSimpleResult() {
    }

    public T get() {
        return value;
    }

}
