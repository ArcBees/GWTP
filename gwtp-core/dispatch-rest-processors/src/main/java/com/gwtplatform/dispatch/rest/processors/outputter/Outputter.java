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

package com.gwtplatform.dispatch.rest.processors.outputter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Ordering;
import com.gwtplatform.dispatch.rest.processors.UnableToProcessException;
import com.gwtplatform.dispatch.rest.processors.definitions.TypeDefinition;
import com.gwtplatform.dispatch.rest.processors.logger.Logger;

import static com.google.common.base.Predicates.containsPattern;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.or;

public class Outputter {
    private static final String PROPERTIES = "/com/gwtplatform/dispatch/rest/processors/velocity.properties";
    private static final String ENCODING = "UTF-8";

    private final Logger logger;
    private final TypeDefinition processorDefinition;
    private final Filer filer;

    private VelocityEngine velocityEngine;

    public Outputter(
            Logger logger,
            TypeDefinition processorDefinition,
            Filer filer) {
        this.logger = logger;
        this.processorDefinition = processorDefinition;
        this.filer = filer;
    }

    public OutputBuilder withTemplateFile(String templateFile) {
        return new OutputBuilder(this, processorDefinition, templateFile);
    }

    String parse(OutputBuilder builder) {
        try (Writer writer = new StringWriter()) {
            merge(builder, writer);
            return writer.toString();
        } catch (IOException e) {
            logger.error("Can not parse `%s`.", e, builder.getErrorLogParameter());
            throw new UnableToProcessException();
        }
    }

    void writeSource(OutputBuilder builder) {
        TypeDefinition typeDefinition = builder.getTypeDefinition().get();
        JavaFileObject classFile = createSourceFile(typeDefinition);

        try (Writer writer = classFile.openWriter()) {
            merge(builder, writer);
        } catch (IOException e) {
            logger.error("Can not write `%s`.", e, typeDefinition.getQualifiedName());
            throw new UnableToProcessException();
        }
    }

    private JavaFileObject createSourceFile(TypeDefinition typeDefinition) {
        try {
            return filer.createSourceFile(typeDefinition.getQualifiedName());
        } catch (IOException e) {
            logger.error("Can not create source file `%s`.", e, typeDefinition.getQualifiedName());
            throw new UnableToProcessException();
        }
    }

    private void merge(OutputBuilder builder, Writer writer) throws IOException {
        VelocityContext context = builder.getContext();
        Optional<TypeDefinition> typeDefinition = builder.getTypeDefinition();
        Collection<String> imports = cleanupImports(builder.getImports(), typeDefinition);

        if (typeDefinition.isPresent()) {
            context.put("impl", typeDefinition.get());
        }

        context.put("processor", builder.getProcessorDefinition());
        context.put("imports", imports);

        getEngine().getTemplate(builder.getTemplateFile(), ENCODING)
                .merge(context, writer);
    }

    private Collection<String> cleanupImports(Collection<String> imports, Optional<TypeDefinition> typeDefinition) {
        List<Predicate<CharSequence>> predicates = new ArrayList<>();
        predicates.add(Predicates.<CharSequence>isNull());
        predicates.add(containsPattern("^java\\.lang\\.[^.]+$"));

        if (typeDefinition.isPresent()) {
            String packageName = typeDefinition.get().getPackageName();
            predicates.add(containsPattern("^" + packageName.replace(".", "\\.") + "\\.[^.]+$"));
        }

        return FluentIterable.from(imports)
                .filter(not(or(predicates)))
                .toSortedSet(Ordering.natural());
    }

    private VelocityEngine getEngine() throws IOException {
        if (velocityEngine == null) {
            Properties properties = new Properties();
            properties.load(getClass().getResourceAsStream(PROPERTIES));

            velocityEngine = new VelocityEngine(properties);
        }

        return velocityEngine;
    }
}
