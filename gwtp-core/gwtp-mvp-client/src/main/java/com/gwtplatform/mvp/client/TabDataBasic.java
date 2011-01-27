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

package com.gwtplatform.mvp.client;

/**
 * The basic type of {@link TabData}, with just enough information to create
 * a basic {@link Tab}.
 *
 * @author Philippe Beaudoin
 */
public class TabDataBasic implements TabData {

  private final String label;
  private final float priority;
  
  public TabDataBasic(String label, float priority) {
    this.label = label;
    this.priority = priority;
  }

  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public float getPriority() {
    return priority;
  }
}
