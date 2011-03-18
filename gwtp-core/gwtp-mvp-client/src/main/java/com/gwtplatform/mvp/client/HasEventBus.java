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

import com.google.gwt.event.shared.HasHandlers;

/**
 * Marker interface that tell that an object is bound to an
 * {@link com.google.gwt.event.shared.EventBus}. Objects implementing this type
 * can be used as a source when firing an event on the event bus.
 *
 * {@link Deprecated} use directly {@link HasHandlers} instead.
 *
 * @author Christian Goudreau
 */
@Deprecated
public interface HasEventBus extends HasHandlers {
}
