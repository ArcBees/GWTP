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

package com.gwtplatform.dispatch.rest.processors.definitions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.gwtplatform.dispatch.rest.rebind.HttpVerb;
import com.gwtplatform.dispatch.rest.shared.ContentType;
import com.gwtplatform.dispatch.rest.shared.HttpParameter;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.notNull;

public class EndPointDefinition implements HasImports {
    private static final Predicate<HttpVariableDefinition> BODY_PREDICATE = new Predicate<HttpVariableDefinition>() {
        @Override
        public boolean apply(HttpVariableDefinition variable) {
            return variable.isBody();
        }
    };

    private final String path;
    private final boolean secured;
    private final Set<ContentType> consumes;
    private final Set<ContentType> produces;
    private final HttpVerb verb;
    private final Collection<HttpVariableDefinition> httpParameters;
    private final Optional<HttpVariableDefinition> body;
    private final TypeDefinition result;

    public EndPointDefinition(
            String path,
            boolean secured,
            Set<ContentType> consumes,
            Set<ContentType> produces) {
        this(path, secured, consumes, produces, null, new ArrayList<HttpVariableDefinition>(), null);
    }

    public EndPointDefinition(
            String path,
            boolean secured,
            Set<ContentType> consumes,
            Set<ContentType> produces,
            HttpVerb verb,
            Collection<HttpVariableDefinition> parameters,
            TypeDefinition result) {
        this.verb = verb;
        this.path = path;
        this.secured = secured;
        this.consumes = ImmutableSet.copyOf(consumes);
        this.produces = ImmutableSet.copyOf(produces);
        this.httpParameters = removeBody(parameters);
        this.body = extractBody(parameters);
        this.result = result;
    }

    private Collection<HttpVariableDefinition> removeBody(Collection<HttpVariableDefinition> parameters) {
        return FluentIterable.from(parameters)
                .filter(not(BODY_PREDICATE))
                .toList();
    }

    private Optional<HttpVariableDefinition> extractBody(Collection<HttpVariableDefinition> parameters) {
        return FluentIterable.from(parameters)
                .filter(BODY_PREDICATE)
                .first();
    }

    public HttpVerb getVerb() {
        return verb;
    }

    public String getPath() {
        return path;
    }

    public boolean isSecured() {
        return secured;
    }

    public Set<ContentType> getConsumes() {
        return consumes;
    }

    public Set<ContentType> getProduces() {
        return produces;
    }

    public Collection<HttpVariableDefinition> getHttpParameters() {
        return httpParameters;
    }

    public Optional<HttpVariableDefinition> getBody() {
        return body;
    }

    public TypeDefinition getResult() {
        return result;
    }

    @Override
    public Collection<String> getImports() {
        FluentIterable<String> imports = FluentIterable.from(httpParameters)
                .transformAndConcat(new Function<HttpVariableDefinition, Iterable<String>>() {
                    @Override
                    public Iterable<String> apply(HttpVariableDefinition variable) {
                        return variable.getImports();
                    }
                })
                .append(result.getImports());

        if (body.isPresent()) {
            imports = imports.append(body.get().getImports());
        }
        if (!httpParameters.isEmpty()) {
            imports = imports.append(HttpParameter.Type.class.getCanonicalName());
        }

        return imports.filter(and(notNull(), not(equalTo(""))))
                .toList();
    }
}
