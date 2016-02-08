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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.tools.FileObject;

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
    private final Map<Type, Multimap<Type, GinBinding>> setBinders;
    private final Multimap<Type, Type> subModules;
    private final Map<Type, FileObject> sourceFiles;

    public GinModuleProcessor() {
        bindings = HashMultimap.create();
        setBinders = new HashMap<>();
        subModules = HashMultimap.create();
        sourceFiles = new HashMap<>();
    }

    @Override
    public Void process(BindingContext context) {
        ensureSourceFileIsCreated(context);

        if (context.getImplementation().isPresent()) {
            if (context.isSubModule()) {
                createSubModule(context);
            } else {
                createBinding(context);
            }
        }

        if (context.isOutputNow()) {
            flushModule(context.getModuleType());
        }

        return null;
    }

    private void ensureSourceFileIsCreated(BindingContext context) {
        Type moduleType = context.getModuleType();

        if (!sourceFiles.containsKey(moduleType)) {
            FileObject file = outputter.prepareSourceFile(moduleType);
            sourceFiles.put(moduleType, file);
        }
    }

    private void createSubModule(BindingContext context) {
        Type implementer = context.getImplementation().get();

        subModules.put(context.getModuleType(), implementer);
    }

    private void createBinding(BindingContext context) {
        GinBinding binding = new GinBinding(
                context.getImplementation().get(),
                context.getParent().orNull(),
                context.getScope().orNull(),
                context.isEagerSingleton());

        if (context.isSetBinder()) {
            createSetBinding(context, binding);
        } else {
            bindings.put(context.getModuleType(), binding);
        }
    }

    private void createSetBinding(BindingContext context, GinBinding binding) {
        Type moduleType = context.getModuleType();
        Optional<Type> implemented = binding.getImplemented();
        Type implementedType = implemented.get();
        Multimap<Type, GinBinding> setBindings;

        if (setBinders.containsKey(implementedType)) {
            setBindings = setBinders.get(moduleType);
        } else {
            setBindings = HashMultimap.create();
            setBinders.put(moduleType, setBindings);
        }

        setBindings.put(implementedType, binding);
    }

    private void flushModule(Type moduleType) {
        outputModule(moduleType);

        sourceFiles.remove(moduleType);
        bindings.removeAll(moduleType);
        subModules.removeAll(moduleType);
        setBinders.remove(moduleType);
    }

    @Override
    public void processLast() {
        for (Type moduleType : sourceFiles.keySet()) {
            outputModule(moduleType);
        }
    }

    private void outputModule(Type moduleType) {
        logger.debug("Generating GIN module `%s`.", moduleType.getQualifiedName());

        FileObject fileObject = sourceFiles.get(moduleType);
        Multimap<Type, GinBinding> setBindings = setBinders.get(moduleType);
        Set<Entry<Type, Collection<GinBinding>>> setBindingsEntries = setBindings == null
                ? new HashSet<Entry<Type, Collection<GinBinding>>>()
                : setBindings.asMap().entrySet();

        outputter
                .configure(TEMPLATE)
                .withParam("bindings", bindings.get(moduleType))
                .withParam("setBindings", setBindingsEntries)
                .withParam("subModules", subModules.get(moduleType))
                .writeTo(moduleType, fileObject);
    }
}
