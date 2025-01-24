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

package com.gwtplatform.common.processors.bundles;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.logger.Logger;

import static java.nio.file.Files.newInputStream;
import static com.google.common.io.CharStreams.readLines;
import static com.google.common.io.Files.getNameWithoutExtension;

class ProviderBundleMetaInfReader {
    private final Logger logger;

    private Map<String, List<Type>> bundles;

    ProviderBundleMetaInfReader(Logger logger) {
        this.logger = logger;
    }

    public Map<String, List<Type>> getBundles() {
        if (bundles == null) {
            bundles = new HashMap<>();
            loadBundlesFromMetaInf();
        }

        return bundles;
    }

    private void loadBundlesFromMetaInf() {
        try {
            Enumeration<URL> resources = getClass().getClassLoader().getResources("META-INF/gwtp/bundles/");

            while (resources.hasMoreElements()) {
                loadBundlesFromUri(resources.nextElement().toURI());
            }
        } catch (IOException | URISyntaxException e) {
            logger.error().throwable(e).log("Unable to read bundles meta data.");
        }
    }

    private void loadBundlesFromUri(URI uri) throws IOException {
        String scheme = uri.getScheme();

        if ("jar".equals(scheme)) {
            loadBundlesFromJar(uri);
        } else if ("file".equals(scheme)) {
            loadBundlesFromDirectory(uri);
        } else {
            logger.warning("Bundle meta data found in '%s', but the protocol is not supported.", uri);
        }
    }

    private void loadBundlesFromJar(URI uri) throws IOException {
        String jarFilePath = uri.toString().split("!")[0].replace("jar:file:", "");

        try (ZipFile zipFile = new ZipFile(jarFilePath)) {
            loadBundlesFromZip(zipFile);
        }
    }

    private void loadBundlesFromZip(ZipFile file) throws IOException {
        Enumeration<? extends ZipEntry> entries = file.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (!entry.isDirectory() && entry.getName().startsWith("META-INF/gwtp/bundles")) {
                loadBundlesFromZipEntry(file, entry);
            }
        }
    }

    private void loadBundlesFromZipEntry(ZipFile file, ZipEntry entry) throws IOException {
        String bundleName = getNameWithoutExtension(entry.getName());
        InputStream inputStream = file.getInputStream(entry);
        loadBundlesFromStream(bundleName, inputStream);
    }

    private void loadBundlesFromDirectory(URI uri) throws IOException {
        Files.walkFileTree(Paths.get(uri), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                loadBundlesFromStream(file.toFile().getName(), newInputStream(file));
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void loadBundlesFromStream(String bundleName, InputStream stream) throws IOException {
        List<Type> bundleCollection = getOrCreateBundleCollection(bundleName);

        bundleCollection.addAll(
                readLines(new InputStreamReader(stream))
                        .stream()
                        .map(Type::new)
                        .collect(Collectors.toList()));
    }

    private List<Type> getOrCreateBundleCollection(String bundleName) {
        List<Type> namedBundleCollection;
        if (!bundles.containsKey(bundleName)) {
            namedBundleCollection = new ArrayList<>();
            bundles.put(bundleName, namedBundleCollection);
        } else {
            namedBundleCollection = bundles.get(bundleName);
        }

        return namedBundleCollection;
    }
}
