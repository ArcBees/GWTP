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

import com.philbeaudoin.gwtp.dispatch.shared.Result;

/**
 * Contains a valid secure session id. Any {@link com.philbeaudoin.gwtp.dispatch.shared.Action}
 * which returns this result will automatically have the specified session id updated as the
 * current session id.
 * <p/>
 * <p/>
 * The {@link SimpleAuthenticationAction} class is an example of this, but more
 * complex authentication requests could be created if required. Simply create a
 * new action that has this class as its Result type. Eg:
 * <p/>
 * <pre>
 * public class MyAuthenticationAction extends Action&lt;SecureSessionResult&gt; {
 *     private String domain;
 * <p/>
 *     private String username;
 * <p/>
 *     private String rsaToken;
 *     ///....
 * }
 * </pre>
 * <p/>
 * <p/>
 * Then, create a handler on the server side, register it and
 *
 * @author David Peterson
 * @see SimpleAuthenticationAction
 */
public class SecureSessionResult implements Result {

  private static final long serialVersionUID = -6911635227963644372L;

  private String sessionId;

  SecureSessionResult() {
  }

  public SecureSessionResult( String sessionId ) {
    this.sessionId = sessionId;
  }

  public String getSessionId() {
    return sessionId;
  }
}
