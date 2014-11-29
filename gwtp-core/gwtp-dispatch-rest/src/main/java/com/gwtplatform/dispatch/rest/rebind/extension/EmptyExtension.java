/**
 * Copyright 2014 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.rebind.extension;

import java.util.Collection;

import com.google.gwt.core.ext.UnableToCompleteException;
import com.gwtplatform.dispatch.rest.rebind.utils.ClassDefinition;

/**
 * Dummy extension used by {@link ExtensionModule} to initialize the {@link com.google.inject.multibindings.Multibinder
 * Multibinder}.
 */
public class EmptyExtension implements ExtensionGenerator {
    @Override
    public boolean canGenerate(ExtensionContext context) {
        return false;
    }

    @Override
    public Collection<ClassDefinition> generate(ExtensionContext context) throws UnableToCompleteException {
        return null;
    }
}
