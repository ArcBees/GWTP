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

package com.gwtplatform.annotation.processor;

import com.gwtplatform.annotation.In;
import com.gwtplatform.annotation.Optional;
import com.gwtplatform.annotation.Order;
import com.gwtplatform.annotation.Out;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;

/**
 * {@link ReflectionHelper} is an internal class that provides common routines
 * only used by the annotation processors.
 * 
 * @author Brendan Doherty 
 * @author Florian Sauter
 * @author Stephen Haberman (concept) 
 */
@SuppressWarnings("unchecked")
public class ReflectionHelper {

  private TypeElement classRepresenter;
  private ProcessingEnvironment environment;
  
  public ReflectionHelper(ProcessingEnvironment environment, TypeElement classRepresenter) {
    this.classRepresenter = classRepresenter;
    this.environment = environment;
  }
  
  /**
   * Returns all fields annotated with the passed annotation classes.
   */
  public Collection<VariableElement> getAnnotatedFields(Class<? extends Annotation>... annotations) {
    Collection<VariableElement> optionalFields = new ArrayList<VariableElement>();
    for (VariableElement field : getFields()) {
      boolean passedAnnotationsArePresent = true;
      for (Class<? extends Annotation> passedAnnotation : annotations) {
        Annotation fieldAnnotation = field.getAnnotation(passedAnnotation);
        if (fieldAnnotation == null) {
          passedAnnotationsArePresent = false;
          break;
        }
      }
      if (passedAnnotationsArePresent) {
        optionalFields.add(field);
      }
    }
    return optionalFields;
  }
  
  /**
   * Returns the class name. 
   * <p>
   * For example:<br>
   * {@code com.gwtplatform.annotation.Foo}
   * </p>
   * @return the class name.
   */
  public String getClassName() {
    return getPackageName() + '.' + getSimpleClassName();
  }
  
  public TypeElement getClassRepresenter() {
    return classRepresenter;
  }
  
  /**
   * Returns all fields.
   * <p>
   * <b>Important:</b> Fields are not sorted according to @{@link Order}!
   * </p>
   * To get these sorted use {@link ReflectionHelper#getOrderedFields()}.
   */
  public List<VariableElement> getFields() {
    List<? extends Element> members = getElementUtils().getAllMembers(classRepresenter);
    return ElementFilter.fieldsIn(members);
  }
  
  /**
   * Returns all fields annotated with @{@link In}.
   */
  public Collection<VariableElement> getInFields() {
    return sortFields(In.class, getAnnotatedFields(In.class));
  }
  
  /**
   * Returns all fields annotated with @{@link Optional}. Sorted based on the @
   * {@link Order} annotation.
   */
  public Collection<VariableElement> getOptionalFields() {
    return sortFields(Order.class, getAnnotatedFields(Optional.class));
  }
  
  /**
   * Returns all fields ordered. See {@link Order} for detailed informations.
   */
  public Collection<VariableElement> getOrderedFields() {
    return sortFields(Order.class, getFields());
  }
  
  /**
   * Returns all fields annotated with @{@link Out}.
   */
  public Collection<VariableElement> getOutFields() {
    return sortFields(Out.class, getAnnotatedFields(Out.class));
  }
  
  public String getPackageName() {
    return getElementUtils().getPackageOf(classRepresenter).getQualifiedName().toString();
  }
  
  public ProcessingEnvironment getProcessingEnvironment() {
    return environment;
  }
  
  public String getSimpleClassName() {
    return classRepresenter.getSimpleName().toString();
  }
  
  public boolean hasOnlyOptionalFields() {
    return getOptionalFields().size() == getFields().size();
  }
  
  public boolean hasOnlyOptionalFields(Class<? extends Annotation> annotation) {
    return getOptionalFields().size() == getAnnotatedFields(annotation).size();
  }
  
  /**
   * Sorts the passed fields based on the passed annotation sort logic.
   */
  public Collection<VariableElement> sortFields(Class<? extends Annotation> annotation, Collection<VariableElement> fields) {
    SortedMap<Integer, VariableElement> sortedFields = new TreeMap<Integer, VariableElement>();
    if (In.class.equals(annotation)) {
      sortInFields(sortedFields, fields);
    } else if (Out.class.equals(annotation)) {
      sortOutFields(sortedFields, fields);
    } else if (Order.class.equals(annotation)) {
      sortOrderFields(sortedFields, fields);
    } else {
      return fields;
    }
    return sortedFields.values();
  }
  
  /**
   * Utility method.
   */
  protected Elements getElementUtils() {
    return environment.getElementUtils();
  }
  
  protected void sortInFields(SortedMap<Integer, VariableElement> sortedFields, Collection<VariableElement> fields) {
    for (VariableElement fieldElement : fields) {
      In inFieldAnnotation = fieldElement.getAnnotation(In.class);
      if (inFieldAnnotation != null) {
        sortedFields.put(inFieldAnnotation.value(), fieldElement);
      }
    }
  }
  
  protected void sortOrderFields(SortedMap<Integer, VariableElement> sortedFields, Collection<VariableElement> fields) {
    int maxOrderNum = -1;
    for (VariableElement fieldElement : fields) {
      Order order = fieldElement.getAnnotation(Order.class);
      if (order != null) {
        maxOrderNum = Math.max(maxOrderNum, order.value());
      }
    }

    for (VariableElement fieldDecl : fields) {
      Order order = fieldDecl.getAnnotation(Order.class);
      if (order != null) {
        maxOrderNum = Math.max(maxOrderNum, order.value());
        sortedFields.put(order.value(), fieldDecl);
      } else {
        sortedFields.put(++maxOrderNum, fieldDecl);
      }
    }
  }
  
  protected void sortOutFields(SortedMap<Integer, VariableElement> sortedFields, Collection<VariableElement> fields) {
    for (VariableElement fieldElement : fields) {
      Out outFieldAnnotation = fieldElement.getAnnotation(Out.class);
      if (outFieldAnnotation != null) {
        sortedFields.put(outFieldAnnotation.value(), fieldElement);
      }
    }
  }
}
