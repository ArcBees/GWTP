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

package com.philbeaudoin.gwtp.dispatch.server.guice;

import com.philbeaudoin.gwtp.dispatch.server.Dispatch;
import com.philbeaudoin.gwtp.dispatch.server.standard.AbstractStandardDispatchServlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GuiceStandardDispatchServlet extends AbstractStandardDispatchServlet {

  private static final long serialVersionUID = -3370273399970264625L;

  private final Dispatch dispatch;

  @Inject
  public GuiceStandardDispatchServlet( Dispatch dispatch ) {
    this.dispatch = dispatch;
  }

  @Override
  protected Dispatch getDispatch() {
    return dispatch;
  }
}
