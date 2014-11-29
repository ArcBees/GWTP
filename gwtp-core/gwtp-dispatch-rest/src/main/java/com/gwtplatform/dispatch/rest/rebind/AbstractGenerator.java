/**
 * Copyright 2014 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.rebind;

import java.io.PrintWriter;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.gwtplatform.dispatch.rest.rebind.utils.Logger;

public abstract class AbstractGenerator implements HasPriority {
    protected static final String IMPL = "Impl";

    private final Logger logger;
    private final GeneratorContext context;

    protected AbstractGenerator(
            Logger logger,
            GeneratorContext context) {
        this.logger = logger;
        this.context = context;
    }

    @Override
    public byte getPriority() {
        return DEFAULT_PRIORITY;
    }

    protected Logger getLogger() {
        return logger;
    }

    protected GeneratorContext getContext() {
        return context;
    }

    private TypeOracle getTypeOracle() {
        return context.getTypeOracle();
    }

    protected JClassType findType(Class<?> clazz) {
        return findType(clazz.getName());
    }

    protected JClassType findType(String typeName) {
        return getTypeOracle().findType(typeName);
    }

    protected JClassType getType(Class<?> clazz) throws UnableToCompleteException {
        return getType(clazz.getName());
    }

    protected JClassType getType(String typeName) throws UnableToCompleteException {
        JClassType type = null;
        try {
            type = getTypeOracle().getType(typeName);
        } catch (NotFoundException e) {
            getLogger().die("Cannot get type '%s'.", typeName);
        }

        return type;
    }

    protected PrintWriter tryCreate(String packageName, String className) {
        return getContext().tryCreate(getLogger(), packageName, className);
    }

    protected void commit(PrintWriter printWriter) {
        getContext().commit(getLogger(), printWriter);
    }
}
