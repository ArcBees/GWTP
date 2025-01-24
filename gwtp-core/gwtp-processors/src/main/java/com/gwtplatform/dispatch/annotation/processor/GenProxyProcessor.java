/*
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

package com.gwtplatform.dispatch.annotation.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

import com.google.web.bindery.requestfactory.shared.Locator;
import com.gwtplatform.dispatch.annotation.GenProxy;
import com.gwtplatform.dispatch.annotation.UseProxy;
import com.gwtplatform.dispatch.annotation.UseProxyName;
import com.gwtplatform.dispatch.annotation.helper.InterfaceGenerationHelper;
import com.gwtplatform.dispatch.annotation.helper.ReflectionHelper;

/**
 * Processes {@link GenProxy} annotations.
 * <p>
 * {@link GenProxyProcessor} should only ever be called by tool infrastructure.
 * See {@link javax.annotation.processing.Processor} for more details.
 */
@SupportedAnnotationTypes("com.gwtplatform.dispatch.annotation.GenProxy")
public class GenProxyProcessor extends GenProcessor {
    @Override
    public void process(final Element proxyElement) {
        GenProxy genProxy = proxyElement.getAnnotation(GenProxy.class);
        generateProxy(
                proxyElement,
                genProxy.targetPackage(),
                genProxy.filterSetter(),
                genProxy.filterGetter(),
                genProxy.isEmbeddedType(),
                getLocatorTypeMirror(genProxy)
        );
    }

