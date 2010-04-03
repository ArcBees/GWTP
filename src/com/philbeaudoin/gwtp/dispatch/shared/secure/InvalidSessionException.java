package com.philbeaudoin.gwtp.dispatch.shared.secure;

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



import com.philbeaudoin.gwtp.dispatch.shared.ServiceException;

public class InvalidSessionException extends ServiceException {

  private static final long serialVersionUID = -3591782650879045864L;

  public InvalidSessionException() {
    super();
  }

  public InvalidSessionException( String message ) {
    super( message );
  }
}
