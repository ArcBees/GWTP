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

package com.gwtplatform.samples.tab.client;

import com.gwtplatform.mvp.client.TabDataBasic;
import com.gwtplatform.mvp.client.proxy.Gatekeeper;

/**
 * This class allows the definition of tabs that can be protected by
 * any {@link Gatekeeper}.
 *
 * @author Philippe Beaudoin
 */
public class TabDataExt extends TabDataBasic {

  private final Gatekeeper gatekeeper;

  public TabDataExt(String label, float priority, Gatekeeper gatekeeper) {
    super(label, priority);
    this.gatekeeper = gatekeeper;
  }

  public Gatekeeper getGatekeeper() {
    return gatekeeper;
  }

}
