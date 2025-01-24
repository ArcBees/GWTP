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

import java.util.Collection;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.web.bindery.event.shared.Event;
import com.gwtplatform.processors.tools.domain.HasImports;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.utils.Utils;

import static javax.lang.model.util.ElementFilter.fieldsIn;
import static javax.lang.model.util.ElementFilter.methodsIn;
import static com.google.auto.common.MoreElements.hasModifiers;
import static com.google.auto.common.MoreTypes.asDeclared;
import static com.google.auto.common.MoreTypes.asExecutable;
import static com.google.auto.common.MoreTypes.asMemberOf;
import static com.google.auto.common.MoreTypes.asTypeElement;
import static com.google.auto.common.MoreTypes.isType;
import static com.google.auto.common.MoreTypes.isTypeOf;
import static com.google.auto.common.MoreTypes.nonObjectSuperclass;
import static com.google.common.collect.FluentIterable.from;

public class ProxyEventMethod implements HasImports {
    private final Logger logger;
    private final Utils utils;
    private final ExecutableElement element;

    private DeclaredType eventTypeMirror;
    private DeclaredType handlerTypeMirror;
    private String annotatedMethodName;
    private String handlerMethodName;
    private String typeAccessor;

    public ProxyEventMethod(
            Logger logger,
            Utils utils,
            ExecutableElement element) {
        this.logger = logger;
        this.utils = utils;
        this.element = element;
    }

    public String getHandlerMethodName() {
        if (handlerMethodName == null) {
            ExecutableElement handlerMethod = extractOnlyHandlerMethod();
            validateEventHandler(handlerMethod);

            handlerMethodName = handlerMethod.getSimpleName().toString();
        }

        return handlerMethodName;
    }

    private ExecutableElement extractOnlyHandlerMethod() {
        TypeElement handlerElement = asTypeElement(getHandlerTypeMirror());
        List<ExecutableElement> handlerMethods = methodsIn(handlerElement.getEnclosedElements());
        if (handlerMethods.size() != 1) {
            throwBadHandlerSignature(handlerElement);
        }

        return handlerMethods.get(0);
    }

    private void validateEventHandler(ExecutableElement handlerMethod) {
        if (handlerMethod.getReturnType().getKind() != TypeKind.VOID) {
            throwBadHandlerSignature(handlerMethod);
        }

        List<? extends VariableElement> parameters = handlerMethod.getParameters();
        if (parameters.size() != 1) {
            throwBadHandlerSignature(handlerMethod);
        }

        VariableElement parameter = parameters.get(0);
        TypeMirror parameterType = asMemberOf(utils.getTypes(), getHandlerTypeMirror(), parameter);
        if (!utils.getTypes().isSameType(parameterType, getEventTypeMirror())) {
            throwBadHandlerSignature(parameter);
        }
    }

    private void throwBadHandlerSignature(Element element) {
        logger.error()
                .context(element)
                .log("Event handler used in a @ProxyEvent context must contain a single method returning void and "
                        + "accepting a single argument of type `%s`.", getEventType());
        throw new UnableToProcessException();
    }

    public Type getHandlerType() {
        return new Type(getHandlerTypeMirror());
    }

    private DeclaredType getHandlerTypeMirror() {
        if (handlerTypeMirror == null) {
            Optional<DeclaredType> optionalSuperType =
                    nonObjectSuperclass(utils.getTypes(), utils.getElements(), getEventTypeMirror());

            while (optionalSuperType.isPresent()) {
                DeclaredType superType = optionalSuperType.get();
                if (isTypeOf(Event.class, superType)) {
                    handlerTypeMirror = extractHandlerTypeFromEvent(superType);
                    break;
                } else {
                    optionalSuperType = nonObjectSuperclass(utils.getTypes(), utils.getElements(), superType);
                }
            }
        }

        return handlerTypeMirror;
    }

    private DeclaredType extractHandlerTypeFromEvent(DeclaredType superType) {
        List<? extends TypeMirror> typeArguments = superType.getTypeArguments();
        if (typeArguments.size() != 1) {
            logger.error().context(eventTypeMirror.asElement())
                    .log("GWT event does not specify a proper handler type while extending Event.");
            throw new UnableToProcessException();
        }

        DeclaredType typeMirror = asDeclared(typeArguments.get(0));
        if (typeMirror.asElement().getKind() != ElementKind.INTERFACE) {
            logger.error().context(typeMirror.asElement()).log("GWT event handler must be an interface.");
            throw new UnableToProcessException();
        }

        return typeMirror;
    }

