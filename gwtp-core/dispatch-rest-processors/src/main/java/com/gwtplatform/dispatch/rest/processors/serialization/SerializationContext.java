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

package com.gwtplatform.dispatch.rest.processors.serialization;

import java.util.Set;

import com.google.common.base.MoreObjects;
import com.gwtplatform.dispatch.rest.processors.definitions.TypeDefinition;
import com.gwtplatform.dispatch.rest.shared.ContentType;

public class SerializationContext {
    public enum IO {
        READ,
        WRITE,
        BOTH
    }

    private final TypeDefinition type;
    private final Set<ContentType> contentTypes;
    private final IO io;

    public SerializationContext(
            TypeDefinition type,
            Set<ContentType> contentTypes,
            IO io) {
        this.type = type;
        this.contentTypes = contentTypes;
        this.io = io;
    }

    public TypeDefinition getType() {
        return type;
    }

    public Set<ContentType> getContentTypes() {
        return contentTypes;
    }

    public IO getIo() {
        return io;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", type)
                .add("contentTypes", contentTypes)
                .add("io", io)
                .toString();
    }
}
