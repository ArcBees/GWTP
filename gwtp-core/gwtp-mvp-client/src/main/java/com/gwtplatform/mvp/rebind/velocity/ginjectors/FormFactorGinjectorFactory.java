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

package com.gwtplatform.mvp.rebind.velocity.ginjectors;

import com.google.inject.assistedinject.Assisted;

public interface FormFactorGinjectorFactory {
    FormFactorGinjectorGenerator createGinjector(
            @Assisted("velocityTemplate") String velocityTemplate,
            @Assisted("propertyName") String propertyName,
            @Assisted("implName") String implName);

    FormFactorGinjectorProviderGenerator createGinjectorProvider(
            @Assisted("velocityTemplate") String velocityTemplate,
            @Assisted("implName") String implName);

    GinjectorProviderGenerator createDefaultGinjectorProvider(
            @Assisted("velocityTemplate") String velocityTemplate,
            @Assisted("implName") String implName);
}
