/**
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.dispatch.rebind;

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;

import com.google.common.base.Strings;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.PrintWriter;
import javax.inject.Provider;

public abstract class AbstractVelocityGenerator {
    protected static final String SUFFIX = "Impl";
    protected static final String SHARED_PACKAGE = "shared";
    protected static final String CLIENT_PACKAGE = "client";

    private final TypeOracle typeOracle;
    private final Logger logger;
    private final Provider<VelocityContext> velocityContextProvider;
    private final VelocityEngine velocityEngine;
    private final GeneratorUtil generatorUtil;

    protected AbstractVelocityGenerator(
            TypeOracle typeOracle,
            Logger logger,
            Provider<VelocityContext> velocityContextProvider,
            VelocityEngine velocityEngine,
            GeneratorUtil generatorUtil) {
        this.typeOracle = typeOracle;
        this.logger = logger;
        this.velocityContextProvider = velocityContextProvider;
        this.velocityEngine = velocityEngine;
        this.generatorUtil = generatorUtil;
    }

    protected GeneratorUtil getGeneratorUtil() {
        return generatorUtil;
    }

    protected Logger getLogger() {
        return logger;
    }

    protected TypeOracle getTypeOracle() {
        return typeOracle;
    }

    protected void mergeTemplate(PrintWriter printWriter, String velocityTemplate, String implName) throws Exception {
        VelocityContext velocityContext = velocityContextProvider.get();
        velocityContext.put("implName", implName);
        velocityContext.put("package", getPackage());

        populateVelocityContext(velocityContext);

        velocityEngine.mergeTemplate(velocityTemplate, "UTF-8", velocityContext, printWriter);
        generatorUtil.closeDefinition(printWriter);
    }

    protected abstract String getPackage();

    protected abstract void populateVelocityContext(VelocityContext velocityContext) throws UnableToCompleteException;

    protected String concatenatePath(String prefix, String suffix) {
        prefix = normalizePath(prefix);
        suffix = normalizePath(suffix);

        if (prefix.endsWith("/") && !suffix.isEmpty()) {
            suffix = suffix.substring(1);
        }

        return prefix + suffix;
    }

    protected String normalizePath(String path) {
        if (!path.isEmpty() && !path.startsWith("/")) {
            path = "/" + path;
        }

        return path;
    }

    private String createName(JClassType type, String name, String suffix) {
        String newName = name + suffix;
        String alternativeName = type.getSimpleSourceName() + suffix;

        newName = Strings.isNullOrEmpty(newName) ? alternativeName : newName;

        return newName;
    }
}
