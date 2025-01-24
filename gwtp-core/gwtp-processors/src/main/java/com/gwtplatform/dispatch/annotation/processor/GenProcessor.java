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

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

/**
 * <p>
 * Abstract processor class for all {@code @GenX} annotations.
 * </p>
 * <p>
 * Annotate your processor with @{@link SupportedAnnotationTypes} or override
 * {@link #getSupportedAnnotationTypes()} to receive elements, that are
 * annotated in the current environment with one of the given annotations. You
 * can get access these elements using the method
 * {@link #process(Element annotatedElement)}.
 */
public abstract class GenProcessor extends AbstractProcessor {

    private ProcessingEnvironment environment;

    @Override
    public synchronized void init(ProcessingEnvironment environment) {
        super.init(environment);
        this.environment = environment;
    }

    /**
     * Returns the current processing environment.
     *
     * @return the processing environment.
     */
    public ProcessingEnvironment getEnvironment() {
        return environment;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        if (!roundEnvironment.processingOver()) {
            onProcessingStarted();
            for (String supportedAnnotationName : getSupportedAnnotationTypes()) {
                printMessage("Searching for " + supportedAnnotationName + " annotations.");
                try {
                    Class<?> supportedAnnotationClass = Class.forName(supportedAnnotationName);
                    if (supportedAnnotationClass.isAnnotation()) {
                        for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith((Class<? extends
                                Annotation>) supportedAnnotationClass)) {
                            printMessage("Found " + annotatedElement.toString() + ".");
                            this.process(annotatedElement);
                        }
                    }
                } catch (ClassNotFoundException e) {
                    printError("Annotation not found: " + supportedAnnotationName);
                }
            }
            onProcessingCompleted();
        }
        return true;
    }

    /**
     * Prints a message.
     *
     * @param message the message
     */
    public void printMessage(String message) {
        getEnvironment().getMessager().printMessage(Kind.NOTE, message);
    }

    /**
     * Prints an error.
     *
     * @param message the error message
     */
    public void printError(String message) {
        getEnvironment().getMessager().printMessage(Kind.ERROR, message);
    }

    /**
     * Override this function to receive elements which you've declared in
     * supported annotations.
     *
     * @param annotatedElement the annotated element.
     */
    public abstract void process(Element annotatedElement);

    /**
     * Utility method called after processing has started.
     */
    public void onProcessingStarted() {
        printMessage(getClass().getName() + " started.");
    }

    /**
     * Utility method called after the processing is finished.
     */
    public void onProcessingCompleted() {
        printMessage(getClass().getName() + " finished.");
    }
}
