/*
 * Copyright 2016 ArcBees Inc.
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

package com.gwtplatform.mvp.processors.proxy;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor8;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.gwt.inject.client.AsyncProvider;
import com.gwtplatform.common.client.IndirectProvider;
import com.gwtplatform.mvp.client.annotations.CustomProvider;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplitBundle;
import com.gwtplatform.mvp.client.annotations.ProxyEvent;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.presenter.slots.NestedSlot;
import com.gwtplatform.mvp.processors.bundle.BundleDetails;
import com.gwtplatform.processors.tools.domain.HasImports;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;
import com.gwtplatform.processors.tools.logger.LogBuilder;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.utils.Utils;

import jakarta.inject.Provider;

import static java.util.Arrays.asList;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.util.ElementFilter.constructorsIn;
import static javax.lang.model.util.ElementFilter.fieldsIn;
import static javax.lang.model.util.ElementFilter.methodsIn;
import static com.google.auto.common.AnnotationMirrors.getAnnotationValue;
import static com.google.auto.common.MoreElements.asType;
import static com.google.auto.common.MoreElements.getAnnotationMirror;
import static com.google.auto.common.MoreElements.hasModifiers;
import static com.google.auto.common.MoreElements.isAnnotationPresent;
import static com.google.auto.common.MoreTypes.asDeclared;
import static com.google.auto.common.MoreTypes.asElement;
import static com.google.auto.common.MoreTypes.asTypeElement;
import static com.google.auto.common.MoreTypes.isTypeOf;
import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static com.google.common.collect.FluentIterable.from;

public abstract class AbstractProxyDetails implements ProxyDetails {
    private static final List<Class<? extends Annotation>> SUPPORTED_ANNOTATIONS =
            asList(ProxyStandard.class, ProxyCodeSplit.class, ProxyCodeSplitBundle.class);

    protected final TypeElement element;
    protected final Logger logger;
    protected final Utils utils;
    protected final TypeMirror proxyMirror;

    private Type type;
    private TypeMirror presenterMirror;
    private Set<String> contentSlots;
    private Optional<BundleDetails> bundleDetails;
    private List<ProxyEventMethod> proxyEventMethods;
    private Optional<Type> customProvider;

    protected AbstractProxyDetails(
            Logger logger,
            Utils utils,
            TypeElement element,
            TypeMirror proxyMirror) {
        this.logger = logger;
        this.utils = utils;
        this.element = element;
        this.proxyMirror = proxyMirror;

        warnIfMultipleAnnotations();
    }

    private void warnIfMultipleAnnotations() {
        int count = 0;
        for (Class<? extends Annotation> classy : SUPPORTED_ANNOTATIONS) {
            if (isAnnotationPresent(element, classy)) {
                ++count;
            }
        }

        if (count > 1) {
            logger.warning().context(element)
                    .log("Multiple proxy annotation detected. Review and make sure only one is present.");
        }
    }

    @Override
    public Type getType() {
        if (type == null) {
            String packageName = new Type(asType(element)).getPackageName();
            String presenterName = getPresenterType().getSimpleName();
            String proxyName = element.getSimpleName().toString();

            type = new Type(packageName, String.format("%s$$%s", presenterName, proxyName));
        }

        return type;
    }

    @Override
    public Type getProxyType() {
        return new Type(element.asType());
    }

    @Override
    public Type getPresenterType() {
        return new Type(getPresenterMirror());
    }

    private TypeMirror getPresenterMirror() {
        if (presenterMirror == null) {
            List<? extends TypeMirror> proxyGenericTypes = asDeclared(proxyMirror).getTypeArguments();
            if (proxyGenericTypes.isEmpty()) {
                logger.error().context(element).log("This proxy must specify its presenter.");
                throw new UnableToProcessException();
            }

            presenterMirror = proxyGenericTypes.get(0);
        }

        return presenterMirror;
    }

    @Override
    public Set<String> getContentSlots() {
        if (contentSlots == null) {
            extractContentSlots();
        }

        return contentSlots;
    }

    private void extractContentSlots() {
        TypeElement presenterElement = asTypeElement(getPresenterMirror());
        List<Element> presenterMembers = utils.getAllMembers(presenterElement);
        List<VariableElement> presenterFields = fieldsIn(presenterMembers);
        contentSlots = new HashSet<>();

        for (VariableElement field : presenterFields) {
            if (isValidContentSlot(field)) {
                contentSlots.add(field.getSimpleName().toString());
            }
        }
    }

    private boolean isValidContentSlot(VariableElement field) {
        if (isTypeOf(NestedSlot.class, field.asType())) {
            if (!hasModifiers(PRIVATE, STATIC).apply(field)) {
                return true;
            }

            logger.mandatoryWarning()
                    .context(field)
                    .log("Content slots cannot be static or private.");
        }

        return false;
    }

    @Override
    public boolean isCodeSplit() {
        return element.getAnnotation(ProxyCodeSplit.class) != null;
    }

    @Override
    public List<ProxyEventMethod> getProxyEventMethods() {
        if (proxyEventMethods == null) {
            TypeElement presenterElement = asTypeElement(getPresenterMirror());
            List<ExecutableElement> methods = methodsIn(utils.getElements().getAllMembers(presenterElement));

            proxyEventMethods = FluentIterable.from(methods)
                    .filter(method -> method.getAnnotation(ProxyEvent.class) != null)
                    .transform(element1 -> new ProxyEventMethod(logger, utils, element1))
                    .toList();

            ensureNoHandlerMethodClashes();
        }

        return proxyEventMethods;
    }

    private void ensureNoHandlerMethodClashes() {
        List<String> handlerMethodNames = FluentIterable.from(proxyEventMethods)
                .transform(ProxyEventMethod::getHandlerMethodName)
                .toList();
        Set<String> uniqueHandlerMethodNames = new HashSet<>(handlerMethodNames);

        if (handlerMethodNames.size() != uniqueHandlerMethodNames.size()) {
            // TODO: Probably not worth it, but with some gymnastic we could print exactly which methods.
            logger.error()
                    .context(asElement(getPresenterMirror()))
                    .log("Presenter contains multiple @ProxyEvents with handlers that have clashing method names.");
            throw new UnableToProcessException();
        }
    }

    @Override
    public BundleDetails getBundleDetails() {
        if (bundleDetails == null) {
            extractBundleDetails();
        }

        return bundleDetails.orNull();
    }

    private void extractBundleDetails() {
        Optional<AnnotationMirror> annotation = getAnnotationMirror(element, ProxyCodeSplitBundle.class);
        bundleDetails = absent();

        if (annotation.isPresent()) {
            bundleDetails = of(new BundleDetails(logger, utils, getPresenterType(), element, annotation.get()));
        }
    }

    @Override
    public Type getCustomProvider() {
        if (customProvider == null) {
            customProvider = extractCustomProvider();
        }

        return customProvider.orNull();
    }

    private Optional<Type> extractCustomProvider() {
        Optional<AnnotationMirror> annotationMirror = getAnnotationMirror(element, CustomProvider.class);

        if (annotationMirror.isPresent()) {
            AnnotationValue value = getAnnotationValue(annotationMirror.get(), "value");
            DeclaredType valueType = value.accept(new SimpleAnnotationValueVisitor8<DeclaredType, Void>() {
                @Override
                public DeclaredType visitType(TypeMirror typeMirror, Void ignored) {
                    return asDeclared(typeMirror);
                }
            }, null);
            LogBuilder errorBuilder = logger.error().context(element, annotationMirror.get(), value);

            validateCustomProviderType(errorBuilder, valueType);
            validateCustomProviderConstructor(errorBuilder, valueType);

            return of(new Type(valueType));
        }

        return absent();
    }

    private void validateCustomProviderType(LogBuilder errorBuilder, DeclaredType type) {
        TypeElement customProviderElement = asTypeElement(type);
        TypeMirror expectedSuperType = utils.createWithTypeArguments(IndirectProvider.class, getPresenterMirror());

        if (customProviderElement.getKind() != CLASS
                || !hasModifiers(PUBLIC).apply(customProviderElement)
                || hasModifiers(ABSTRACT).apply(customProviderElement)
                || !utils.getTypes().isSubtype(type, expectedSuperType)) {
            errorBuilder.log("Element passed to @CustomProvider must be a public, non-abstract class "
                    + "and implement `%s`.", new Type(expectedSuperType).getParameterizedName());
            throw new UnableToProcessException();
        }
    }

    private void validateCustomProviderConstructor(LogBuilder errorBuilder, DeclaredType type) {
        TypeMirror expectedParameterMirror = utils.createWithTypeArguments(
                getBundleDetails() != null || isCodeSplit() ? AsyncProvider.class : Provider.class,
                getPresenterMirror());
        List<ExecutableElement> constructors = constructorsIn(asTypeElement(type).getEnclosedElements());

        for (ExecutableElement constructor : constructors) {
            List<? extends VariableElement> parameters = constructor.getParameters();
            if (hasModifiers(PUBLIC).apply(constructor)
                    && parameters.size() == 1
                    && utils.getTypes().isAssignable(expectedParameterMirror, parameters.get(0).asType())) {
                return;
            }
        }

        errorBuilder.log("Class passed to @CustomProvider must have a public constructor with a single parameter of "
                + "type `%s`.", new Type(expectedParameterMirror).getParameterizedName());
        throw new UnableToProcessException();
    }

    @Override
    public Collection<String> getImports() {
        FluentIterable<String> imports =
                from(getProxyEventMethods())
                        .transformAndConcat(HasImports.EXTRACT_IMPORTS_FUNCTION)
                        .append(getProxyType().getImports())
                        .append(getPresenterType().getImports());

        if (getBundleDetails() != null) {
            imports = imports.append(getBundleDetails().getImports());
        }
        if (getCustomProvider() != null) {
            imports = imports.append(getCustomProvider().getImports());
        }

        return imports.toList();
    }
}
