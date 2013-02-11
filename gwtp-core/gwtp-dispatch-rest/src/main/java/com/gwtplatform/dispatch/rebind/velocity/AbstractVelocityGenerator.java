/*
 * Copyright (c) 2012 by Zafin Labs, All rights reserved.
 * This source code, and resulting software, is the confidential and proprietary information
 * ("Proprietary Information") and is the intellectual property ("Intellectual Property")
 * of Zafin Labs ("The Company"). You shall not disclose such Proprietary Information and
 * shall use it only in accordance with the terms and conditions of any and all license
 * agreements you have entered into with The Company.
 */

package com.gwtplatform.dispatch.rebind.velocity;

import com.google.common.base.Strings;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.gwtplatform.dispatch.shared.rest.RestAction;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.inject.Provider;
import java.io.PrintWriter;

public abstract class AbstractVelocityGenerator {
    protected static final String SUFFIX = "Impl";
    protected static final String PACKAGE = RestAction.class.getPackage().getName();

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

    protected void mergeTemplate(PrintWriter printWriter, String velocityTemplate, JClassType descriptor,
            String implName) throws Exception {
        VelocityContext velocityContext = velocityContextProvider.get();
        velocityContext.put("implName", implName);
        velocityContext.put("package", PACKAGE);

        populateVelocityContext(velocityContext, descriptor);

        velocityEngine.mergeTemplate(velocityTemplate, "UTF-8", velocityContext, printWriter);
        generatorUtil.closeDefinition(printWriter);
    }

    protected abstract void populateVelocityContext(VelocityContext velocityContext,
            JClassType descriptor) throws UnableToCompleteException;

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