    public String getTypeAccessor() {
        if (typeAccessor == null) {
            TypeElement eventTypeElement = asTypeElement(getEventTypeMirror());
            List<? extends Element> members = utils.getElements().getAllMembers(eventTypeElement);
            Optional<ExecutableElement> methodAccessor = extractEventTypeMethodAccessor(members);
            Optional<VariableElement> fieldAccessor = extractEventTypeFieldAccessor(members);

            // TODO: We could explicitly check that in Event<H1> and Type<H2>, H1 === H2.
            // For now, we will get compile time error.

            if (methodAccessor.isPresent()) {
                typeAccessor = methodAccessor.get().getSimpleName().toString() + "()";
            } else if (fieldAccessor.isPresent()) {
                typeAccessor = fieldAccessor.get().getSimpleName().toString();
            } else {
                logger.error().context(eventTypeElement)
                        .log("Event used with a @ProxyEvent, but it does not contain a static field `TYPE` or a static "
                                + "method `getType()` returning a valid `Event.Type<>` instance.");
                throw new UnableToProcessException();
            }
        }

        return typeAccessor;
    }

    private Optional<ExecutableElement> extractEventTypeMethodAccessor(List<? extends Element> members) {
        return FluentIterable.from(methodsIn(members))
                .firstMatch(method -> method.getSimpleName().contentEquals("getType")
                        && method.getParameters().isEmpty()
                        && isGwtEventType(method, method.getReturnType()));
    }

    private Optional<VariableElement> extractEventTypeFieldAccessor(List<? extends Element> members) {
        return FluentIterable.from(fieldsIn(members))
                .firstMatch(variable -> variable.getSimpleName().contentEquals("TYPE")
                        && isGwtEventType(variable, variable.asType()));
    }

    private boolean isGwtEventType(Element element, TypeMirror typeMirror) {
        return hasModifiers(Modifier.PUBLIC, Modifier.STATIC).apply(element)
                && isType(typeMirror)
                && utils.getTypes().isSubtype(typeMirror, utils.createWithWildcard(Event.Type.class))
                && asDeclared(typeMirror).getTypeArguments().size() == 1;
    }

    public Type getEventType() {
        return new Type(getEventTypeMirror());
    }

    private DeclaredType getEventTypeMirror() {
        if (eventTypeMirror == null) {
            List<? extends TypeMirror> parameterTypes = asExecutable(element.asType()).getParameterTypes();
            if (parameterTypes.size() != 1) {
                throwInvalidProxyMethodArguments();
            }

            TypeMirror parameterType = parameterTypes.get(0);
            TypeMirror genericEventType = utils.createWithWildcard(Event.class);
            if (!utils.getTypes().isAssignable(parameterType, genericEventType)) {
                throwInvalidProxyMethodArguments();
            }

            eventTypeMirror = asDeclared(parameterType);
        }

        return eventTypeMirror;
    }

    private DeclaredType throwInvalidProxyMethodArguments() {
        logger.error().context(element)
                .log("@ProxyEvent method must accept a single argument extending GWT's Event.");
        throw new UnableToProcessException();
    }

    public String getAnnotatedMethodName() {
        if (annotatedMethodName == null) {
            if (!element.getThrownTypes().isEmpty()) {
                logger.error().context(element).log("@ProxyEvent method cannot throw exceptions.");
                throw new UnableToProcessException();
            } else if (!element.getTypeParameters().isEmpty()) {
                logger.error().context(element).log("@ProxyEvent method cannot have type parameters.");
                throw new UnableToProcessException();
            } else if (hasModifiers(Modifier.PRIVATE).apply(element)) {
                logger.error().context(element)
                        .log("@ProxyEvent method must not be private. Package-local is recommended.");
                throw new UnableToProcessException();
            } else if (element.getReturnType().getKind() != TypeKind.VOID) {
                logger.warning().context(element)
                        .log("@ProxyEvent method does not return void. The returned value will be ignored.");
            }

            annotatedMethodName = element.getSimpleName().toString();
        }

        return annotatedMethodName;
    }

    @Override
    public Collection<String> getImports() {
        return from(getEventType().getImports())
                .append(getHandlerType().getImports())
                .toList();
    }
}
