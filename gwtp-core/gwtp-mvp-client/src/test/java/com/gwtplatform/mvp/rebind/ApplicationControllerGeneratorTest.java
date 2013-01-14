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
import com.gwtplatform.mvp.client.ApplicationController;
import com.gwtplatform.mvp.client.Bootstrapper;
import com.gwtplatform.mvp.client.PreBootstrapper;
import com.gwtplatform.mvp.client.annotations.Bootstrap;
import com.gwtplatform.mvp.client.annotations.PreBootstrap;
import com.gwtplatform.mvp.rebind.util.GeneratorTestBase;
import com.gwtplatform.mvp.rebind.util.GwtpResourceBase;
import com.google.gwt.dev.util.UnitTestTreeLogger;
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
            GINJECTOR_MULTIPLE_EXTENSIONS;

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
                .with(AbstractGenerator.GIN_GINJECTOR_MODULES, true, GwtpResourceBase.FOOMODULE.getTypeName(),
                        GwtpResourceBase.BARMODULE.getTypeName())
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
                        GwtpResourceBase.GINJECTOREXTENSION1.getTypeName())
                .with(AbstractGenerator.GIN_GINJECTOR_MODULES, true, GwtpResourceBase.FOOMODULE.getTypeName())
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
                        GwtpResourceBase.GINJECTOREXTENSION1.getTypeName(),
                        GwtpResourceBase.GINJECTOREXTENSION2.getTypeName())
                .with(AbstractGenerator.GIN_GINJECTOR_MODULES, true, GwtpResourceBase.FOOMODULE.getTypeName())
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
                        GwtpResourceBase.GINJECTOREXTENSION1.getTypeName(),
                        GwtpResourceBase.GINJECTOREXTENSION2.getTypeName(),
                        GwtpResourceBase.GINJECTOREXTENSION3.getTypeName())
                .with(AbstractGenerator.GIN_GINJECTOR_MODULES, true, GwtpResourceBase.FOOMODULE.getTypeName())
                .build();
        
        StandardGeneratorContext context = createGeneratorContext(propOracle, gwtpResourcesWith());

        assertEquals(FINAL_NAME, generator.generate(logger, context, ApplicationController.class.getName()));
    }

    // TODO I think this should fail since there are 2 methods with same return value in the ginjector or will
    // the GinjectorInspector catch this?
    @Test
    public void testMultipleGinjectorExtensionsWithConflictingMethodsShouldFail() throws Exception {
        PropertyOracle propOracle = createPropertyOracleBuilder()
                .with(AbstractGenerator.GIN_GINJECTOR_EXTENSION, true,
                        GwtpResourceBase.GINJECTOREXTENSION1.getTypeName(),
                        GwtpResourceBase.GINJECTOREXTENSION2.getTypeName(),
                        GwtpResourceBase.GINJECTOREXTENSION4.getTypeName())
                .with(AbstractGenerator.GIN_GINJECTOR_MODULES, true, GwtpResourceBase.FOOMODULE.getTypeName())
                .build();
        
        StandardGeneratorContext context = createGeneratorContext(propOracle, gwtpResourcesWith());

        assertEquals(FINAL_NAME, generator.generate(logger, context, ApplicationController.class.getName()));
    }

    @Test
    public void testWithCustomBootstrapper() throws Exception {
        StandardGeneratorContext context = createGeneratorContext(createDefaultOracle(), gwtpResourcesWith(
                GwtpResourceBase.CUSTOMBOOTSTRAPPER1));

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
                GwtpResourceBase.CUSTOMBOOTSTRAPPER1, GwtpResourceBase.CUSTOMBOOTSTRAPPER2));

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
                GwtpResourceBase.CUSTOMBOOTSTRAPPER3));

        try {
            generator.generate(logger, context, ApplicationController.class.getName());
        } catch (Exception e) {
            logger.assertLogEntriesContainExpected();
        }
    }

    @Test
    public void testWithPreBootstrapper() throws Exception {
        StandardGeneratorContext context = createGeneratorContext(createDefaultOracle(),gwtpResourcesWith(
                GwtpResourceBase.CUSTOMPREBOOTSTRAPPER1));

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
                GwtpResourceBase.CUSTOMPREBOOTSTRAPPER1, GwtpResourceBase.CUSTOMPREBOOTSTRAPPER2));

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
                GwtpResourceBase.CUSTOMPREBOOTSTRAPPER3));

        try {
            generator.generate(logger, context, ApplicationController.class.getName());
        } catch (Exception e) {
            logger.assertLogEntriesContainExpected();
        }
    }

    private PropertyOracle createDefaultOracle() {
        return createPropertyOracleBuilder().with(AbstractGenerator.GIN_GINJECTOR_EXTENSION, true)
                .with(AbstractGenerator.GIN_GINJECTOR_MODULES, true, GwtpResourceBase.FOOMODULE.getTypeName())
                .build();
    }
}
