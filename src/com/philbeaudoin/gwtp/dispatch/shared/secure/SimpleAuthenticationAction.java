/**
 * Copyright 2010 Philippe Beaudoin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.philbeaudoin.gwtp.dispatch.shared.secure;

import com.philbeaudoin.gwtp.dispatch.shared.Action;

/**
 * A simple username/password authentication request. If successful, a session
 * has been created and a {@link SecureSessionResult} is returned.
 * 
 * @author David Peterson
 */
public class SimpleAuthenticationAction implements Action<SecureSessionResult> {

  private static final long serialVersionUID = -9035569517961908934L;

  private String username;

  private String password;

  SimpleAuthenticationAction() {
  }

  public SimpleAuthenticationAction( String username, String password ) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }
}
