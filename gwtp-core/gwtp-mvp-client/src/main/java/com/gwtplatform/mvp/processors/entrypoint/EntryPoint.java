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

package com.gwtplatform.mvp.processors.entrypoint;

import java.lang.annotation.Annotation;
import java.util.Collection;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor8;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.gwtplatform.mvp.client.DefaultBootstrapper;
import com.gwtplatform.mvp.client.annotations.UseBootstrapper;
import com.gwtplatform.mvp.client.annotations.UsePreBootstrapper;
import com.gwtplatform.processors.tools.domain.HasImports;
import com.gwtplatform.processors.tools.domain.HasType;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;
import com.gwtplatform.processors.tools.logger.Logger;

import static com.google.auto.common.AnnotationMirrors.getAnnotationValue;
import static com.google.auto.common.MoreElements.getAnnotationMirror;
import static com.google.auto.common.MoreTypes.asDeclared;
import static com.google.auto.common.MoreTypes.asTypeElement;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Predicates.or;
import static com.gwtplatform.processors.tools.utils.ElementPredicates.IS_ACCESSIBLE_CLASS;
import static com.gwtplatform.processors.tools.utils.ElementPredicates.IS_DEFAULT_CONSTRUCTOR;
import static com.gwtplatform.processors.tools.utils.ElementPredicates.IS_INJECTABLE_CONSTRUCTOR;
import static com.gwtplatform.processors.tools.utils.ElementPredicates.hasMatchingConstructorOrNone;

public class EntryPoint implements HasType, HasImports {
    private static final Type DEFAULT_BOOTSTRAPPER_TYPE = new Type(DefaultBootstrapper.class);
    private static final Type TYPE = new Type("com.gwtplatform.mvp.client", "GeneratedMvpEntryPoint");

    private final Logger logger;
    private final TypeElement element;

    private Type bootstrapper;
    private Optional<Type> preBootstrapper;

    public EntryPoint(
            Logger logger,
            TypeElement element) {
        this.logger = logger;
        this.element = element;
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    public Type getBootstrapper() {
        if (bootstrapper == null) {
            bootstrapper = extractBootstrapperType();
        }

        return bootstrapper;
    }

    private Type extractBootstrapperType() {
        DeclaredType bootstrapperMirror = getTypeMirrorFromAnnotation(UseBootstrapper.class);

        if (bootstrapperMirror != null) {
            validateBootstrapper(bootstrapperMirror);
            return new Type(bootstrapperMirror);
        }

        return DEFAULT_BOOTSTRAPPER_TYPE;
    }

    private void validateBootstrapper(DeclaredType bootstrapper) {
        TypeElement typeElement = asTypeElement(bootstrapper);

        if (!IS_ACCESSIBLE_CLASS.apply(typeElement)) {
            logger.error().context(typeElement)
                    .log("Declared bootstrapper must be a public, non-abstract class.");
            throw new UnableToProcessException();
        }
        if (!hasMatchingConstructorOrNone(or(IS_DEFAULT_CONSTRUCTOR, IS_INJECTABLE_CONSTRUCTOR)).apply(typeElement)) {
            logger.error().context(typeElement)
                    .log("Declared bootstrapper must have a public, no-arg, or @Inject constructor.");
            throw new UnableToProcessException();
        }
    }

    public Type getPreBootstrapper() {
        if (preBootstrapper == null) {
            preBootstrapper = fromNullable(extractPreBootstrapperType());
        }

        return preBootstrapper.orNull();
    }

    private Type extractPreBootstrapperType() {
        DeclaredType preBootstrapperMirror = getTypeMirrorFromAnnotation(UsePreBootstrapper.class);

        if (preBootstrapperMirror != null) {
            validatePreBootstrapper(preBootstrapperMirror);
            return new Type(preBootstrapperMirror);
        }

        return null;
    }

    private void validatePreBootstrapper(DeclaredType preBootstrapper) {
        TypeElement typeElement = asTypeElement(preBootstrapper);

        if (!IS_ACCESSIBLE_CLASS.apply(typeElement)) {
            logger.error().context(typeElement)
                    .log("Declared pre-bootstrapper must be a public, non-abstract class.");
            throw new UnableToProcessException();
        }
        if (!hasMatchingConstructorOrNone(IS_DEFAULT_CONSTRUCTOR).apply(typeElement)) {
            logger.error().context(typeElement)
                    .log("Declared pre-bootstrapper must have a public, no-arg constructor.");
            throw new UnableToProcessException();
        }
    }

    private DeclaredType getTypeMirrorFromAnnotation(Class<? extends Annotation> annotationClass) {
        Optional<AnnotationMirror> annotation = getAnnotationMirror(element, annotationClass);
        if (annotation.isPresent()) {
            return getAnnotationValue(annotation.get(), "value")
                    .accept(new SimpleAnnotationValueVisitor8<DeclaredType, Void>(null) {
                        @Override
                        public DeclaredType visitType(TypeMirror typeMirror, Void nothing) {
                            return asDeclared(typeMirror);
                        }
                    }, null);
        }

        return null;
    }

    @Override
    public Collection<String> getImports() {
        ImmutableList.Builder<String> imports = ImmutableList.<String>builder()
                .add(getBootstrapper().getQualifiedName());

        if (getPreBootstrapper() != null) {
            imports.add(getPreBootstrapper().getQualifiedName());
        }

        return imports.build();
    }
}
