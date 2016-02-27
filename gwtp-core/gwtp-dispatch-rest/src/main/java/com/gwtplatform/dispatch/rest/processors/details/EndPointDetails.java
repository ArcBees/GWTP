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
import com.gwtplatform.processors.tools.utils.Primitives;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.ImmutableSet.copyOf;
import static com.gwtplatform.dispatch.rest.processors.resolvers.ContentTypeResolver.resolveConsumes;
import static com.gwtplatform.dispatch.rest.processors.resolvers.ContentTypeResolver.resolveProduces;
import static com.gwtplatform.processors.tools.utils.Primitives.findByPrimitive;

public class EndPointDetails implements HasImports {
    public interface Factory {
        EndPointDetails create(TypeElement element);

        EndPointDetails create(EndPointDetails parentDetails, TypeElement element);

        EndPointDetails create(EndPointDetails parentDetails, Method method);
    }

    private static final String MANY_POTENTIAL_BODY = "Method `%s` has more than one body parameter.";
    private static final String FORM_AND_BODY_PARAM = "Method `%s` has both a @FormParam and a body parameter. "
            + "Specify one or the other.";
    private static final String GET_WITH_BODY = "End-point method annotated with @GET or @HEAD contains illegal "
            + "parameters. Verify that `%s` contains no body or @FormParam parameters.";
    private static final String BAD_REST_ACTION = "Method `%s` returns a RestAction<> without a type argument.";

    private final Logger logger;
    private final Utils utils;

    private PathDetails path;
    private Secured secured;
    private Set<ContentType> consumes;
    private Set<ContentType> produces;
    private HttpVerb verb;
    private Collection<HttpVariable> httpVariables = Collections.emptyList();
    private Optional<HttpVariable> body = Optional.absent();
    private Type resultType;

    EndPointDetails(
            Logger logger,
            Utils utils) {
        this.logger = logger;
        this.utils = utils;
    }

    EndPointDetails(
            Logger logger,
            Utils utils,
            EndPointDetails parentDetails) {
        this.logger = logger;
        this.utils = utils;

        copyFields(parentDetails);
    }

    private void copyFields(EndPointDetails parentDetails) {
        path = parentDetails.getPath();
        secured = parentDetails.getSecured();
        consumes = copyOf(parentDetails.getConsumes());
        produces = copyOf(parentDetails.getProduces());
        httpVariables = parentDetails.getHttpVariables();
        body = parentDetails.getBody();
        resultType = parentDetails.getResultType();
    }

    void processMethod(Method method) {
        processElement(method.getElement());
        processVerb(method);
        processResult(method);
        createVariables(method);
    }

    void processElement(Element element) {
        path = path == null ? new PathDetails(element) : new PathDetails(element, path);
        secured = secured == null ? new Secured(element) : new Secured(element, secured);
        consumes = copyOf(consumes == null ? resolveConsumes(element) : resolveConsumes(element, consumes));
        produces = copyOf(produces == null ? resolveProduces(element) : resolveProduces(element, produces));
    }

    private void processVerb(Method method) {
        verb = new HttpVerbResolver(logger).resolve(method.getElement());
    }

    private void processResult(Method method) {
        String restActionName = RestAction.class.getCanonicalName();
        Type returnType = method.getReturnType();

        if (restActionName.equals(returnType.getQualifiedName())) {
            resolveRestActionResult(method);
        } else {
            Optional<Primitives> primitive = findByPrimitive(returnType.getQualifiedParameterizedName());

            if (primitive.isPresent()) {
                resultType = new Type(primitive.get().getBoxedClass().getCanonicalName());
            } else {
                resultType = returnType;
            }
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

    private void createVariables(Method method) {
        httpVariables = new ArrayList<>(httpVariables);

        for (Variable variable : method.getParameters()) {
            resolveVariable(method, variable);
        }

        httpVariables = ImmutableList.copyOf(httpVariables);

        logPotentialErrors(method);
    }

    private void resolveVariable(Method method, Variable variable) {
        HttpVariable httpVariable = new HttpVariable(logger, utils, getPath(), variable);

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

    public PathDetails getPath() {
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
