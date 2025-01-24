/*
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

package com.gwtplatform.mvp.client.annotations;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
/**
* Use this annotation in classes implementing {@link com.gwtplatform.mvp.client.Presenter} and that have
* slots to display child presenters. This annotates every static field
* containing a type of event that is monitored by this presenter. When handling
* this event, a child presenter is inserted in the presenter's view. You should
* make sure the view handles event of this type in its
* {@link com.gwtplatform.mvp.client.View#setInSlot(Object, com.google.gwt.user.client.ui.Widget)} method.
* @deprecated since 1.5. use {@link com.gwtplatform.mvp.client.presenter.slots.NestedSlot} instead.
*/
@Deprecated
@Target(ElementType.FIELD)
public @interface ContentSlot {
}