    protected void generateProxy(Element proxyElement, String targetPackage, String[] filterSetter,
            String[] filterGetter, boolean isEmbeddedType, TypeMirror locatorType) {
        InterfaceGenerationHelper writer = null;
        try {
            ReflectionHelper reflection = new ReflectionHelper(getEnvironment(), (TypeElement) proxyElement);
            String proxyElementSimpleName = reflection.getSimpleClassName();
            String proxyElementClassName = reflection.getClassName();
            String proxyElementPackage = reflection.getPackageName();

            // Magic: By default proxies should be available for client and server side.
            String preparedProxyElementClassName = proxyElementClassName.replace(".server", ".shared");
            String preparedProxyElementPackage = proxyElementPackage.replace(".server", ".shared");

            if (targetPackage != null && !targetPackage.isEmpty()) {
                // Prepare user defined proxy target package and do not replace server with shared.
                preparedProxyElementClassName = targetPackage + "." + proxyElementSimpleName;
                preparedProxyElementPackage = targetPackage;
            }

            String proxySimpleName = proxyElementSimpleName + "Proxy";
            String proxyClassName = preparedProxyElementClassName + "Proxy";

            @SuppressWarnings("resource")
            Writer sourceWriter = getEnvironment().getFiler().createSourceFile(proxyClassName,
                    proxyElement).openWriter();
            writer = new InterfaceGenerationHelper(sourceWriter);
            writer.generatePackageDeclaration(preparedProxyElementPackage);

            if (isEmbeddedType) {
                generateValueProxyHeader(writer, reflection, proxyElementClassName, proxySimpleName);
            } else {
                generateEntityProxyHeader(writer, reflection, proxyElementClassName, proxySimpleName, locatorType);
            }

            writer.println();

            Collection<VariableElement> allFields = reflection.getNonConstantFields();

            // Generate getters.
            Collection<VariableElement> getterFields = reflection.filterFields(allFields, filterGetter);
            for (VariableElement getterField : getterFields) {
                generateGetter(writer, getterField);
            }

            // Generate setters.
            Collection<VariableElement> setterFields = reflection.filterFields(allFields, filterSetter);
            for (VariableElement setterField : setterFields) {
                generateSetter(writer, setterField);
            }

            writer.println();
            writer.generateFooter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    protected void generateEntityProxyHeader(InterfaceGenerationHelper writer, ReflectionHelper reflection,
            String proxyElementClassName, String proxySimpleName, TypeMirror locatorType) {
        writer.generateImports(
                "com.google.web.bindery.requestfactory.shared.ProxyFor",
                "com.google.web.bindery.requestfactory.shared.EntityProxy",
                "com.google.web.bindery.requestfactory.shared.EntityProxyId"
        );
        writer.println();
        // Check for locator.
        if (locatorType == null || Locator.class.getName().equals(locatorType.toString())) {
            writer.generateAnnotation("ProxyFor", proxyElementClassName + ".class");
        } else {
            writer.println("@{0}(value = {1}.class, locator = {2}.class)",
                    "ProxyFor", proxyElementClassName,
                    locatorType.toString());
        }
        writer.generateInterfaceHeader(proxySimpleName, reflection.getClassRepresenter().getModifiers(),
                "EntityProxy"
        );
        writer.println();
        writer.generateEmptyMethodBody("stableId", "EntityProxyId<" + proxySimpleName + ">");
    }

    protected void generateValueProxyHeader(InterfaceGenerationHelper writer, ReflectionHelper reflection,
            String proxyElementClassName, String proxySimpleName) {
        writer.generateImports(
                "com.google.web.bindery.requestfactory.shared.ProxyFor",
                "com.google.web.bindery.requestfactory.shared.ValueProxy"
        );
        writer.println();
        writer.generateAnnotation("ProxyFor", proxyElementClassName + ".class");
        writer.generateInterfaceHeader(proxySimpleName, reflection.getClassRepresenter().getModifiers(), "ValueProxy");
    }

    protected void generateGetter(InterfaceGenerationHelper writer, VariableElement getterField) {
        // Check for embedded types.
        UseProxy useProxyAnnotation = getterField.getAnnotation(UseProxy.class);
        UseProxyName useProxyNameAnnotation = getterField.getAnnotation(UseProxyName.class);
        if (useProxyAnnotation != null) {
            writer.generateGetter(getterField.getSimpleName().toString(), getProxyTypeMirrorName(useProxyAnnotation,
                    getterField.asType()));
        } else if (useProxyNameAnnotation != null) {
            writer.generateGetter(getterField.getSimpleName().toString(), useProxyNameAnnotation.value());
        } else {
            writer.generateGetter(getterField);
        }
    }

    protected void generateSetter(InterfaceGenerationHelper writer, VariableElement setterField) {
        // Check for embedded types.
        UseProxy useProxyAnnotation = setterField.getAnnotation(UseProxy.class);
        UseProxyName useProxyNameAnnotation = setterField.getAnnotation(UseProxyName.class);
        if (useProxyAnnotation != null) {
            writer.generateSetter(setterField.getSimpleName().toString(), getProxyTypeMirrorName(useProxyAnnotation,
                    setterField.asType()));
        } else if (useProxyNameAnnotation != null) {
            writer.generateSetter(setterField.getSimpleName().toString(), useProxyNameAnnotation.value());
        } else {
            writer.generateSetter(setterField);
        }
    }

    /**
     * Workaround for MirroredTypeException (Attempt to access Class object for TypeMirror).
     *
     * @see <a href="http://goo.gl/7ee2R">Getting class values from annotations in an annotationprocessor</a>
     */
    protected final String getProxyTypeMirrorName(UseProxy useProxyAnnotation, TypeMirror originalTypeMirror) {
        TypeMirror mirror = null;
        try {
            useProxyAnnotation.value();
        } catch (MirroredTypeException e) {
            mirror = e.getTypeMirror();
        }

        return nestIntoCollectionIfNecessary(mirror, originalTypeMirror);
    }

    private String nestIntoCollectionIfNecessary(TypeMirror mirror, TypeMirror originalTypeMirror) {
        String originalTypeDeclaration = originalTypeMirror.toString();

        // Shortcut if no generics can be found in the type declaration
        if (!(originalTypeDeclaration.contains("<") && originalTypeDeclaration.contains(">"))) {
            return mirror.toString();
        }

        String collectionClassName = originalTypeDeclaration.substring(0, originalTypeDeclaration.indexOf("<"));
        Class collectionClass = tryRetrieveCollectionClass(collectionClassName);

        StringBuilder builder = new StringBuilder();
        if (collectionClass != null && (isAssignableToSet(collectionClass) || isAssignableToList(collectionClass))) {
            builder.append(collectionClassName).append("<").append(mirror.toString()).append(">");
        } else {
            builder.append(mirror.toString());
        }
        return builder.toString();
    }

    private Class tryRetrieveCollectionClass(String collectionClassName) {
        try {
            return Class.forName(collectionClassName);
        } catch (ClassNotFoundException e) {
            printMessage("Potential collection class " + collectionClassName + " could not be found.");
        }
        return null;
    }

    private boolean isAssignableToSet(Class collectionClass) {
        if (collectionClass.equals(Set.class)) {
            return true;
        }
        for (Class interfaceClass : collectionClass.getInterfaces()) {
            if (interfaceClass.equals(Set.class)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAssignableToList(Class collectionClass) {
        if (collectionClass.equals(List.class)) {
            return true;
        }
        for (Class interfaceClass : collectionClass.getInterfaces()) {
            if (interfaceClass.equals(List.class)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see #getProxyTypeMirrorName(com.gwtplatform.dispatch.annotation.UseProxy, javax.lang.model.type.TypeMirror)
     */
    protected final TypeMirror getLocatorTypeMirror(GenProxy genProxyAnnotation) {
        TypeMirror mirror = null;
        try {
            genProxyAnnotation.locator();
        } catch (MirroredTypeException e) {
            mirror = e.getTypeMirror();
        }
        return mirror;
    }
}
