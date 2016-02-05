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

package com.gwtplatform.processors;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;
import com.google.common.collect.Sets;
import com.gwtplatform.common.client.GwtpApp;
import com.gwtplatform.processors.tools.bindings.BindingContext;
import com.gwtplatform.processors.tools.bindings.BindingsProcessors;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.outputter.Outputter;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.gwtplatform.processors.tools.GwtSourceFilter.GWTP_MODULE_OPTION;
import static com.gwtplatform.processors.tools.bindings.gin.GinModuleProcessor.META_INF_TYPE;
import static com.gwtplatform.processors.tools.logger.Logger.DEBUG_OPTION;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedOptions({DEBUG_OPTION, GWTP_MODULE_OPTION})
public class GwtpAppModuleProcessor extends AbstractProcessor {
    private static final Type MAIN_MODULE_TYPE = new Type(GwtpApp.class.getPackage().getName(), "GeneratedGwtpModule");

    private Logger logger;
    private BindingsProcessors bindingsProcessors;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        Map<String, String> options = processingEnv.getOptions();

        logger = new Logger(processingEnv.getMessager(), options);
        Utils utils = new Utils(logger, processingEnv.getTypeUtils(), processingEnv.getElementUtils(), options);
        Outputter outputter = new Outputter(logger, this, processingEnv.getFiler());
        bindingsProcessors = new BindingsProcessors(logger, utils, outputter);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Sets.newHashSet(GwtpApp.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            if (roundEnv.processingOver()) {
                bindingsProcessors.processLast();
            } else {
                ensureModuleIsCreated();

                List<String> modules = findModulesToInstall();
                installModules(modules);
            }
        } catch (Exception e) {
            logger.error().throwable(e).log("Could not read GIN modules metadata.");
        }
        return false;
    }

    private void ensureModuleIsCreated() {
        bindingsProcessors.process(new BindingContext(MAIN_MODULE_TYPE));
    }

    private List<String> findModulesToInstall() throws IOException, URISyntaxException {
        String metadataFileName = "META-INF/" + META_INF_TYPE.getSimpleName();
        List<URL> moduleFiles = Collections.list(getClass().getClassLoader().getResources(metadataFileName));

        return extractModulesToInstall(moduleFiles);
    }

    private List<String> extractModulesToInstall(List<URL> moduleFiles) throws URISyntaxException, IOException {
        List<String> modules = new ArrayList<>();
        for (URL moduleFile : moduleFiles) {
            modules.addAll(extractModulesToInstall(moduleFile.toURI()));
        }

        return modules;
    }

    private List<String> extractModulesToInstall(URI moduleFile) throws URISyntaxException, IOException {
        if (moduleFile.toString().contains("!")) {
            return extractModulesToInstallFromZip(moduleFile);
        } else {
            return extractModulesToInstall(Paths.get(moduleFile));
        }
    }

    private List<String> extractModulesToInstallFromZip(URI uri) throws IOException {
        Map<String, Object> env = new HashMap<>();
        String[] paths = uri.toString().split("!");

        try (FileSystem fileSystem = FileSystems.newFileSystem(URI.create(paths[0]), env)) {
            return extractModulesToInstall(fileSystem.getPath(paths[1]));
        }
    }

    private List<String> extractModulesToInstall(Path path) throws IOException {
        return Files.readAllLines(path, Charset.defaultCharset());
    }

    private void installModules(List<String> modules) {
        for (String module : modules) {
            bindingsProcessors.process(new BindingContext(MAIN_MODULE_TYPE, new Type(module), true));
        }
    }
}
