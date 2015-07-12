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

package com.gwtplatform.dispatch.rest.processors.bindings.gin;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import com.google.auto.service.AutoService;
import com.google.common.base.Optional;
import com.google.inject.TypeLiteral;
import com.gwtplatform.dispatch.rest.processors.AbstractContextProcessor;
import com.gwtplatform.dispatch.rest.processors.bindings.BindingContext;
import com.gwtplatform.dispatch.rest.processors.bindings.BindingsProcessor;
import com.gwtplatform.dispatch.rest.processors.domain.Type;
import com.gwtplatform.dispatch.rest.processors.outputter.OutputBuilder;

@AutoService(BindingsProcessor.class)
public class GinModuleProcessor extends AbstractContextProcessor<BindingContext, Void> implements BindingsProcessor {
    private static final String TEMPLATE = "com/gwtplatform/dispatch/rest/processors/bindings/gin/GinModule.vm";
    private static final String QUALIFIED_NAME = "com.gwtplatform.dispatch.rest.client.RestGinModule";

    private final List<GinBinding> bindings;

    private Type impl;
    private JavaFileObject sourceFile;
    private boolean containsTypeLiteral;

    public GinModuleProcessor() {
        this.bindings = new ArrayList<>();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        impl = new Type(QUALIFIED_NAME);
        sourceFile = outputter.prepareSourceFile(impl);
    }

    @Override
    public Void process(BindingContext context) {
        Type implementer = context.getImplementer();
        Optional<Type> implemented = context.getImplemented();
        Optional<Type> scope = context.getScope();

        bindings.add(new GinBinding(implementer, implemented.orNull(), scope.orNull(), context.isEagerSingleton()));
        containsTypeLiteral |= implementer.isParameterized()
                || (implemented.isPresent() && implemented.get().isParameterized());

        return null;
    }

    @Override
    public void processLast() {
        logger.debug("Generating GIN module `%s`.", impl.getQualifiedName());

        OutputBuilder outputBuilder = outputter
                .withTemplateFile(TEMPLATE)
                .withParam("bindings", bindings);

        if (containsTypeLiteral) {
            outputBuilder = outputBuilder.withImport(TypeLiteral.class.getCanonicalName());
        }

        outputBuilder.writeTo(impl, sourceFile);
    }
}
