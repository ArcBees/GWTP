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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.gwtplatform.processors.tools.domain.HasImports;
import com.gwtplatform.processors.tools.domain.HasType;
import com.gwtplatform.processors.tools.domain.Type;

public class JacksonMapper implements HasType, HasImports {
    private final Type key;
    private final Type mapped;
    private final Type type;

    public JacksonMapper(
            Type key,
            Type mapped,
            Type type) {
        this.key = key;
        this.mapped = mapped;
        this.type = type;
    }

    public Type getKey() {
        return key;
    }

    public Type getMapped() {
        return mapped;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Collection<String> getImports() {
        List<String> imports = new ArrayList<>(mapped.getImports());
        imports.addAll(type.getImports());

        return imports;
    }
}
