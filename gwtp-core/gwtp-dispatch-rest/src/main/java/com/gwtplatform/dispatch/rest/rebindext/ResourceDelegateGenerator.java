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

package com.gwtplatform.dispatch.rest.rebindext;

import java.util.Collection;

import javax.inject.Inject;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.gwtplatform.dispatch.rest.rebind2.AbstractGenerator;
import com.gwtplatform.dispatch.rest.rebind2.extension.ExtensionContext;
import com.gwtplatform.dispatch.rest.rebind2.extension.ExtensionGenerator;
import com.gwtplatform.dispatch.rest.rebind2.utils.ClassDefinition;
import com.gwtplatform.dispatch.rest.rebind2.utils.Logger;

public class ResourceDelegateGenerator extends AbstractGenerator implements ExtensionGenerator {
    @Inject
    ResourceDelegateGenerator(
            Logger logger,
            GeneratorContext context) {
        super(logger, context);
    }

    @Override
    public boolean canGenerate(ExtensionContext context) throws UnableToCompleteException {
        return false;
    }

    @Override
    public Collection<ClassDefinition> generate(ExtensionContext context) throws UnableToCompleteException {
        return null;
    }
}
