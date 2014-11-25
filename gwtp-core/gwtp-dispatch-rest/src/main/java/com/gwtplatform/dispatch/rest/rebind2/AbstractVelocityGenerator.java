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

package com.gwtplatform.dispatch.rest.rebind2;

import java.io.PrintWriter;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.google.common.collect.Maps;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.gwtplatform.dispatch.rest.rebind2.utils.ClassDefinition;
import com.gwtplatform.dispatch.rest.rebind2.utils.Logger;

public abstract class AbstractVelocityGenerator extends AbstractGenerator {
    private static final String ENCODING = "UTF-8";

    private final VelocityEngine velocityEngine;

    protected AbstractVelocityGenerator(
            Logger logger,
            GeneratorContext context,
            VelocityEngine velocityEngine) {
        super(logger, context);

        this.velocityEngine = velocityEngine;
    }

    protected PrintWriter tryCreate() throws UnableToCompleteException {
        return getContext().tryCreate(getLogger(), getPackageName(), getImplName());
    }

    protected void mergeTemplate(PrintWriter printWriter) throws UnableToCompleteException {
        VelocityContext velocityContext = new VelocityContext(createTemplateVariables());
        velocityContext.put("lf", "\n");
        velocityContext.put("impl", getImplName());
        velocityContext.put("package", getPackageName());

        boolean success = velocityEngine.mergeTemplate(getTemplate(), ENCODING, velocityContext, printWriter);

        if (!success) {
            getLogger().die("An error occured while generating '%s'. See previous entries for details.",
                    getClassDefinition());
        }
    }

    protected Map<String, Object> createTemplateVariables() {
        return Maps.newHashMap();
    }

    protected ClassDefinition getClassDefinition() {
        return new ClassDefinition(getPackageName(), getImplName());
    }

    protected abstract String getTemplate();

    protected abstract String getPackageName();

    protected abstract String getImplName();
}
