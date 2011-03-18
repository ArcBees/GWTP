/**
 * Copyright 2010 ArcBees Inc.
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

package com.gwtplatform.dispatch.shared;

/**
 * Provides support for simple update response which contain both the old value
 * and new value.
 *
 * @author David Peterson
 *
 * @param <T> The value type.
 */
public abstract class AbstractUpdateResult<T> implements Result {

  private T newValue;

  private T oldValue;

  public AbstractUpdateResult(T oldValue, T newValue) {
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  /**
   * For serialization support only. Subclasses should provide an package-local
   * (aka default) empty constructor.
   */
  protected AbstractUpdateResult() {
  }

  /**
   * The new/current value.
   *
   * @return The new value.
   */
  public T getNew() {
    return newValue;
  }

  /**
   * The previous value.
   *
   * @return The old value.
   */
  public T getOld() {
    return oldValue;
  }
}
