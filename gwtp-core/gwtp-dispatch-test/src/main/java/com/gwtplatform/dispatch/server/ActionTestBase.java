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

package com.gwtplatform.dispatch.server;

import junit.framework.Assert;

import com.gwtplatform.dispatch.server.actionhandler.TestActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.gwtplatform.dispatch.shared.ServiceException;
import com.gwtplatform.dispatch.shared.action.TestAction;
import com.gwtplatform.dispatch.shared.action.TestResult;

/**
 * @author Peter Simun
 */
public class ActionTestBase {

  protected void testAction(Dispatch dispatch) throws ActionException, ServiceException {
    TestAction action = new TestAction(TestActionHandler.MESSAGE);
    TestResult result = dispatch.execute(action);
    Assert.assertTrue("Invalid action result! Processing error occured", result.getResult());
  }
}