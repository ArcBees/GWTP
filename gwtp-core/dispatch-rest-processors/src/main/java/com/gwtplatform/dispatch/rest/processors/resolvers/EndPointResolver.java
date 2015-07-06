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

package com.gwtplatform.dispatch.rest.processors.resolvers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.google.common.base.Optional;
import com.gwtplatform.dispatch.rest.processors.definitions.EndPointDefinition;
import com.gwtplatform.dispatch.rest.processors.definitions.HttpVariableDefinition;
import com.gwtplatform.dispatch.rest.processors.definitions.TypeDefinition;
import com.gwtplatform.dispatch.rest.processors.logger.Logger;
import com.gwtplatform.dispatch.rest.rebind.HttpVerb;
import com.gwtplatform.dispatch.rest.shared.ContentType;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;
import com.gwtplatform.dispatch.rest.shared.RestAction;

import static com.google.auto.common.MoreElements.asType;
import static com.google.auto.common.MoreTypes.asDeclared;
import static com.gwtplatform.dispatch.rest.processors.NameFactory.methodName;
import static com.gwtplatform.dispatch.rest.processors.resolvers.ContentTypeResolver.resolveConsumes;
import static com.gwtplatform.dispatch.rest.processors.resolvers.ContentTypeResolver.resolveProduces;

public class EndPointResolver {
    private static final String MANY_POTENTIAL_BODY = "Method `%s` has more than one body parameter.";
    private static final String FORM_AND_BODY_PARAM = "Method `%s` has both a @FormParam and a body parameter. "
            + "Specify one or the other.";
    private static final String GET_WITH_BODY = "End-point method annotated with @GET or @HEAD contains illegal "
            + "parameters. Verify that `%s#%s` contains no body or @FormParam parameters.";
    private static final String BAD_REST_ACTION = "Method `%s` returns a RestAction<> without a type argument.";

    private final Logger logger;
    private final HttpVerbResolver httpVerbResolver;
    private final HttpVariableResolver httpVariableResolver;

    public EndPointResolver(
            Logger logger,
            Types types,
            Elements elements) {
        this.logger = logger;
        this.httpVerbResolver = new HttpVerbResolver(logger);
        this.httpVariableResolver = new HttpVariableResolver(logger, types, elements);
    }

    public boolean canResolve(ExecutableElement element, EndPointDefinition parent) {
        return hasValidVerb(element)
                && hasValidReturnType(element)
                && hasValidParameters(element, parent);
    }

    public boolean hasValidVerb(ExecutableElement element) {
        return httpVerbResolver.canResolve(element);
    }

    private boolean hasValidReturnType(ExecutableElement element) {
        DeclaredType returnType = asDeclared(element.getReturnType());
        String restActionName = RestAction.class.getCanonicalName();
        String returnTypeName = asType(returnType.asElement()).getQualifiedName().toString();

        if (restActionName.equals(returnTypeName)) {
            boolean isValid = returnType.getTypeArguments().size() == 1;
            if (!isValid) {
                logger.warning(BAD_REST_ACTION, methodName(element));
            }

            return isValid;
        }

        return false;
    }

    private boolean hasValidParameters(ExecutableElement element, EndPointDefinition parent) {
        boolean valid = true;
        int bodyCount = containsBody(parent) ? 1 : 0;
        boolean hasForms = containsForm(parent);

        for (VariableElement variableElement : element.getParameters()) {
            if (httpVariableResolver.canResolve(variableElement)) {
                HttpVariableDefinition variable = httpVariableResolver.resolve(variableElement);

                if (variable.isBody()) {
                    ++bodyCount;
                } else {
                    hasForms |= variable.getHttpAnnotation().getParameterType() == Type.FORM;
                }
            } else {
                valid = false;
            }
        }

        return hasErrors(element, bodyCount, hasForms) && valid;
    }

    private boolean hasErrors(ExecutableElement element, int bodyCount, boolean hasForms) {
        boolean valid = true;
        String methodName = methodName(element);

        if (bodyCount != 0 && hasForms) {
            logger.error(FORM_AND_BODY_PARAM, methodName);
            valid = false;
        }

        if (bodyCount > 1) {
            logger.error(MANY_POTENTIAL_BODY, methodName);
            valid = false;
        }

        HttpVerb verb = httpVerbResolver.resolve(element);
        if (verb == HttpVerb.GET || verb == HttpVerb.HEAD) {
            logger.error(GET_WITH_BODY, methodName);
            valid = false;
        }

        return valid;
    }

    private boolean containsBody(EndPointDefinition endPoint) {
        for (HttpVariableDefinition variable : endPoint.getHttpParameters()) {
            if (variable.isBody()) {
                return true;
            }
        }

        return false;
    }

    private boolean containsForm(EndPointDefinition endPoint) {
        for (HttpVariableDefinition variable : endPoint.getHttpParameters()) {
            if (variable.getHttpAnnotation().getParameterType() == Type.FORM) {
                return true;
            }
        }

        return false;
    }

    public EndPointDefinition resolve(DeclaredType type) {
        Element element = type.asElement();

        String path = PathResolver.resolve(element);
        boolean secured = SecuredResolver.resolve(element);
        Set<ContentType> consumes = resolveConsumes(element);
        Set<ContentType> produces = resolveProduces(element);

        return new EndPointDefinition(path, secured, consumes, produces);
    }

    public EndPointDefinition resolve(ExecutableElement element, EndPointDefinition parent) {
        String path = PathResolver.resolve(element, parent.getPath());
        boolean secured = parent.isSecured() && SecuredResolver.resolve(element);
        Set<ContentType> consumes = resolveConsumes(element, parent.getConsumes());
        Set<ContentType> produces = resolveProduces(element, parent.getProduces());
        HttpVerb verb = httpVerbResolver.resolve(element);
        List<HttpVariableDefinition> variables = resolveVariables(element, parent);
        TypeDefinition result = resolveResult(element);

        return new EndPointDefinition(path, secured, consumes, produces, verb, variables, result);
    }

    public List<HttpVariableDefinition> resolveVariables(ExecutableElement element, EndPointDefinition parent) {
        List<HttpVariableDefinition> variables = new ArrayList<>(parent.getHttpParameters());
        Optional<HttpVariableDefinition> body = parent.getBody();

        if (body.isPresent()) {
            variables.add(body.get());
        }

        for (VariableElement variableElement : element.getParameters()) {
            variables.add(httpVariableResolver.resolve(variableElement));
        }

        return variables;
    }

    public TypeDefinition resolveResult(ExecutableElement element) {
        DeclaredType returnType = asDeclared(element.getReturnType());
        List<? extends TypeMirror> typeArguments = returnType.getTypeArguments();

        return new TypeDefinition(typeArguments.get(0));
    }
}
