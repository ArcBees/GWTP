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

package com.gwtplatform.dispatch.server.actionvalidator;

import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

/**
 * Implementors must provide an implementation of this interface and provide it
 * to the {@link DispatchService} implementation so that it can check whether or
 * not the action can be executed. Doing so will often requiring session
 * information, which can be done by injecting a {@code Provider<HttpSession>}
 * into the validator and calling {@code httpSessionProvider.get()} within the
 * {@link #isValid} method.
 * <p />
 * You should think of annotating your validators with {@code @RequestScoped} or
 * {@code @Singleton}.
 * 
 * @author David Peterson
 * @author Christian Goudreau
 */
public interface ActionValidator {
  /**
   * Validate whether or not that {@link Action} can be executed at this time.
   * 
   * @param action The action that called this validator.
   * 
   * @return {@code true} if the action can be executed, {@code false}
   *         otherwise.
   */
  boolean isValid(Action<? extends Result> action);
}