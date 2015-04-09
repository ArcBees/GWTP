/**
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

package com.gwtplatform.dispatch.rest.rebind.serialization;

import java.util.Collection;

import com.google.gwt.core.ext.typeinfo.JType;
import com.gwtplatform.dispatch.rest.rebind.utils.ClassDefinition;
import com.gwtplatform.dispatch.rest.shared.ContentType;

public class SerializationDefinition extends ClassDefinition {
    private final JType serializableType;
    private final Collection<ContentType> contentTypes;

    public SerializationDefinition(
            String packageName,
            String className,
            JType serializableType,
            Collection<ContentType> contentTypes) {
        super(packageName, className);

        this.serializableType = serializableType;
        this.contentTypes = contentTypes;
    }

    public JType getSerializableType() {
        return serializableType;
    }

    public Collection<ContentType> getContentTypes() {
        return contentTypes;
    }
}
