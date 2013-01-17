/**
 * Copyright 2011 ArcBees Inc.
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

package com.gwtplatform.mvp.rebind;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gwt.core.ext.PropertyOracle;
import com.google.gwt.dev.javac.GeneratedUnit;
import com.google.gwt.dev.javac.StandardGeneratorContext;
import com.google.gwt.dev.util.UnitTestTreeLogger;
import com.gwtplatform.mvp.client.ApplicationController;
import com.gwtplatform.mvp.client.Bootstrapper;
import com.gwtplatform.mvp.client.PreBootstrapper;
import com.gwtplatform.mvp.client.annotations.Bootstrap;
import com.gwtplatform.mvp.client.annotations.PreBootstrap;
import com.gwtplatform.mvp.rebind.util.GeneratorTestBase;
import com.gwtplatform.mvp.rebind.util.ResourceBase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ApplicationControllerGeneratorTest extends GeneratorTestBase {
    private static final String FINAL_NAME = ApplicationController.class.getName() + ApplicationControllerGenerator.SUFFIX;

    private static String CONTROLLER_DEFAULT,
            CONTROLLER_CUSTOM_BOOTSTRAPPER,
            CONTROLLER_PREBOOTSTRAPPER,
            GINJECTOR_FOO,
            GINJECTOR_FOO_BAR,
            GINJECTOR_FOO_CUSTOM_BOOTSTRAPPER,
            GINJECTOR_SINGLE_EXTENSION,
            GINJECTOR_MULTIPLE_EXTENSIONS,
            GINJECTOR_WITH_PRESENTERS,
            GINJECTOR_WITH_PRESENTER_BUNDLES,
            GINJECTOR_WITH_DEFAULTGATEKEEPER,
            FOO_PROVIDER_BUNDLE;

    private UnitTestTreeLogger logger;

    private ApplicationControllerGenerator generator;

    @BeforeClass
    public static void setUpClass() throws Exception {
        CONTROLLER_DEFAULT = Files.toString(new File(ApplicationControllerGeneratorTest.class
                .getResource("ApplicationControllerDefault.txt").toURI()), Charsets.UTF_8);
        CONTROLLER_CUSTOM_BOOTSTRAPPER = Files.toString(new File(ApplicationControllerGeneratorTest.class
                .getResource("ApplicationControllerCustomBootstrapper.txt").toURI()), Charsets.UTF_8);
        CONTROLLER_PREBOOTSTRAPPER = Files.toString(new File(ApplicationControllerGeneratorTest.class
                .getResource("ApplicationControllerWithPreBootstrapper.txt").toURI()), Charsets.UTF_8);
        GINJECTOR_FOO = Files.toString(new File(ApplicationControllerGeneratorTest.class
                .getResource("GinjectorFoo.txt").toURI()), Charsets.UTF_8);
        GINJECTOR_FOO_BAR = Files.toString(new File(ApplicationControllerGeneratorTest.class
                .getResource("GinjectorFooBar.txt").toURI()), Charsets.UTF_8);
        GINJECTOR_FOO_CUSTOM_BOOTSTRAPPER = Files.toString(new File(ApplicationControllerGeneratorTest.class
                .getResource("GinjectorFooCustomBootstrapper.txt").toURI()), Charsets.UTF_8);
        GINJECTOR_SINGLE_EXTENSION = Files.toString(new File(ApplicationControllerGeneratorTest.class
                .getResource("GinjectorSingleExtension.txt").toURI()), Charsets.UTF_8);
        GINJECTOR_MULTIPLE_EXTENSIONS = Files.toString(new File(ApplicationControllerGeneratorTest.class
                .getResource("GinjectorMultipleExtensions.txt").toURI()), Charsets.UTF_8);
        GINJECTOR_MULTIPLE_EXTENSIONS = Files.toString(new File(ApplicationControllerGeneratorTest.class
                .getResource("GinjectorMultipleExtensions.txt").toURI()), Charsets.UTF_8);
        GINJECTOR_WITH_PRESENTERS = Files.toString(new File(ApplicationControllerGeneratorTest.class
                .getResource("GinjectorWithPresenters.txt").toURI()), Charsets.UTF_8);
        GINJECTOR_WITH_PRESENTER_BUNDLES = Files.toString(new File(ApplicationControllerGeneratorTest.class
                .getResource("GinjectorWithPresenterBundles.txt").toURI()), Charsets.UTF_8);
        FOO_PROVIDER_BUNDLE = Files.toString(new File(ApplicationControllerGeneratorTest.class
                .getResource("FooProviderBundle.txt").toURI()), Charsets.UTF_8);
        GINJECTOR_WITH_DEFAULTGATEKEEPER = Files.toString(new File(ApplicationControllerGeneratorTest.class
                .getResource("GinjectorWithDefaultGatekeeper.txt").toURI()), Charsets.UTF_8);
    }

    @Before
    public void setUp() throws Exception {
        generator = new ApplicationControllerGenerator();
        logger = createDefaultUnitTestTreeLogger();
    }

    @Test
    public void testFooModuleOnly() throws Exception {
        StandardGeneratorContext context = createGeneratorContext(createDefaultOracle(), gwtpResourcesWith());

        assertEquals(FINAL_NAME, generator.generate(logger, context, ApplicationController.class.getName()));

        GeneratedUnit controllerUnit = context.getGeneratedUnitMap().get(FINAL_NAME);
        GeneratedUnit ginjectorUnit = context.getGeneratedUnitMap().get(GinjectorGenerator.DEFAULT_FQ_NAME);

        assertNotNull(controllerUnit);
        assertNotNull(ginjectorUnit);

        assertEquals(CONTROLLER_DEFAULT, controllerUnit.getSource());
        assertEquals(GINJECTOR_FOO, ginjectorUnit.getSource());
    }

    @Test
    public void testFooAndBarModule() throws Exception {
        PropertyOracle propOracle = createPropertyOracleBuilder()
                .with(AbstractGenerator.GIN_GINJECTOR_EXTENSION, true)
                .with(AbstractGenerator.GIN_GINJECTOR_MODULES, true, ResourceBase.FOOMODULE.getTypeName(),
                        ResourceBase.BARMODULE.getTypeName())
                .build();

        StandardGeneratorContext context = createGeneratorContext(propOracle, gwtpResourcesWith());

        assertEquals(FINAL_NAME, generator.generate(logger, context, ApplicationController.class.getName()));

        GeneratedUnit controllerUnit = context.getGeneratedUnitMap().get(FINAL_NAME);
        GeneratedUnit ginjectorUnit = context.getGeneratedUnitMap().get(GinjectorGenerator.DEFAULT_FQ_NAME);

        assertNotNull(controllerUnit);
        assertNotNull(ginjectorUnit);

        assertEquals(CONTROLLER_DEFAULT, controllerUnit.getSource());
        assertEquals(GINJECTOR_FOO_BAR, ginjectorUnit.getSource());
    }

    @Test
    public void testSingleGinjectorExtension() throws Exception {
        PropertyOracle propOracle = createPropertyOracleBuilder()
                .with(AbstractGenerator.GIN_GINJECTOR_EXTENSION, true,
                        ResourceBase.GINJECTOREXTENSION1.getTypeName())
                .with(AbstractGenerator.GIN_GINJECTOR_MODULES, true, ResourceBase.FOOMODULE.getTypeName())
                .build();

        StandardGeneratorContext context = createGeneratorContext(propOracle, gwtpResourcesWith());

        assertEquals(FINAL_NAME, generator.generate(logger, context, ApplicationController.class.getName()));

        GeneratedUnit controllerUnit = context.getGeneratedUnitMap().get(FINAL_NAME);
        GeneratedUnit ginjectorUnit = context.getGeneratedUnitMap().get(GinjectorGenerator.DEFAULT_FQ_NAME);

        assertNotNull(controllerUnit);
        assertNotNull(ginjectorUnit);

        assertEquals(CONTROLLER_DEFAULT, controllerUnit.getSource());
        assertEquals(GINJECTOR_SINGLE_EXTENSION, ginjectorUnit.getSource());
    }

    @Test
    public void testMultipleGinjectorExtensions() throws Exception {
        PropertyOracle propOracle = createPropertyOracleBuilder()
                .with(AbstractGenerator.GIN_GINJECTOR_EXTENSION, true,
                        ResourceBase.GINJECTOREXTENSION1.getTypeName(),
                        ResourceBase.GINJECTOREXTENSION2.getTypeName())
                .with(AbstractGenerator.GIN_GINJECTOR_MODULES, true, ResourceBase.FOOMODULE.getTypeName())
                .build();

        StandardGeneratorContext context = createGeneratorContext(propOracle, gwtpResourcesWith());

        assertEquals(FINAL_NAME, generator.generate(logger, context, ApplicationController.class.getName()));

        GeneratedUnit controllerUnit = context.getGeneratedUnitMap().get(FINAL_NAME);
        GeneratedUnit ginjectorUnit = context.getGeneratedUnitMap().get(GinjectorGenerator.DEFAULT_FQ_NAME);

        assertNotNull(controllerUnit);
        assertNotNull(ginjectorUnit);

        assertEquals(CONTROLLER_DEFAULT, controllerUnit.getSource());
        assertEquals(GINJECTOR_MULTIPLE_EXTENSIONS, ginjectorUnit.getSource());
    }

    @Test
    public void testMultipleGinjectorExtensionsWithSameMethodnameShouldWork() throws Exception {
        PropertyOracle propOracle = createPropertyOracleBuilder()
                .with(AbstractGenerator.GIN_GINJECTOR_EXTENSION, true,
                        ResourceBase.GINJECTOREXTENSION1.getTypeName(),
                        ResourceBase.GINJECTOREXTENSION2.getTypeName(),
                        ResourceBase.GINJECTOREXTENSION3.getTypeName())
                .with(AbstractGenerator.GIN_GINJECTOR_MODULES, true, ResourceBase.FOOMODULE.getTypeName())
                .build();

        StandardGeneratorContext context = createGeneratorContext(propOracle, gwtpResourcesWith());

        assertEquals(FINAL_NAME, generator.generate(logger, context, ApplicationController.class.getName()));
    }

    @Test
    public void testWithCustomBootstrapper() throws Exception {
        StandardGeneratorContext context = createGeneratorContext(createDefaultOracle(), gwtpResourcesWith(
                ResourceBase.CUSTOMBOOTSTRAPPER1));

        assertEquals(FINAL_NAME, generator.generate(logger, context, ApplicationController.class.getName()));

        GeneratedUnit controllerUnit = context.getGeneratedUnitMap().get(FINAL_NAME);
        GeneratedUnit ginjectorUnit = context.getGeneratedUnitMap().get(GinjectorGenerator.DEFAULT_FQ_NAME);

        assertNotNull(controllerUnit);
        assertNotNull(ginjectorUnit);

        assertEquals(CONTROLLER_CUSTOM_BOOTSTRAPPER, controllerUnit.getSource());
        assertEquals(GINJECTOR_FOO_CUSTOM_BOOTSTRAPPER, ginjectorUnit.getSource());
    }

    @Test
    public void shouldFailWithTwoBootstrappers() throws Exception {
        UnitTestTreeLogger.Builder loggerBuilder = createUnitTestTreeLoggerBuilder();
        loggerBuilder.expectError(String.format(ApplicationControllerGenerator.TOO_MANY_BOOTSTRAPPER_FOUND,
                Bootstrapper.class.getSimpleName(), Bootstrapper.class.getSimpleName(),
                Bootstrap.class.getSimpleName()), null);
        logger = loggerBuilder.createLogger();

        StandardGeneratorContext context = createGeneratorContext(createDefaultOracle(), gwtpResourcesWith(
                ResourceBase.CUSTOMBOOTSTRAPPER1, ResourceBase.CUSTOMBOOTSTRAPPER2));

        try {
            generator.generate(logger, context, ApplicationController.class.getName());
        } catch (Exception e) {
            logger.assertLogEntriesContainExpected();
        }
    }

    @Test
    public void shouldFailWithIllegalBootstrapper() throws Exception {
        UnitTestTreeLogger.Builder loggerBuilder = createUnitTestTreeLoggerBuilder();
        loggerBuilder.expectError(String.format(ApplicationControllerGenerator.DOES_NOT_EXTEND_BOOTSTRAPPER,
                Bootstrapper.class.getSimpleName(), Bootstrapper.class.getSimpleName()), null);
        logger = loggerBuilder.createLogger();

        StandardGeneratorContext context = createGeneratorContext(createDefaultOracle(), gwtpResourcesWith(
                ResourceBase.CUSTOMBOOTSTRAPPER3));

        try {
            generator.generate(logger, context, ApplicationController.class.getName());
        } catch (Exception e) {
            logger.assertLogEntriesContainExpected();
        }
    }

    @Test
    public void testWithPreBootstrapper() throws Exception {
        StandardGeneratorContext context = createGeneratorContext(createDefaultOracle(), gwtpResourcesWith(
                ResourceBase.CUSTOMPREBOOTSTRAPPER1));

        assertEquals(FINAL_NAME, generator.generate(logger, context, ApplicationController.class.getName()));

        GeneratedUnit controllerUnit = context.getGeneratedUnitMap().get(FINAL_NAME);
        GeneratedUnit ginjectorUnit = context.getGeneratedUnitMap().get(GinjectorGenerator.DEFAULT_FQ_NAME);

        assertNotNull(controllerUnit);
        assertNotNull(ginjectorUnit);

        assertEquals(CONTROLLER_PREBOOTSTRAPPER, controllerUnit.getSource());
        assertEquals(GINJECTOR_FOO, ginjectorUnit.getSource());
    }

    @Test
    public void shouldFailWithTwoPreBootstrappers() throws Exception {
        UnitTestTreeLogger.Builder loggerBuilder = createUnitTestTreeLoggerBuilder();
        loggerBuilder.expectError(String.format(ApplicationControllerGenerator.TOO_MANY_BOOTSTRAPPER_FOUND,
                PreBootstrapper.class.getSimpleName(), PreBootstrapper.class.getSimpleName(),
                PreBootstrap.class.getSimpleName()), null);
        logger = loggerBuilder.createLogger();

        StandardGeneratorContext context = createGeneratorContext(createDefaultOracle(), gwtpResourcesWith(
                ResourceBase.CUSTOMPREBOOTSTRAPPER1, ResourceBase.CUSTOMPREBOOTSTRAPPER2));

        try {
            generator.generate(logger, context, ApplicationController.class.getName());
        } catch (Exception e) {
            logger.assertLogEntriesContainExpected();
        }
    }

    @Test
    public void shouldFailWithIllegalPreBootstrapper() throws Exception {
        UnitTestTreeLogger.Builder loggerBuilder = createUnitTestTreeLoggerBuilder();
        loggerBuilder.expectError(String.format(ApplicationControllerGenerator.DOES_NOT_EXTEND_BOOTSTRAPPER,
                PreBootstrapper.class.getSimpleName(), PreBootstrapper.class.getSimpleName()), null);
        logger = loggerBuilder.createLogger();

        StandardGeneratorContext context = createGeneratorContext(createDefaultOracle(), gwtpResourcesWith(
                ResourceBase.CUSTOMPREBOOTSTRAPPER3));

        try {
            generator.generate(logger, context, ApplicationController.class.getName());
        } catch (Exception e) {
            logger.assertLogEntriesContainExpected();
        }
    }

    @Test
    // with prebootstrapper, custom bootstrapper and multiple modules/extensions
    public void testComplexExample() throws Exception {
        PropertyOracle propOracle = createPropertyOracleBuilder()
                .with(AbstractGenerator.GIN_GINJECTOR_EXTENSION, true,
                        ResourceBase.GINJECTOREXTENSION1.getTypeName(),
                        ResourceBase.GINJECTOREXTENSION2.getTypeName())
                .with(AbstractGenerator.GIN_GINJECTOR_MODULES, true,
                        ResourceBase.FOOMODULE.getTypeName(),
                        ResourceBase.BARMODULE.getTypeName())
                .build();

        StandardGeneratorContext context = createGeneratorContext(propOracle, gwtpResourcesWith(
                ResourceBase.CUSTOMBOOTSTRAPPER1, ResourceBase.CUSTOMPREBOOTSTRAPPER1));

        assertNotNull(generator.generate(logger, context, ApplicationController.class.getName()));
    }

    @Test
    public void testWithPresenters() throws Exception {
        StandardGeneratorContext context = createGeneratorContext(createDefaultOracle(), gwtpResourcesWith(
                ResourceBase.PRESENTER1, ResourceBase.PRESENTER2, ResourceBase.PRESENTERASYNC));

        assertNotNull(generator.generate(logger, context, ApplicationController.class.getName()));

        GeneratedUnit ginjectorUnit = context.getGeneratedUnitMap().get(GinjectorGenerator.DEFAULT_FQ_NAME);

        assertNotNull(ginjectorUnit);

        assertEquals(GINJECTOR_WITH_PRESENTERS, ginjectorUnit.getSource());
    }

    @Test
    public void testWithPresenterBundles() throws Exception {
        StandardGeneratorContext context = createGeneratorContext(createDefaultOracle(), gwtpResourcesWith(
                ResourceBase.PRESENTER_CODESPLIT_BUNDLE1, ResourceBase.PRESENTER_CODESPLIT_BUNDLE2,
                ResourceBase.PRESENTER_CODESPLIT_BUNDLE3));

        assertNotNull(generator.generate(logger, context, ApplicationController.class.getName()));

        GeneratedUnit ginjectorUnit = context.getGeneratedUnitMap().get(GinjectorGenerator.DEFAULT_FQ_NAME);
        GeneratedUnit fooModuleUnit = context.getGeneratedUnitMap().get("com.gwtplatform.mvp.client.FooBundle");

        assertNotNull(ginjectorUnit);

        assertEquals(FOO_PROVIDER_BUNDLE, fooModuleUnit.getSource());
        assertEquals(GINJECTOR_WITH_PRESENTER_BUNDLES, ginjectorUnit.getSource());
    }

    @Test
    public void testWithDefaultGatekeeper() throws Exception {
        StandardGeneratorContext context = createGeneratorContext(createDefaultOracle(), gwtpResourcesWith(
                ResourceBase.DEFAULTGATEKEEPER, ResourceBase.PRESENTER1));

        assertNotNull(generator.generate(logger, context, ApplicationController.class.getName()));

        GeneratedUnit ginjectorUnit = context.getGeneratedUnitMap().get(GinjectorGenerator.DEFAULT_FQ_NAME);

        assertNotNull(ginjectorUnit);

        assertEquals(GINJECTOR_WITH_DEFAULTGATEKEEPER, ginjectorUnit.getSource());
    }

    private PropertyOracle createDefaultOracle() {
        return createPropertyOracleBuilder().with(AbstractGenerator.GIN_GINJECTOR_EXTENSION, true)
                .with(AbstractGenerator.GIN_GINJECTOR_MODULES, true, ResourceBase.FOOMODULE.getTypeName())
                .build();
    }
}
