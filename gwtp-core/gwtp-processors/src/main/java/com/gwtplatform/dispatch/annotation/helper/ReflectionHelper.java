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

package com.gwtplatform.dispatch.annotation.helper;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;

import com.gwtplatform.dispatch.annotation.In;
import com.gwtplatform.dispatch.annotation.Optional;
import com.gwtplatform.dispatch.annotation.Order;
import com.gwtplatform.dispatch.annotation.Out;

/**
 * {@link ReflectionHelper} is an internal class that provides common routines
 * only used by the annotation processors.
 */
@SuppressWarnings("unchecked")
public class ReflectionHelper {

    private final TypeElement classRepresenter;
    private final ProcessingEnvironment environment;

    public ReflectionHelper(ProcessingEnvironment environment, TypeElement classRepresenter) {
        this.classRepresenter = classRepresenter;
        this.environment = environment;
    }

    public Collection<VariableElement> filterConstantFields(Collection<VariableElement> fieldElements) {
        return filterFields(fieldElements, Modifier.STATIC, Modifier.FINAL);
    }

    /**
     * Returns only fields which are not annotated with one of the passed annotation.
     */
    public Collection<VariableElement> filterFields(Collection<VariableElement> fieldElements,
            Class<? extends Annotation>... annotations) {
        Collection<VariableElement> filteredFields = new ArrayList<VariableElement>();
        filteredFields.addAll(fieldElements);
        for (VariableElement fieldElement : fieldElements) {
            for (Class<? extends Annotation> passedAnnotation : annotations) {
                Annotation fieldAnnotation = fieldElement.getAnnotation(passedAnnotation);
                if (fieldAnnotation != null) {
                    filteredFields.remove(fieldElement);
                    break;
                }
            }
        }
        return filteredFields;
    }

    /**
     * Returns only fields which do not contain one of the passed modifiers.
     */
    public Collection<VariableElement> filterFields(Collection<VariableElement> fieldElements, Modifier... modifiers) {
        Collection<VariableElement> filteredFields = new ArrayList<VariableElement>();
        filteredFields.addAll(fieldElements);
        for (VariableElement fieldElement : fieldElements) {
            for (Modifier modifier : modifiers) {
                if (fieldElement.getModifiers().contains(modifier)) {
                    filteredFields.remove(fieldElement);
                    break;
                }
            }
        }
        return filteredFields;
    }

    /**
     * Returns only fields which simple names do not equal the passed field names.
     */
    public Collection<VariableElement> filterFields(Collection<VariableElement> fieldElements,
            String... simpleFieldNames) {
        Collection<VariableElement> filteredFields = new ArrayList<VariableElement>();
        filteredFields.addAll(fieldElements);
        for (VariableElement fieldElement : fieldElements) {
            for (String simpleFieldName : simpleFieldNames) {
                if (fieldElement.getSimpleName().toString().equals(simpleFieldName)) {
                    filteredFields.remove(fieldElement);
                    break;
                }
            }
        }
        return filteredFields;
    }

    /**
     * Returns all fields annotated with the passed annotation classes.
     */
    public Collection<VariableElement> getAnnotatedFields(Class<? extends Annotation>... annotations) {
        Collection<VariableElement> fieldsCopy = getFields();
        for (Class<? extends Annotation> annotation : annotations) {
            Collection<VariableElement> nonAnnotatedFields = filterFields(getFields(), annotation);
            fieldsCopy.removeAll(nonAnnotatedFields);
        }
        return fieldsCopy;
    }

    /**
     * Returns the class name.
     * <p>
     * For example:<br>
     * {@code com.gwtplatform.dispatch.annotation.Foo}
     * </p>
     *
     * @return the class name.
     */
    public String getClassName() {
        return getPackageName() + '.' + getSimpleClassName();
    }

    public TypeElement getClassRepresenter() {
        return classRepresenter;
    }

    /**
     * Returns all fields ordered that are {@link Modifier#FINAL} or {@link Modifier#STATIC}.
     */
    public Collection<VariableElement> getConstantFields() {
        return getModifierFields(Modifier.FINAL, Modifier.STATIC);
    }

