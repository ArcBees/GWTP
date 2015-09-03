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

package com.gwtplatform.dispatch.rest.processors.details;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.gwtplatform.dispatch.rest.processors.NameUtils;
import com.gwtplatform.dispatch.rest.processors.resolvers.HttpVerbResolver;
import com.gwtplatform.dispatch.rest.shared.ContentType;
import com.gwtplatform.dispatch.rest.shared.HttpParameter;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.processors.tools.domain.HasImports;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.notNull;
import static com.gwtplatform.dispatch.rest.processors.resolvers.ContentTypeResolver.resolveConsumes;
import static com.gwtplatform.dispatch.rest.processors.resolvers.ContentTypeResolver.resolveProduces;

public class EndPointDetails implements HasImports {
    private static final String MANY_POTENTIAL_BODY = "Method `%s` has more than one body parameter.";
    private static final String FORM_AND_BODY_PARAM = "Method `%s` has both a @FormParam and a body parameter. "
            + "Specify one or the other.";
    private static final String GET_WITH_BODY = "End-point method annotated with @GET or @HEAD contains illegal "
            + "parameters. Verify that `%s` contains no body or @FormParam parameters.";
    private static final String BAD_REST_ACTION = "Method `%s` returns a RestAction<> without a type argument.";

    private final Logger logger;
    private final Utils utils;

    private Path path;
    private Secured secured;
    private Set<ContentType> consumes;
    private Set<ContentType> produces;
    private HttpVerb verb;
    private Collection<HttpVariable> httpVariables = Collections.emptyList();
    private Optional<HttpVariable> body = Optional.absent();
    private Type resultType;

    public EndPointDetails(
            Logger logger,
            Utils utils,
            TypeElement element) {
        this.logger = logger;
        this.utils = utils;
        this.path = new Path(element);
        this.secured = new Secured(element);
        this.consumes = ImmutableSet.copyOf(resolveConsumes(element));
        this.produces = ImmutableSet.copyOf(resolveProduces(element));
    }

    public EndPointDetails(
            Logger logger,
            Utils utils,
            TypeElement element,
            EndPointDetails parentDetails) {
        this.logger = logger;
        this.utils = utils;

        resolveAndInheritParent(element, parentDetails);
    }

    public EndPointDetails(
            Logger logger,
            Utils utils,
            Method method,
            EndPointDetails parentDetails) {
        this.logger = logger;
        this.utils = utils;

        resolveAndInheritParent(method.getElement(), parentDetails);
        resolveVerb(method);
        resolveResult(method);
        resolveVariables(method);
    }

    public void resolveAndInheritParent(Element element, EndPointDetails parentDetails) {
        path = new Path(element, parentDetails.getPath());
        secured = new Secured(element, parentDetails.getSecured());
        consumes = ImmutableSet.copyOf(resolveConsumes(element, parentDetails.getConsumes()));
        produces = ImmutableSet.copyOf(resolveProduces(element, parentDetails.getProduces()));
        httpVariables = parentDetails.getHttpVariables();
        body = parentDetails.getBody();
        resultType = parentDetails.getResultType();
    }

    public void resolveVerb(Method method) {
        verb = new HttpVerbResolver(logger).resolve(method.getElement());
    }

    private void resolveResult(Method method) {
        String restActionName = RestAction.class.getCanonicalName();
        Type returnType = method.getReturnType();

        if (restActionName.equals(returnType.getQualifiedName())) {
            resolveRestActionResult(method);
        } else {
            resultType = returnType;
        }
    }

    private void resolveRestActionResult(Method method) {
        List<Type> typeArguments = method.getReturnType().getTypeArguments();

        if (typeArguments.size() == 1) {
            resultType = typeArguments.get(0);
        } else {
            ExecutableElement element = method.getElement();

            logger.error().context(element).log(BAD_REST_ACTION, NameUtils.qualifiedMethodName(element));
            throw new UnableToProcessException();
        }
    }

    private void resolveVariables(Method method) {
        httpVariables = new ArrayList<>(httpVariables);

        for (Variable variable : method.getParameters()) {
            resolveVariable(method, variable);
        }

        httpVariables = ImmutableList.copyOf(httpVariables);

        logPotentialErrors(method);
    }

    private void resolveVariable(Method method, Variable variable) {
        HttpVariable httpVariable = new HttpVariable(logger, utils, variable);

        if (body.isPresent() && httpVariable.isBody()) {
            ExecutableElement methodElement = method.getElement();

            logger.error()
                    .context(methodElement)
                    .log(MANY_POTENTIAL_BODY, NameUtils.qualifiedMethodName(methodElement));
            throw new UnableToProcessException();
        }

        if (httpVariable.isBody()) {
            body = Optional.of(httpVariable);
        } else {
            httpVariables.add(httpVariable);
        }
    }

    private void logPotentialErrors(Method method) {
        ExecutableElement methodElement = method.getElement();
        String methodName = NameUtils.qualifiedMethodName(methodElement);
        boolean containsFormVariables = containsFormVariables();
        boolean containsBody = body.isPresent();

        if (containsBody && containsFormVariables) {
            logger.error().context(methodElement).log(FORM_AND_BODY_PARAM, methodName);
            throw new UnableToProcessException();
        }
        if ((verb == HttpVerb.GET || verb == HttpVerb.HEAD) && (containsBody || containsFormVariables)) {
            logger.error().context(methodElement).log(GET_WITH_BODY, methodName);
            throw new UnableToProcessException();
        }
    }

    private boolean containsFormVariables() {
        return Iterables.any(httpVariables, new Predicate<HttpVariable>() {
            @Override
            public boolean apply(HttpVariable httpVariable) {
                return httpVariable.getHttpAnnotation().get().getParameterType() == HttpParameter.Type.FORM;
            }
        });
    }

    public HttpVerb getVerb() {
        return verb;
    }

    public Path getPath() {
        return path;
    }

    public Secured getSecured() {
        return secured;
    }

    public Set<ContentType> getConsumes() {
        return consumes;
    }

    public Set<ContentType> getProduces() {
        return produces;
    }

    public Collection<HttpVariable> getHttpVariables() {
        return httpVariables;
    }

    public Optional<HttpVariable> getBody() {
        return body;
    }

    public Type getResultType() {
        return resultType;
    }

    @Override
    public Collection<String> getImports() {
        FluentIterable<String> imports = FluentIterable.from(httpVariables)
                .transformAndConcat(EXTRACT_IMPORTS_FUNCTION)
                .append(resultType.getImports());

        if (body.isPresent()) {
            imports = imports.append(body.get().getImports());
        }
        if (!httpVariables.isEmpty()) {
            imports = imports.append(HttpParameter.Type.class.getCanonicalName());
        }

        return imports.filter(and(notNull(), not(equalTo("")))).toList();
    }
}
