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

package com.gwtplatform.dispatch.rest.processors.bindings.gin;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

import com.google.auto.service.AutoService;
import com.google.common.base.Strings;
import com.google.inject.TypeLiteral;
import com.gwtplatform.dispatch.rest.processors.AbstractContextProcessor;
import com.gwtplatform.dispatch.rest.processors.bindings.BindingsProcessor;
import com.gwtplatform.dispatch.rest.processors.bindings.GenBinding;
import com.gwtplatform.dispatch.rest.processors.bindings.GenBinding.Auto;
import com.gwtplatform.dispatch.rest.processors.bindings.GenBinding.None;
import com.gwtplatform.dispatch.rest.processors.definitions.TypeDefinition;
import com.gwtplatform.dispatch.rest.processors.outputter.OutputBuilder;

import static com.google.auto.common.AnnotationMirrors.getAnnotationValue;
import static com.google.auto.common.MoreElements.asType;
import static com.google.auto.common.MoreElements.getAnnotationMirror;
import static com.google.auto.common.MoreTypes.asTypeElement;
import static com.google.auto.common.MoreTypes.isTypeOf;

@AutoService(BindingsProcessor.class)
public class GinModuleProcessor extends AbstractContextProcessor<Element, Void> implements BindingsProcessor {
    private static final String TEMPLATE = "com/gwtplatform/dispatch/rest/processors/bindings/gin/GinModule.vm";
    private static final String QUALIFIED_NAME = "com.gwtplatform.dispatch.rest.client.RestGinModule";
    private static final String NONE = None.class.getCanonicalName();
    private static final String AUTO = Auto.class.getCanonicalName();

    private final List<GinBinding> bindings;

    private TypeDefinition impl;
    private JavaFileObject sourceFile;
    private boolean containsTypeLiteral;

    public GinModuleProcessor() {
        this.bindings = new ArrayList<>();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        impl = new TypeDefinition(QUALIFIED_NAME);
        sourceFile = outputter.prepareSourceFile(impl);
    }

    @Override
    public boolean canProcess(Element element) {
        return element.getKind() == ElementKind.CLASS;
    }

    @Override
    public Void process(Element element) {
        GenBinding binding = element.getAnnotation(GenBinding.class);
        TypeElement type = asType(element);

        TypeDefinition implementer = new TypeDefinition(type.asType());
        TypeDefinition implemented = processImplemented(binding, type);
        TypeDefinition scope = processScope(type);

        bindings.add(new GinBinding(implementer, implemented, scope, binding.eagerSingleton()));
        containsTypeLiteral |= implementer.isParameterized() || implemented.isParameterized();

        return null;
    }

    private TypeDefinition processImplemented(GenBinding binding, TypeElement type) {
        String parentClass = binding.parentClass();
        TypeDefinition implemented = null;

        if (!Strings.isNullOrEmpty(parentClass) && !NONE.equals(parentClass)) {
            if (!AUTO.equals(parentClass)) {
                implemented = new TypeDefinition(parentClass);
            } else {
                implemented = findImplemented(type);
            }
        }

        return implemented;
    }

    private TypeDefinition findImplemented(TypeElement type) {
        TypeMirror superclass = type.getSuperclass();
        if (superclass.getKind() != TypeKind.NONE && !isTypeOf(Object.class, superclass)) {
            return new TypeDefinition(superclass);
        }

        List<? extends TypeMirror> interfaces = type.getInterfaces();
        if (interfaces.size() == 1) {
            return new TypeDefinition(interfaces.get(0));
        }

        logger.mandatoryWarning()
                .context(type)
                .log("Could not find the parent class of `%s`.", type.getQualifiedName());
        return null;
    }

    private TypeDefinition processScope(TypeElement type) {
        AnnotationMirror bindingMirror = getAnnotationMirror(type, GenBinding.class).get();
        AnnotationValue scopeValue = getAnnotationValue(bindingMirror, "scope");
        TypeMirror scopeMirror = (TypeMirror) scopeValue.getValue();
        TypeElement scopeElement = asTypeElement(scopeMirror);

        if (!NONE.equals(scopeElement.getQualifiedName().toString())) {
            return new TypeDefinition(scopeMirror);
        }

        return null;
    }

    @Override
    public void processLast() {
        logger.debug("Generating GIN module `%s`.", impl.getQualifiedName());

        OutputBuilder outputBuilder = outputter
                .withTemplateFile(TEMPLATE)
                .withParam("bindings", this.bindings);

        if (containsTypeLiteral) {
            outputBuilder = outputBuilder.withImport(TypeLiteral.class.getCanonicalName());
        }

        outputBuilder.writeTo(impl, sourceFile);
    }
}
