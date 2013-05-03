/**
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.carstore.client.gin;

import com.google.gwt.inject.client.Ginjector;
import com.gwtplatform.carstore.client.application.cars.car.widget.CarPropertiesEditor;

/**
 * Methods defined here will be added to the generated Ginjector. While using GinUiBinder, you must define each
 * classes that is used inside a .ui.xml that use injection.
 */
public interface MyGinjector extends Ginjector {
    CarPropertiesEditor getPropertiesEditor();
}
