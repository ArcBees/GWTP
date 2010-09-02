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
 * Interface meant to be extended by your view interface to be able to set your
 * view's control inside your presenter's constructor. <b>Important</b>, don't
 * forget to call getView().setControls() in your presenter's constructor.
 * 
 * @param <C> Your {@link Controls}'s interface type.
 * 
 * @author Christian Goudreau
 */
public interface HasControls<C extends Controls> {
  void setControls(C controls);
}
