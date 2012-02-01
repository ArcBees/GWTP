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

package com.gwtplatform.dispatch.server.spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gwtplatform.dispatch.server.ActionTestBase;
import com.gwtplatform.dispatch.server.Dispatch;
import com.gwtplatform.dispatch.shared.ActionException;
import com.gwtplatform.dispatch.shared.ServiceException;

/**
 * @author Peter Simun
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"spring-test-context.xml"})
public class ActionSpringTest extends ActionTestBase {

  @Autowired
  private Dispatch dispatchService;

  @Test
  public void testAction() throws ActionException, ServiceException {
    super.testAction(dispatchService);
  }
}