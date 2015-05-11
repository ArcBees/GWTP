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

package com.gwtplatform.dispatch.rest.rebind.serialization;

import java.util.Set;

import com.google.gwt.core.ext.typeinfo.JType;
import com.gwtplatform.dispatch.rest.rebind.action.ActionContext;
import com.gwtplatform.dispatch.rest.shared.ContentType;

public class SerializationContext {
    private final ActionContext context;
    private final JType type;
    private final Set<ContentType> contentTypes;

    public SerializationContext(
            ActionContext context,
            JType type,
            Set<ContentType> contentTypes) {
        this.context = context;
        this.type = type;
        this.contentTypes = contentTypes;
    }

    public ActionContext getContext() {
        return context;
    }

    public JType getType() {
        return type;
    }

    public Set<ContentType> getContentTypes() {
        return contentTypes;
    }
}
