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

package com.google.gwt.event.shared;

/**
 * GwtEventHelper is class that let us set the source of event that are meant to
 * be set on the event bus. Without it, the source would always be <code>null</code> and
 * that's not the intended behavior. Users don't need to use it, we do it
 * automatically inside Gwt-Platform when you use fireEvent.
 * 
 * @author Christian Goudreau
 */
public class GwtEventHelper {
  /**
   * @param event The event that we need to set the source on.
   * @param source The source that fired the event.
   */
  public static void setSource(GwtEvent<?> event, Object source) {
    event.setSource(source);
  }

}
