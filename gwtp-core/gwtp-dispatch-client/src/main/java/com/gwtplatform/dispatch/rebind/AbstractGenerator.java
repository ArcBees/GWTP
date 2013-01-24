/**
 * Copyright 2011 ArcBees Inc.
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

import java.io.PrintWriter;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.ConfigurationProperty;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.PropertyOracle;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Base generator.
 */
public abstract class AbstractGenerator extends Generator {
    private GeneratorContext generatorContext;
    private TreeLogger treeLogger;
    private TypeOracle typeOracle;
    private JClassType typeClass;
    private PropertyOracle propertyOracle;

    private String packageName = "";
    private String className = "";

    public PropertyOracle getPropertyOracle() {
        return propertyOracle;
    }

    public void setPropertyOracle(PropertyOracle propertyOracle) {
        this.propertyOracle = propertyOracle;
    }

    public void setTreeLogger(TreeLogger treeLogger) {
        this.treeLogger = treeLogger;
    }

    public TreeLogger getTreeLogger() {
        return treeLogger;
    }

    public TypeOracle getTypeOracle() {
        return typeOracle;
    }

    public void setTypeOracle(TypeOracle typeOracle) {
        this.typeOracle = typeOracle;
    }

    public JClassType getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(JClassType typeName) {
        this.typeClass = typeName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    protected void setGeneratorContext(GeneratorContext generatorContext) {
        this.generatorContext = generatorContext;
    }

    protected GeneratorContext getGeneratorContext() {
        return generatorContext;
    }

    protected String getSimpleNameFromTypeName(String typeName) {
        return typeName.substring(typeName.lastIndexOf(".") + 1);
    }

    protected PrintWriter tryCreatePrintWriter(String prefix, String suffix) throws UnableToCompleteException {
        return tryCreatePrintWriter(getTypeClass().getPackage().getName(), prefix, suffix);
    }

    protected PrintWriter tryCreatePrintWriter(String packageName, String prefix, String suffix)
            throws UnableToCompleteException {
        setPackageName(packageName);
        setClassName(prefix + getTypeClass().getName() + suffix);

        return generatorContext.tryCreate(getTreeLogger(), getPackageName(), getClassName());
    }

    protected JClassType getType(String typeName) throws UnableToCompleteException {
        try {
            return getTypeOracle().getType(typeName);
        } catch (NotFoundException e) {
            getTreeLogger().log(TreeLogger.ERROR, "Cannot find " + typeName, e);
            throw new UnableToCompleteException();
        }
    }

    protected ConfigurationProperty findConfigurationProperty(String prop) throws UnableToCompleteException {
        try {
            return getPropertyOracle().getConfigurationProperty(prop);
        } catch (BadPropertyValueException e) {
            getTreeLogger().log(TreeLogger.ERROR, "Cannot find " + prop + " property in your module.gwt.xml file.", e);
            throw new UnableToCompleteException();
        }
    }

    protected void closeDefinition(SourceWriter sourceWriter) {
        sourceWriter.commit(getTreeLogger());
    }

    /**
     * Import the given JType in the composer. Also includes generic parameters recursively.
     */
    protected void addImports(ClassSourceFileComposerFactory composer, JType type) {
        if (type.isWildcard() != null) {
            return;
        }

        composer.addImport(type.getQualifiedSourceName());

        JParameterizedType parameterizedType = type.isParameterized();
        if (parameterizedType != null) {
            for (JClassType typeArg : parameterizedType.getTypeArgs()) {
                addImports(composer, typeArg);
            }
        }
    }

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
}
