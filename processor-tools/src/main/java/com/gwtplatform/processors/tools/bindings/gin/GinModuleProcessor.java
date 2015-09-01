/*
 * Copyright 2015 ArcBees Inc.
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

package com.gwtplatform.processors.tools.bindings.gin;

import java.util.HashMap;
import java.util.Map;

import javax.tools.JavaFileObject;

import com.google.auto.service.AutoService;
import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.gwtplatform.processors.tools.AbstractContextProcessor;
import com.gwtplatform.processors.tools.bindings.BindingContext;
import com.gwtplatform.processors.tools.bindings.BindingsProcessor;
import com.gwtplatform.processors.tools.domain.Type;

@AutoService(BindingsProcessor.class)
public class GinModuleProcessor extends AbstractContextProcessor<BindingContext, Void> implements BindingsProcessor {
    private static final String TEMPLATE = "com/gwtplatform/processors/tools/bindings/gin/GinModule.vm";

    private final Multimap<Type, GinBinding> bindings;
    private final Multimap<Type, Type> subModules;
    private final Map<Type, JavaFileObject> sourceFiles;

    public GinModuleProcessor() {
        bindings = HashMultimap.create();
        subModules = HashMultimap.create();
        sourceFiles = new HashMap<>();
    }

    @Override
    public Void process(BindingContext context) {
        Type moduleType = findOrCreateSourceFile(context);

        if (context.isSubModule()) {
            createSubModule(context, moduleType);
        } else {
            createBinding(context, moduleType);
        }

        return null;
    }

    public Type findOrCreateSourceFile(BindingContext context) {
        Type moduleType = context.getModuleType();
        if (!sourceFiles.containsKey(moduleType)) {
            JavaFileObject file = outputter.prepareSourceFile(moduleType);
            sourceFiles.put(moduleType, file);
        }

        return moduleType;
    }

    public void createSubModule(BindingContext context, Type moduleType) {
        Type implementer = context.getImplementer();

        subModules.put(moduleType, implementer);
    }

    public void createBinding(BindingContext context, Type moduleType) {
        Type implementer = context.getImplementer();

        Optional<Type> implemented = context.getImplemented();
        Optional<Type> scope = context.getScope();
        GinBinding binding =
                new GinBinding(implementer, implemented.orNull(), scope.orNull(), context.isEagerSingleton());

        bindings.put(moduleType, binding);
    }

    @Override
    public void processLast() {
        for (Map.Entry<Type, JavaFileObject> entry : sourceFiles.entrySet()) {
            Type moduleType = entry.getKey();
            logger.debug("Generating GIN module `%s`.", moduleType.getQualifiedName());

            outputter
                    .withTemplateFile(TEMPLATE)
                    .withParam("bindings", bindings.get(moduleType))
                    .withParam("subModules", subModules.get(moduleType))
                    .writeTo(moduleType, entry.getValue());
        }
    }
}
