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

import javax.tools.FileObject;
import javax.ws.rs.core.MediaType;

import com.google.auto.service.AutoService;
import com.google.common.base.Optional;
import com.gwtplatform.dispatch.rest.client.serialization.JacksonMapperProvider;
import com.gwtplatform.dispatch.rest.processors.DispatchRestContextProcessor;
import com.gwtplatform.dispatch.rest.processors.NameUtils;
import com.gwtplatform.dispatch.rest.processors.serialization.SerializationContext;
import com.gwtplatform.dispatch.rest.processors.serialization.SerializationProcessor;
import com.gwtplatform.dispatch.rest.shared.ContentType;
import com.gwtplatform.processors.tools.bindings.BindingContext;
import com.gwtplatform.processors.tools.bindings.BindingsProcessors;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.outputter.Outputter;
import com.gwtplatform.processors.tools.utils.Primitives;
import com.gwtplatform.processors.tools.utils.Utils;

import jakarta.inject.Singleton;

import static com.gwtplatform.dispatch.rest.processors.NameUtils.findRestModuleType;
import static com.gwtplatform.processors.tools.bindings.BindingContext.newSetBinder;
import static com.gwtplatform.processors.tools.utils.Primitives.findByBoxed;
import static com.gwtplatform.processors.tools.utils.Primitives.findByPrimitive;

@AutoService(SerializationProcessor.class)
public class JacksonSerializationProcessor extends DispatchRestContextProcessor<SerializationContext, Void>
        implements SerializationProcessor {
    private static final String TEMPLATE =
            "com/gwtplatform/dispatch/rest/processors/serialization/jackson/JacksonMapperProvider.vm";
    private static final ContentType APPLICATION_JSON = ContentType.valueOf(MediaType.APPLICATION_JSON);

    private final JacksonMapperProcessor mapperProcessor;
    private final Map<Type, JacksonMapper> mappers;
    private final Type parent;

    private Type impl;
    private FileObject sourceFile;

    public JacksonSerializationProcessor() {
        this.mapperProcessor = new JacksonMapperProcessor();
        this.mappers = new TreeMap<>();
        this.parent = new Type(JacksonMapperProvider.class);
    }

    @Override
    public synchronized void init(Logger logger, Utils utils, Outputter outputter) {
        super.init(logger, utils, outputter);

        mapperProcessor.init(logger, utils, outputter);
        impl = NameUtils.findJacksonMapperProviderType(utils);
        sourceFile = outputter.prepareSourceFile(impl);

        BindingContext context = newSetBinder(findRestModuleType(utils), parent, impl, Singleton.class);
        new BindingsProcessors(logger, utils, outputter).process(context);
    }

    @Override
    public boolean canProcess(SerializationContext context) {
        return context.getContentTypes().contains(APPLICATION_JSON);
    }

    @Override
    public Void process(SerializationContext context) {
        if (!mappers.containsKey(context.getType())) {
            JacksonMapper mapper = mapperProcessor.process(context);

            mappers.put(mapper.getMapped(), mapper);
            duplicateIfBoxedOrPrimitive(mapper);
        }

        return null;
    }

    private void duplicateIfBoxedOrPrimitive(JacksonMapper mapper) {
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
            mappers.put(newKey, new JacksonMapper(newKey, mapper.getMapped(), mapper.getType()));
        }
    }

    @Override
    public void processLast() {
        logger.debug("Generating Jackson serialization policy `%s`.", impl.getQualifiedName());

        outputter.configure(TEMPLATE)
                .withParam("mappers", mappers.values())
                .writeTo(impl, sourceFile);
    }
}
