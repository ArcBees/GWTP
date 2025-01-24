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

package com.gwtplatform.processors.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.utils.Utils;

public class GwtSourceFilter {
    public static final String GWTP_MODULE_OPTION = "gwtp.module";

    private static final String MODULE_EXTENSION = ".gwt.xml";
    private static final String SOURCE_TAG = "source";
    private static final String PATH_ATTRIBUTE = "path";
    private static final String INHERITS_TAG = "inherits";
    private static final String NAME_ATTRIBUTE = "name";

    private static final DocumentBuilder documentBuilder = createDocumentBuilder();

    private final Logger logger;
    private final Utils utils;
    private final String[] moduleNames;
    private final Set<String> parsedModules;
    private final Set<String> sourcePackages;

    private boolean initialized;

    public GwtSourceFilter(
            Logger logger,
            Utils utils,
            String... moduleNames) {
        this.logger = logger;
        this.utils = utils;
        this.moduleNames = moduleNames;
        this.parsedModules = new HashSet<>();
        this.sourcePackages = new LinkedHashSet<>();
    }

    private static DocumentBuilder createDocumentBuilder() {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            documentBuilder.setEntityResolver((publicId, systemId) -> {
                // Disable the loading of DTDs as it is a huge slow down during compilation.
                return new InputSource(new StringReader(""));
            });

            return documentBuilder;
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public String getApplicationPackage() {
        return getSourcePackages().stream().findFirst().orElse("");
    }

    public Set<String> getSourcePackages() {
        if (!initialized) {
            resolveSourcePackages();
            initialized = true;
        }

        return new LinkedHashSet<>(sourcePackages);
    }

    private void resolveSourcePackages() {
        if (moduleNames != null) {
            addModules(moduleNames);
        }
        loadFromOptions();

        if (sourcePackages.isEmpty()) {
            logger.warning("No source packages found. Make sure your GWT module contains <source> tags.");
        }
    }

    private void loadFromOptions() {
        String modules = utils.getOption(GWTP_MODULE_OPTION);

        if (modules == null) {
            logger.warning("Add `-A%s=com.domain.YourModule` to your compiler options to enable GWTP "
                    + "annotation processors.", GWTP_MODULE_OPTION);
        } else {
            addModules(modules.split(","));
        }
    }

    public <E extends Element> Set<E> filterElements(Set<E> elements) {
        Set<E> filteredElements = elements.stream()
                .filter(element -> elementIsPartOfGwtSource(element))
                .collect(Collectors.toSet());

        return filteredElements;
    }

    public boolean elementIsPartOfGwtSource(Element element) {
        PackageElement packageElement = utils.getElements().getPackageOf(element);
        String packageName = packageElement.getQualifiedName() + ".";

        for (String sourcePackage : getSourcePackages()) {
            if (packageName.startsWith(sourcePackage + ".")) {
                return true;
            }
        }

        return false;
    }

    public void addModules(String[] moduleNames) {
        addModules(Arrays.asList(moduleNames));
    }

    public void addModules(Collection<String> moduleNames) {
        for (String moduleName : moduleNames) {
            addModule(moduleName);
        }
    }

    public void addModule(String moduleName) {
        try {
            loadModule(moduleName);
        } catch (IOException | SAXException e) {
            logger.mandatoryWarning().throwable(e).log("Unable to parse module for source paths '%s'.", moduleName);
        }
    }

    private void loadModule(String moduleName) throws IOException, SAXException {
        if (moduleName == null || moduleName.isEmpty() || parsedModules.contains(moduleName)) {
            return;
        }

        parsedModules.add(moduleName);

        String moduleFileName = "/" + moduleName.replace('.', '/') + MODULE_EXTENSION;
        String moduleBasePackage = moduleFileName.substring(1, moduleFileName.lastIndexOf('/')).replace('/', '.');

        try (InputStream moduleFile = GwtSourceFilter.class.getResourceAsStream(moduleFileName)) {
            if (moduleFile != null) {
                loadModule(moduleBasePackage, moduleFile);
            } else {
                logger.warning("Unable to find module file '%s' in source tree.", moduleName);
            }
        }
    }

    private void loadModule(String moduleBasePackage, InputStream moduleFile) throws IOException, SAXException {
        Document document = parseModule(moduleFile);

        loadSourcePackages(moduleBasePackage, document);
        loadInheritedModules(document);
    }

    private Document parseModule(InputStream moduleFile) throws SAXException, IOException {
        Document document = documentBuilder.parse(moduleFile);
        document.getDocumentElement().normalize();

        return document;
    }

    private void loadSourcePackages(String moduleBasePackage, Document document) {
        NodeList sourceNodes = document.getElementsByTagName(SOURCE_TAG);

        for (int i = 0; i < sourceNodes.getLength(); ++i) {
            loadSourcePackage(moduleBasePackage, sourceNodes.item(i));
        }
    }

    private void loadSourcePackage(String moduleBasePackage, Node sourceNode) {
        NamedNodeMap sourceAttributes = sourceNode.getAttributes();
        Node pathAttribute = sourceAttributes.getNamedItem(PATH_ATTRIBUTE);
        String path = pathAttribute.getNodeValue();

        String sourcePackage = moduleBasePackage;
        if (!path.isEmpty()) {
            sourcePackage += "." + path.replace('/', '.');
        }

        sourcePackages.add(sourcePackage);
    }

    private void loadInheritedModules(Document document) throws IOException, SAXException {
        NodeList inheritsNodes = document.getElementsByTagName(INHERITS_TAG);
        for (int i = 0; i < inheritsNodes.getLength(); ++i) {
            loadInheritedModule(inheritsNodes.item(i));
        }
    }

    private void loadInheritedModule(Node inheritsNode) throws IOException, SAXException {
        NamedNodeMap inheritsAttributes = inheritsNode.getAttributes();
        Node nameAttribute = inheritsAttributes.getNamedItem(NAME_ATTRIBUTE);
        String name = nameAttribute.getNodeValue();

        loadModule(name);
    }
}
