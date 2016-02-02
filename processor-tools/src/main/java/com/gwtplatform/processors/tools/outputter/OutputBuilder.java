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

package com.gwtplatform.processors.tools.outputter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import javax.tools.FileObject;

import org.apache.velocity.VelocityContext;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.gwtplatform.processors.tools.domain.HasImports;
import com.gwtplatform.processors.tools.domain.Type;

import static com.gwtplatform.processors.tools.domain.HasImports.EXTRACT_IMPORTS_FUNCTION;

public class OutputBuilder {
    private final Outputter outputter;
    private final Type processor;
    private final String templateFile;
    private final VelocityContext context;
    private final Collection<String> imports;

    private Optional<FileObject> sourceFile;
    private Optional<Type> type;
    private Optional<String> errorLogParameter;

    OutputBuilder(
            Outputter outputter,
            Type processor,
            String templateFile) {
        this.outputter = outputter;
        this.processor = processor;
        this.templateFile = templateFile;
        this.context = new VelocityContext();
        this.imports = new HashSet<>();
        this.sourceFile = Optional.absent();
        this.type = Optional.absent();
        this.errorLogParameter = Optional.absent();
    }

    public OutputBuilder withParams(Map<String, Object> params) {
        for (Entry<String, Object> param : params.entrySet()) {
            withParam(param.getKey(), param.getValue());
        }

        return this;
    }

    public OutputBuilder withParam(String key, HasImports value) {
        context.put(key, value);

        return withImports(value.getImports());
    }

    public OutputBuilder withParam(String key, Iterable<? extends HasImports> value) {
        context.put(key, value);

        return withImports(FluentIterable.from(value).transformAndConcat(EXTRACT_IMPORTS_FUNCTION).toList());
    }

    public OutputBuilder withParam(String key, Object value) {
        context.put(key, value);
        return this;
    }

    public OutputBuilder withImports(Collection<String> imports) {
        this.imports.addAll(imports);
        return this;
    }

    public OutputBuilder withImport(String anImport) {
        imports.add(anImport);
        return this;
    }

    public OutputBuilder withErrorLogParameter(String errorLogParameter) {
        this.errorLogParameter = Optional.of(errorLogParameter);
        return this;
    }

    public void writeTo(Type type) {
        writeTo(type, null);
    }

    public void writeTo(Type type, FileObject sourceFile) {
        this.type = Optional.of(type);
        this.sourceFile = Optional.fromNullable(sourceFile);

        if (!errorLogParameter.isPresent()) {
            errorLogParameter = Optional.of(type.getQualifiedName());
        }

        outputter.writeSource(this);
    }

    public CodeSnippet parse() {
        return outputter.parse(this);
    }

    Type getProcessor() {
        return processor;
    }

    String getTemplateFile() {
        return templateFile;
    }

    VelocityContext getContext() {
        return context;
    }

    Collection<String> getImports() {
        return imports;
    }

    Optional<FileObject> getSourceFile() {
        return sourceFile;
    }

    Optional<Type> getType() {
        return type;
    }

    Optional<String> getErrorLogParameter() {
        return errorLogParameter;
    }
}
