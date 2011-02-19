/**
 * Copyright 2010 ArcBees Inc.
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

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * <p>
 * Abstract processor class for all {@code @GenX} annotations.
 * </p>
 * 
 * Annotate your processor with @{@link SupportedAnnotationTypes} or override
 * {@link #getSupportedAnnotationTypes()} to receive elements, that are
 * annotated in the current environment with one of the given annotations. You
 * can get access these elements using the method
 * {@link #process(Element annotatedElement)}.
 * 
 * @author Florian Sauter
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
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
    if (!roundEnvironment.processingOver()) {
      for (TypeElement currentAnnotation : annotations) {
        for (String supportedAnnotationName : getSupportedAnnotationTypes()) {
          if (currentAnnotation.getQualifiedName().contentEquals(supportedAnnotationName)) {
            for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(currentAnnotation)) {
              this.process(annotatedElement);
            }
          }
        }
      }
    }
    return true;
  }
  
  /**
   * Override this function to receive elements which you've declared in
   * supported annotations.
   * 
   * @param annotatedElement the annotated element.
   */
  public abstract void process(Element annotatedElement);
}
