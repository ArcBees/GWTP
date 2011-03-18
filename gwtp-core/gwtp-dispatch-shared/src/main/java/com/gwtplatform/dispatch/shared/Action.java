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

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * An action represents a command sent to the
 * {@link com.gwtplatform.dispatch.server.Dispatch}. It has a specific result
 * type which is returned if the action is successful. Your implementation
 * should override {@link #getServiceName} to return a default service url. If
 * you use a {@link SecurityCookie} to prevent XSRF attacks and you want this
 * action to be secured against such attacks (i.e. it's not meant to be an
 * anonymous action) then you should override {@link #isSecured()} to return
 * {@code true}.
 * <p />
 * You can usually inherit from {@link ActionImpl} or
 * {@link UnsecuredActionImpl} instead.
 *
 * @author David Peterson
 * @param <R> The {@link Result} type.
 */
public interface Action<R extends Result> extends IsSerializable {

  /**
   * The URL of the service used by default.
   */
  String DEFAULT_SERVICE_NAME = "dispatch/";

  /**
   * Access the name of the service, which will be used as the URL path to
   * access the action.
   *
   * @return The service name.
   */
  String getServiceName();

  /**
   * Verifies if the action is secured. Secured actions perform a number of
   * extra security checks, such as validating the {@link SecurityCookie} to
   * foil XSRF attacks.
   * <p />
   * <b>Important!</b> Make sure your method returns a value that does not
   * depend on client-side information, otherwise it could be tampered with to
   * turn a secure action into an insecure one. An example of a bad practice
   * would be to store a {@code boolean secured} member and return that. Since
   * this field is serialized, the user could change it on his side. A simple
   * and good practice is simply to {@code return true;} or
   * {@code return false;}.
   *
   * @return {@code true} if the action should be secured against XSRF attacks,
   *         {@code false} otherwise.
   */
  boolean isSecured();
}
