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

package com.gwtplatform.dispatch.rest.processors.serialization.jackson;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.processing.ProcessingEnvironment;
import javax.inject.Singleton;
import javax.tools.JavaFileObject;
import javax.ws.rs.core.MediaType;

import com.google.auto.service.AutoService;
import com.google.common.base.Optional;
import com.gwtplatform.dispatch.rest.client.serialization.JacksonMapperProvider;
import com.gwtplatform.dispatch.rest.processors.AbstractContextProcessor;
import com.gwtplatform.dispatch.rest.processors.ContextProcessors;
import com.gwtplatform.dispatch.rest.processors.bindings.BindingContext;
import com.gwtplatform.dispatch.rest.processors.bindings.BindingsProcessor;
import com.gwtplatform.dispatch.rest.processors.domain.Type;
import com.gwtplatform.dispatch.rest.processors.serialization.SerializationContext;
import com.gwtplatform.dispatch.rest.processors.serialization.SerializationProcessor;
import com.gwtplatform.dispatch.rest.processors.utils.Primitives;
import com.gwtplatform.dispatch.rest.shared.ContentType;

import static com.google.common.collect.Iterables.isEmpty;
import static com.gwtplatform.dispatch.rest.processors.utils.Primitives.findByBoxed;
import static com.gwtplatform.dispatch.rest.processors.utils.Primitives.findByPrimitive;

@AutoService(SerializationProcessor.class)
public class JacksonSerializationProcessor extends AbstractContextProcessor<SerializationContext, Void>
        implements SerializationProcessor {
    private static final String TEMPLATE =
            "com/gwtplatform/dispatch/rest/processors/serialization/jackson/JacksonMapperProvider.vm";
    private static final ContentType APPLICATION_JSON = ContentType.valueOf(MediaType.APPLICATION_JSON);

    private final JacksonMapperProcessor mapperProcessor;
    private final Map<Type, MapperDefinition> mappers;
    private final Type impl;
    private final Type parent;

    private JavaFileObject sourceFile;
    private ContextProcessors contextProcessors;

    public JacksonSerializationProcessor() {
        this.mapperProcessor = new JacksonMapperProcessor();
        this.mappers = new TreeMap<>();
        this.parent = new Type(JacksonMapperProvider.class);
        this.impl = new Type(parent.getPackageName(), parent.getSimpleName() + "Impl");
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        mapperProcessor.init(processingEnv);

        contextProcessors = new ContextProcessors(processingEnv, logger);
        sourceFile = outputter.prepareSourceFile(impl);

        processBinding();
    }

    private void processBinding() {
        BindingContext context = new BindingContext(impl);
        context.setImplemented(parent);
        context.setScope(Singleton.class);

        Iterable<BindingsProcessor> processors = contextProcessors.getProcessors(BindingsProcessor.class, context);
        for (BindingsProcessor processor : processors) {
            processor.process(context);
        }

        if (isEmpty(processors)) {
            logger.mandatoryWarning("No binding policy found for serialization policy `%s`.", impl.getQualifiedName());
        }
    }

    @Override
    public boolean canProcess(SerializationContext context) {
        return context.getContentTypes().contains(APPLICATION_JSON);
    }

    @Override
    public Void process(SerializationContext context) {
        if (!mappers.containsKey(context.getType())) {
            MapperDefinition mapper = mapperProcessor.process(context);

            mappers.put(mapper.getMapped(), mapper);
            duplicateIfBoxedOrPrimitive(mapper);
        }

        return null;
    }

    private void duplicateIfBoxedOrPrimitive(MapperDefinition mapper) {
        String name = mapper.getKey().getQualifiedParameterizedName();
        Optional<Primitives> primitive = findByPrimitive(name);
        Optional<Primitives> boxed = findByBoxed(name);
        Type newKey = null;

        if (primitive.isPresent()) {
            newKey = new Type(primitive.get().getBoxedClass());
        } else if (boxed.isPresent()) {
            newKey = new Type(boxed.get().getPrimitive());
        }

        if (newKey != null) {
            mappers.put(newKey, new MapperDefinition(newKey, mapper.getMapped(), mapper.getImpl()));
        }
    }

    @Override
    public void processLast() {
        logger.debug("Generating Jackson serialization policy `%s`.", impl.getQualifiedName());

        outputter.withTemplateFile(TEMPLATE)
                .withParam("mappers", mappers.values())
                .writeTo(impl, sourceFile);
    }
}
