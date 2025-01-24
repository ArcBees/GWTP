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

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Processor;
import javax.tools.FileObject;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Ordering;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.utils.Primitives;

import static com.google.common.base.Predicates.containsPattern;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.or;

public class Outputter {
    private static final String PROPERTIES = "/com/gwtplatform/processors/tools/velocity.properties";
    private static final String DEFAULT_MACRO_FILE = "/com/gwtplatform/processors/tools/macros.vm";
    private static final String ENCODING = "UTF-8";
    private static final SimpleDateFormat PROCESSING_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private final Logger logger;
    private final Type processor;
    private final Filer filer;
    private final Set<String> macroFiles;

    private VelocityEngine velocityEngine;

    public Outputter(
            Logger logger,
            Processor processor,
            Filer filer,
            String... macroFiles) {
        this(logger, processor, filer, List.of(macroFiles));
    }

    public Outputter(
            Logger logger,
            Processor processor,
            Filer filer,
            Collection<String> macroFiles) {
        this.logger = logger;
        this.processor = new Type(processor.getClass());
        this.filer = filer;
        this.macroFiles = FluentIterable.from(macroFiles).append(DEFAULT_MACRO_FILE).toSet();
    }

    public OutputBuilder configure(String templateFile) {
        return new OutputBuilder(this, processor, templateFile);
    }

    CodeSnippet parse(OutputBuilder builder) {
        try (Writer writer = new StringWriter()) {
            merge(builder, writer);

            String code = writer.toString();
            Collection<String> imports = builder.getImports();

            return new CodeSnippet(code, imports);
        } catch (IOException e) {
            logger.error().throwable(e).log("Can not parse `%s`.", builder.getErrorLogParameter().get());
            throw new UnableToProcessException();
        }
    }

    void writeSource(OutputBuilder builder) {
        try (Writer writer = prepareSourceFile(builder).openWriter()) {
            merge(builder, writer);
        } catch (IOException e) {
            logger.error().throwable(e).log("Can not write `%s`.", builder.getErrorLogParameter().get());
            throw new UnableToProcessException();
        }
    }

    private FileObject prepareSourceFile(OutputBuilder builder) throws IOException {
        Optional<FileObject> sourceFile = builder.getSourceFile();
        if (sourceFile.isPresent()) {
            return sourceFile.get();
        } else {
            return prepareSourceFile(builder.getType().get(), builder.getOutputType().get());
        }
    }

    public FileObject prepareSourceFile(Type type) {
        return prepareSourceFile(type, OutputType.GWT);
    }

    public FileObject prepareSourceFile(Type type, OutputType outputType) {
        try {
            return outputType.createFileObject(logger, filer, type);
        } catch (IOException e) {
            logger.error().throwable(e).log("Can not create source file `%s`.", type.getQualifiedName());
            throw new UnableToProcessException();
        }
    }

    private void merge(OutputBuilder builder, Writer writer) throws IOException {
        VelocityContext context = builder.getContext();
        Optional<Type> type = builder.getType();
        Collection<String> imports = cleanupImports(builder.getImports(), type);

        if (type.isPresent()) {
            context.put("impl", type.get());
        }

        context.put("processor", builder.getProcessor());
        context.put("processingDate", PROCESSING_DATE_FORMAT.format(new Date()));
        context.put("imports", imports);

        getEngine().mergeTemplate(builder.getTemplateFile(), ENCODING, context, writer);
    }

    private Collection<String> cleanupImports(Collection<String> imports, Optional<Type> type) {
        List<Predicate<CharSequence>> predicates = new ArrayList<>();
        predicates.add(Predicates.<CharSequence>isNull());
        predicates.add(Primitives.IS_PRIMITIVE_PREDICATE);
        predicates.add(containsPattern("^java\\.lang\\.[^.]+$"));

        if (type.isPresent()) {
            String packageName = type.get().getPackageName();
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
            properties.put("velocimacro.library", Joiner.on(",").join(macroFiles));

            velocityEngine = new VelocityEngine(properties);
        }

        return velocityEngine;
    }
}
