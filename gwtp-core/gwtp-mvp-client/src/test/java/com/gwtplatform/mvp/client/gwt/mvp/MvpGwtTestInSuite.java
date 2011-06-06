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

package com.gwtplatform.mvp.client.gwt.mvp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.gwtplatform.mvp.client.DelayedBindRegistry;

/**
 * Integration test for various components of GWTP's MVP module.
 *
 * @author Philippe Beaudoin
 */
public class MvpGwtTestInSuite extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.gwtplatform.mvp.MvpGwtTest";
  }

  GinjectorTestUtilGwt ginjector;
  MainPresenterTestUtilGwt presenter;

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    InstantiationCounterTestUtilGwt.resetCounter();
    ginjector = GWT.create(GinjectorTestUtilGwt.class);
    DelayedBindRegistry.bind(ginjector);
  }

  /**
   * Verifies that the ginjector is created only once.
   */
  public void testShouldCreateOnlyOneGinjector() {
    ginjector.getPlaceManager().revealCurrentPlace();
    assertEquals(1, InstantiationCounterTestUtilGwt.getCounter());
  }
}