    /**
     * Returns all fields.
     * <p>
     * <b>Important:</b> Fields are not sorted according to @{@link Order}!
     * </p>
     * To get these sorted use {@link ReflectionHelper#getOrderedFields()}.
     */
    public Collection<VariableElement> getFields() {
        List<? extends Element> members = getElementUtils().getAllMembers(classRepresenter);
        return ElementFilter.fieldsIn(members);
    }

    /**
     * Returns all fields which contains {@link Modifier#FINAL}.
     */
    public Collection<VariableElement> getFinalFields() {
        return filterFields(getOrderedFields(), Modifier.FINAL);
    }

    /**
     * Returns all fields annotated with @{@link In}. Sorted based on the @
     * {@link In} annotation.
     */
    public Collection<VariableElement> getInFields() {
        return sortFields(In.class, getAnnotatedFields(In.class));
    }

    /**
     * Returns all fields with the passed modifier.
     */
    public Collection<VariableElement> getModifierFields(Modifier... modifiers) {
        Collection<VariableElement> modifierFields = new ArrayList<VariableElement>();
        modifierFields.addAll(getFields());
        for (Modifier modifier : modifiers) {
            Collection<VariableElement> nonModifierFields = filterFields(getFields(), modifier);
            modifierFields.removeAll(nonModifierFields);
        }
        return modifierFields;
    }

    /**
     * Returns all fields that are not {@link Modifier#FINAL} or
     * {@link Modifier#STATIC}. Sorted based on the @ {@link Order} annotation.
     */
    public Collection<VariableElement> getNonConstantFields() {
        return filterFields(getOrderedFields(), Modifier.FINAL, Modifier.STATIC);
    }

    /**
     * Returns all non constant fields annotated with @{@link Optional}. Sorted
     * based on the @ {@link Order} annotation.
     */
    public Collection<VariableElement> getOptionalFields() {
        return sortFields(Order.class, filterConstantFields(getAnnotatedFields(Optional.class)));
    }

    /**
     * Returns all non constant fields annotated with passed annotation.
     * <p>
     * <b>Important:</b> Fields are not sorted!
     * </p>
     */
    public Collection<VariableElement> getOptionalFields(Class<? extends Annotation> annotation) {
        return filterConstantFields(getAnnotatedFields(Optional.class, annotation));
    }

    /**
     * Returns all fields ordered. Sorted based on the @ {@link Order} annotation.
     */
    public Collection<VariableElement> getOrderedFields() {
        return sortFields(Order.class, getFields());
    }

    /**
     * Returns all fields annotated with @{@link Out}. Sorted based on the @
     * {@link Out} annotation.
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

    /**
     * Returns all non {@link Optional}|{@link Modifier#STATIC}|
     * {@link Modifier#FINAL} fields <b>ordered</b>. Sorted based on the @
     * {@link Order} annotation.
     * <p>
     * Required are all:
     * <ul>
     * <li>Declared fields matching rules below</li>
     * <li><b>Non</b> annotated {@link Optional} fields</li>
     * <li><b>Non</b> {@link Modifier#STATIC} fields</li>
     * <li><b>Non</b> {@link Modifier#FINAL} fields</li>
     * </ul>
     */
    public Collection<VariableElement> getRequiredFields() {
        Collection<VariableElement> fields = getFields();
        fields.removeAll(getOptionalFields());
        fields = filterFields(fields, Modifier.FINAL, Modifier.STATIC);
        return sortFields(Order.class, fields);
    }

    public String getSimpleClassName() {
        return classRepresenter.getSimpleName().toString();
    }

    /**
     * Returns all fields which contains {@link Modifier#STATIC}.
     */
    public Collection<VariableElement> getStaticFields() {
        return filterFields(getOrderedFields(), Modifier.STATIC);
    }

    /**
     * Sorts the passed fields based on the passed annotation sort logic.
     */
    public Collection<VariableElement> sortFields(Class<? extends Annotation> annotation,
            Collection<VariableElement> fields) {
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

    public boolean hasOptionalFields() {
        return getOptionalFields().size() > 0;
    }

    public boolean hasRequiredFields() {
        return getRequiredFields().size() > 0;
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

    protected void sortOrderFields(SortedMap<Integer, VariableElement> sortedFields,
            Collection<VariableElement> fields) {
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
