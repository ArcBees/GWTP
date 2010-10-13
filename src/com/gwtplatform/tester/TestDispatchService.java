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

package com.gwtplatform.tester;

import com.google.inject.Inject;
import com.gwtplatform.dispatch.client.DispatchService;
import com.gwtplatform.dispatch.server.Dispatch;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.ActionException;
import com.gwtplatform.dispatch.shared.Result;
import com.gwtplatform.dispatch.shared.ServiceException;

/**
 * This class is an implementation of {@link DispatchService} for use with test
 * cases that configure guice using a {@link MockHandlerModule}.
 * 
 * @author Brendan Doherty
 */

class TestDispatchService implements DispatchService {
  private Dispatch dispatch;

  @Inject
  public TestDispatchService(Dispatch dispatch) {
    this.dispatch = dispatch;
  }

  @Override
  public Result execute(String cookieSentByRPC, Action<?> action)
      throws ActionException, ServiceException {
    return dispatch.execute(action);
  }

  @Override
  public void undo(String cookieSentByRPC, Action<Result> action, Result result)
      throws ActionException, ServiceException {
    dispatch.undo(action, result);
  }
}
