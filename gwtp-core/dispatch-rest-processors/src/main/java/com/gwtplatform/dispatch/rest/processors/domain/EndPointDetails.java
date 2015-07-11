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

package com.gwtplatform.dispatch.rest.processors.domain;

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

public class EndPointDetails implements HasImports {
    private static final Predicate<HttpVariable> BODY_PREDICATE = new Predicate<HttpVariable>() {
        @Override
        public boolean apply(HttpVariable variable) {
            return variable.isBody();
        }
    };

    private final String path;
    private final boolean secured;
    private final Set<ContentType> consumes;
    private final Set<ContentType> produces;
    private final HttpVerb verb;
    private final Collection<HttpVariable> httpParameters;
    private final Optional<HttpVariable> body;
    private final Type result;

    public EndPointDetails(
            String path,
            boolean secured,
            Set<ContentType> consumes,
            Set<ContentType> produces) {
        this(path, secured, consumes, produces, null, new ArrayList<HttpVariable>(), null);
    }

    public EndPointDetails(
            String path,
            boolean secured,
            Set<ContentType> consumes,
            Set<ContentType> produces,
            HttpVerb verb,
            Collection<HttpVariable> parameters,
            Type result) {
        this.verb = verb;
        this.path = path;
        this.secured = secured;
        this.consumes = ImmutableSet.copyOf(consumes);
        this.produces = ImmutableSet.copyOf(produces);
        this.httpParameters = removeBody(parameters);
        this.body = extractBody(parameters);
        this.result = result;
    }

    private Collection<HttpVariable> removeBody(Collection<HttpVariable> parameters) {
        return FluentIterable.from(parameters)
                .filter(not(BODY_PREDICATE))
                .toList();
    }

    private Optional<HttpVariable> extractBody(Collection<HttpVariable> parameters) {
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

    public Collection<HttpVariable> getHttpParameters() {
        return httpParameters;
    }

    public Optional<HttpVariable> getBody() {
        return body;
    }

    public Type getResult() {
        return result;
    }

    @Override
    public Collection<String> getImports() {
        FluentIterable<String> imports = FluentIterable.from(httpParameters)
                .transformAndConcat(new Function<HttpVariable, Iterable<String>>() {
                    @Override
                    public Iterable<String> apply(HttpVariable variable) {
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
