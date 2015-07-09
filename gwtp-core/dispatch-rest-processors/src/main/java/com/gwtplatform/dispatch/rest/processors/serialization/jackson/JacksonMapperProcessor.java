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

import java.util.regex.Pattern;

import com.google.common.base.Optional;
import com.gwtplatform.dispatch.rest.client.serialization.JacksonMapperProvider;
import com.gwtplatform.dispatch.rest.processors.AbstractContextProcessor;
import com.gwtplatform.dispatch.rest.processors.definitions.TypeDefinition;
import com.gwtplatform.dispatch.rest.processors.serialization.SerializationContext;
import com.gwtplatform.dispatch.rest.processors.utils.Primitives;

import static com.gwtplatform.dispatch.rest.processors.utils.Primitives.findByPrimitive;

public class JacksonMapperProcessor extends AbstractContextProcessor<SerializationContext, MapperDefinition> {
    private static final Pattern SANITIZE_NAME_PATTERN = Pattern.compile("[^a-zA-Z0-9_]");
    private static final String PACKAGE = JacksonMapperProvider.class.getPackage().getName() + ".mappers";
    private static final String NAME_SUFFIX = "Mapper";
    private static final String TEMPLATE =
            "/com/gwtplatform/dispatch/rest/processors/serialization/jackson/JacksonMapper.vm";

    @Override
    public boolean canProcess(SerializationContext context) {
        return true;
    }

    @Override
    public MapperDefinition process(SerializationContext context) {
        logger.debug("Generating Jackson mapper for `%s`.", context.getType().getQualifiedParameterizedName());

        MapperDefinition mapper = processMapper(context);

        outputter.withTemplateFile(TEMPLATE)
                .withParam("mapped", mapper.getMapped())
                .writeTo(mapper.getImpl());

        return mapper;
    }

    private MapperDefinition processMapper(SerializationContext context) {
        TypeDefinition type = context.getType();
        TypeDefinition mapped = ensureNotPrimitive(type);

        String name = SANITIZE_NAME_PATTERN.matcher(mapped.getQualifiedParameterizedName()).replaceAll("_");
        name += NAME_SUFFIX;

        return new MapperDefinition(type, mapped, new TypeDefinition(PACKAGE, name));
    }

    private TypeDefinition ensureNotPrimitive(TypeDefinition type) {
        Optional<Primitives> primitive = findByPrimitive(type.getQualifiedParameterizedName());
        if (primitive.isPresent()) {
            return new TypeDefinition(primitive.get().getBoxedClass());
        }

        return type;
    }
}
