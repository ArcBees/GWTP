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
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.dispatch.rest.processors.serialization.SerializationContext;
import com.gwtplatform.processors.tools.utils.Primitives;

import static com.gwtplatform.processors.tools.utils.Primitives.findByPrimitive;

public class JacksonMapperProcessor extends AbstractContextProcessor<SerializationContext, JacksonMapper> {
    private static final Pattern SANITIZE_NAME_PATTERN = Pattern.compile("[^a-zA-Z0-9_]");
    private static final String PACKAGE = JacksonMapperProvider.class.getPackage().getName() + ".mappers";
    private static final String NAME_SUFFIX = "Mapper";
    private static final String TEMPLATE =
            "/com/gwtplatform/dispatch/rest/processors/serialization/jackson/JacksonMapper.vm";

    @Override
    public JacksonMapper process(SerializationContext context) {
        logger.debug("Generating Jackson mapper for `%s`.", context.getType().getQualifiedParameterizedName());

        JacksonMapper mapper = processMapper(context);

        outputter.withTemplateFile(TEMPLATE)
                .withParam("mapped", mapper.getMapped())
                .writeTo(mapper.getImpl());

        return mapper;
    }

    private JacksonMapper processMapper(SerializationContext context) {
        Type type = context.getType();
        Type mapped = ensureNotPrimitive(type);

        String name = SANITIZE_NAME_PATTERN.matcher(mapped.getQualifiedParameterizedName()).replaceAll("_");
        name += NAME_SUFFIX;

        return new JacksonMapper(type, mapped, new Type(PACKAGE, name));
    }

    private Type ensureNotPrimitive(Type type) {
        Optional<Primitives> primitive = findByPrimitive(type.getQualifiedParameterizedName());
        if (primitive.isPresent()) {
            return new Type(primitive.get().getBoxedClass());
        }

        return type;
    }
}
