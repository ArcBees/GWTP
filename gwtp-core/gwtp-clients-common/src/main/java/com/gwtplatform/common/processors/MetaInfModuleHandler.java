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

package com.gwtplatform.common.processors;

import java.io.BufferedWriter;
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

import javax.tools.FileObject;

import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.outputter.OutputType;
import com.gwtplatform.processors.tools.outputter.Outputter;

public class MetaInfModuleHandler {
    private static final Type META_INF_TYPE = new Type("", "gwtp/ginModules");

    private final Logger logger;
    private final Outputter outputter;

    private BufferedWriter metaInfWriter;

    public MetaInfModuleHandler(
            Logger logger,
            Outputter outputter) {
        this.logger = logger;
        this.outputter = outputter;
    }

    public void writeLine(String line) throws IOException {
        if (metaInfWriter == null) {
            createMetaInfWriter();
        }

        metaInfWriter.write(line);
        metaInfWriter.newLine();
    }

    private void createMetaInfWriter() {
        try {
            FileObject fileObject = outputter.prepareSourceFile(META_INF_TYPE, OutputType.META_INF);
            metaInfWriter = new BufferedWriter(fileObject.openWriter());
        } catch (IOException e) {
            logger.error().throwable(e).log("Could not to create GIN modules metadata file.");
            throw new UnableToProcessException();
        }
    }

    public void closeWriter() throws IOException {
        if (metaInfWriter != null) {
            metaInfWriter.close();
        }
    }

    public List<String> readAll() throws Exception {
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
}
