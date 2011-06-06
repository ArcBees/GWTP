/**
 * Copyright 2011 ArcBees Inc.
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

package com.gwtplatform.dispatch.server.actionhandler;

import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.shared.ActionException;
import com.gwtplatform.dispatch.shared.action.TestAction;
import com.gwtplatform.dispatch.shared.action.TestResult;

/**
 * @author Peter Simun
 */
public class TestActionHandler extends AbstractActionHandler<TestAction, TestResult> {

  public static final String MESSAGE = "This is test message!";

  public TestActionHandler() {
    super(TestAction.class);
  }

  @Override
  public TestResult execute(TestAction action, ExecutionContext context) throws ActionException {
    if (action.getTestMessage().equals(MESSAGE)) {
      return new TestResult(true);
    }
    return new TestResult(false);
  }

  @Override
  public void undo(TestAction action, TestResult result, ExecutionContext context) throws ActionException {
    // No undo support
  }
}