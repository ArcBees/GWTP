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

public class UnsupportedActionException extends ServiceException {

  private static final long serialVersionUID = -3362561625013898012L;

  /**
   * For serialization.
   */
  UnsupportedActionException() {
  }

  @SuppressWarnings({"unchecked"})
  public UnsupportedActionException( Action<? extends Result> action ) {
    this( ( Class<? extends Action<? extends Result>> ) action.getClass() );
  }

  public UnsupportedActionException( Class<? extends Action<? extends Result>> actionClass ) {
    super( "No handler is registered for " + actionClass.getName() );
  }

}
