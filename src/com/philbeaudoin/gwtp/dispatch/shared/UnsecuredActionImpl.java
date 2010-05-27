/**
 * Copyright 2010 Gwt-Platform
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

package com.philbeaudoin.gwtp.dispatch.shared;

/**
 * An {@link Action} that uses the standard service name {@code "dispatch"}.
 * Actions inheriting from this are <b>not</b> secured against XSRF attacks,
 * and they will work even if you do not configure a {@link SecurityCookie}.
 * 
 * @author Philippe Beaudoin
 *
 * @param <R> The {@link Result} type.
 */
public class UnsecuredActionImpl<R extends Result> implements Action<R> {

  @Override
  public String getServiceName() {
    return DEFAULT_SERVICE_NAME;
  }
  
  @Override
  public boolean isSecured() {
    return false;
  }  
}
