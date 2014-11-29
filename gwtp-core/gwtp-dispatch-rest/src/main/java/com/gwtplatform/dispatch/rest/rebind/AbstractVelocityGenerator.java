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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.google.common.collect.Maps;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.gwtplatform.dispatch.rest.rebind.utils.ClassDefinition;
import com.gwtplatform.dispatch.rest.rebind.utils.Logger;

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

    protected String mergeTemplate() throws UnableToCompleteException {
        StringWriter writer = new StringWriter();

        mergeTemplate(writer);

        return writer.toString();
    }

    protected void mergeTemplate(Writer writer) throws UnableToCompleteException {
        Map<String, Object> variables = Maps.newHashMap();
        populateTemplateVariables(variables);

        VelocityContext velocityContext = new VelocityContext(variables);
        velocityContext.put("lf", "\n");
        velocityContext.put("impl", getImplName());
        velocityContext.put("package", getPackageName());

        boolean success = writeAndClose(writer, velocityContext);
        if (!success) {
            getLogger().die("An error occurred while generating '%s'. See previous entries for details.",
                    getClassDefinition());
        }
    }

    protected void populateTemplateVariables(Map<String, Object> variables) {
    }

    protected ClassDefinition getClassDefinition() {
        return new ClassDefinition(getPackageName(), getImplName());
    }

    protected abstract String getTemplate();

    protected abstract String getPackageName();

    protected abstract String getImplName();

    private boolean writeAndClose(Writer writer, VelocityContext velocityContext) {
        try {
            return velocityEngine.mergeTemplate(getTemplate(), ENCODING, velocityContext, writer);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                getLogger().log(Type.ERROR, "Error while closing the print writer.", e);
            }
        }
    }
}
