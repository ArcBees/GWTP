/*
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

package com.gwtplatform.mvp.rebind.velocity;

import com.gwtplatform.mvp.rebind.velocity.ginjectors.FormFactorGinjectorFactory;
import com.gwtplatform.mvp.rebind.velocity.ginjectors.FormFactorGinjectorGenerator;
import com.gwtplatform.mvp.rebind.velocity.ginjectors.FormFactorGinjectorProviderGenerator;
import com.gwtplatform.mvp.rebind.velocity.ginjectors.GinjectorProviderGenerator;

import jakarta.inject.Inject;

public class GenerateFormFactorGinjectors {
    private static final String FORMFACTOR_GINJECTOR_TEMPLATE = "com/gwtplatform/mvp/rebind/FormFactorGinjector.vm";
    private static final String FORMFACTOR_GINJECTOR_PROVIDER_TEMPLATE
            = "com/gwtplatform/mvp/rebind/FormFactorGinjectorProvider.vm";

    private static final String GINJECTOR_PROVIDER_IMPL_NAME = "GinjectorProvider";
    private static final String GINJECTOR_PROVIDER_TEMPLATE = "com/gwtplatform/mvp/rebind/GinjectorProvider.vm";

    private static final String DESKTOP_PROPERTY_NAME = "gin.ginjector.module.desktop";
    private static final String DESKTOP_GINJECTOR_IMPL_NAME = "DesktopGinjector";

    private static final String MOBILE_PROPERTY_NAME = "gin.ginjector.module.mobile";
    private static final String MOBILE_GINJECTOR_IMPL_NAME = "MobileGinjector";

    private static final String TABLET_PROPERTY_NAME = "gin.ginjector.module.tablet";
    private static final String TABLET_GINJECTOR_IMPL_NAME = "TabletGinjector";

    private final FormFactorGinjectorFactory formFactorGinjectorFactory;

    @Inject
    GenerateFormFactorGinjectors(FormFactorGinjectorFactory formFactorGinjectorFactory) {
        this.formFactorGinjectorFactory = formFactorGinjectorFactory;
    }

    public void generate() throws Exception {
        generateGinjectorProvider();

        generateDesktopFormFactorGenerators();

        generateMobileFormFactorGenerators();

        generateTabletFormFactorGenerators();
    }

    private void generateGinjectorProvider() throws Exception {
        GinjectorProviderGenerator ginjectorProvider
                = formFactorGinjectorFactory.createDefaultGinjectorProvider(
                GINJECTOR_PROVIDER_TEMPLATE,
                GINJECTOR_PROVIDER_IMPL_NAME);
        ginjectorProvider.generate();
    }

    private void generateDesktopFormFactorGenerators() throws Exception {
        FormFactorGinjectorGenerator desktopGinjector
                = formFactorGinjectorFactory.createGinjector(
                FORMFACTOR_GINJECTOR_TEMPLATE,
                DESKTOP_PROPERTY_NAME,
                DESKTOP_GINJECTOR_IMPL_NAME);
        desktopGinjector.generate();

        FormFactorGinjectorProviderGenerator desktopGinjectorProvider
                = formFactorGinjectorFactory.createGinjectorProvider(
                FORMFACTOR_GINJECTOR_PROVIDER_TEMPLATE,
                DESKTOP_GINJECTOR_IMPL_NAME);
        desktopGinjectorProvider.generate();
    }

    private void generateMobileFormFactorGenerators() throws Exception {
        FormFactorGinjectorGenerator mobileGinjector
                = formFactorGinjectorFactory.createGinjector(
                FORMFACTOR_GINJECTOR_TEMPLATE,
                MOBILE_PROPERTY_NAME,
                MOBILE_GINJECTOR_IMPL_NAME);
        mobileGinjector.generate();

        FormFactorGinjectorProviderGenerator mobileGinjectorProvider
                = formFactorGinjectorFactory.createGinjectorProvider(
                FORMFACTOR_GINJECTOR_PROVIDER_TEMPLATE,
                MOBILE_GINJECTOR_IMPL_NAME);
        mobileGinjectorProvider.generate();
    }

    private void generateTabletFormFactorGenerators() throws Exception {
        FormFactorGinjectorGenerator tabletGinjector
                = formFactorGinjectorFactory.createGinjector(
                FORMFACTOR_GINJECTOR_TEMPLATE,
                TABLET_PROPERTY_NAME,
                TABLET_GINJECTOR_IMPL_NAME);
        tabletGinjector.generate();

        FormFactorGinjectorProviderGenerator tabletGinjectorProvider
                = formFactorGinjectorFactory.createGinjectorProvider(
                FORMFACTOR_GINJECTOR_PROVIDER_TEMPLATE,
                TABLET_GINJECTOR_IMPL_NAME);
        tabletGinjectorProvider.generate();
    }
}
