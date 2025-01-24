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

package com.gwtplatform.dispatch.rest.processors;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

import org.mockito.MockingDetails;

import com.google.gwt.dev.util.collect.Maps;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;

/**
 * Improvements to be done in order to be moved to tools:
 * - Support no, one or many annotations
 * - Support setting no, one or many annotation values.
 */
public class AnnotatedElementBuilder {
    private final Element element;
    private final Class<? extends Annotation> annotationClass;
    private final AnnotationMirror annotationMirror;

    public AnnotatedElementBuilder(Annotation annotation) {
        this(mock(Element.class), annotation);
    }

    public AnnotatedElementBuilder(
            Element element,
            Annotation annotation) {
        this(element, findAnnotationClass(annotation));

        // TODO : Not sure why Intellij fails without this cast..
        // noinspection RedundantCast
        given(element.getAnnotation((Class<Annotation>) annotationClass)).willReturn(annotation);
    }

    public AnnotatedElementBuilder(
            Element element,
            Class<? extends Annotation> annotationClass) {
        this.element = element;
        this.annotationClass = annotationClass;
        this.annotationMirror = mock(AnnotationMirror.class);

        initialize();
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Annotation> findAnnotationClass(Annotation annotation) {
        Class<? extends Annotation> clazz;
        MockingDetails mockingDetails = mockingDetails(annotation);

        if (mockingDetails.isMock() || mockingDetails.isSpy()) {
            clazz = (Class<? extends Annotation>) annotation.getClass().getInterfaces()[0];
        } else {
            clazz = annotation.getClass();
        }

        return clazz;
    }

    public static Element stubElementWithoutAnnotations() {
        Element element = mock(Element.class);
        given(element.getAnnotationMirrors()).willReturn((List) new ArrayList<>());

        return element;
    }

    private void initialize() {
        DeclaredType annotationType = mock(DeclaredType.class);
        TypeElement annotationElement = mock(TypeElement.class);
        Name annotationName = mock(Name.class);

        setAnnotationMirrors();
        given(annotationMirror.getAnnotationType()).willReturn(annotationType);
        given(annotationType.asElement()).willReturn(annotationElement);
        given(annotationElement.accept(any(ElementVisitor.class), any())).willReturn(annotationElement);
        given(annotationElement.getQualifiedName()).willReturn(annotationName);
        given(annotationName.contentEquals(annotationClass.getCanonicalName())).willReturn(true);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void setAnnotationMirrors() {
        given(element.getAnnotationMirrors()).willReturn((List) asList(annotationMirror));
    }

    public void setAnnotationValue(String name, Object value) {
        ExecutableElement annotationValueMethod = mock(ExecutableElement.class);
        Name annotationValueMethodName = mock(Name.class);
        AnnotationValue annotationValue = mock(AnnotationValue.class);

        given(annotationValueMethod.getSimpleName()).willReturn(annotationValueMethodName);
        given(annotationValueMethodName.contentEquals(name)).willReturn(true);
        given(annotationValue.getValue()).willReturn(value);
        setAnnotationValues(annotationValueMethod, annotationValue);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void setAnnotationValues(ExecutableElement annotationValueMethod, AnnotationValue annotationValue) {
        given(annotationMirror.getElementValues())
                .willReturn((Map) Maps.create(annotationValueMethod, annotationValue));
    }

    public Element getElement() {
        return element;
    }
}
