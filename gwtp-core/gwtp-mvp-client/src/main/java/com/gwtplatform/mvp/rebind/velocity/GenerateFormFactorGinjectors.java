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

package com.gwtplatform.mvp.rebind.velocity;

import javax.inject.Inject;

import com.gwtplatform.mvp.rebind.velocity.ginjectors.FormFactorGinjectorFactory;
import com.gwtplatform.mvp.rebind.velocity.ginjectors.FormFactorGinjectorProviderGenerator;

public class GenerateFormFactorGinjectors {
    private static final String GINJECTOR_PROVIDER_IMPL_NAME = "GinjectorProvider";
    private static final String GINJECTOR_PROVIDER_TEMPLATE = "com/gwtplatform/mvp/rebind/GinjectorProvider.vm";

    private static final String DESKTOP_PROPERTY_NAME = "gin.ginjector.module.desktop";
    private static final String DESKTOP_GINJECTOR_IMPL_NAME = "DesktopGinjector";
    private static final String DESKTOP_GINJECTOR_PROVIDER_IMPL_NAME = "DesktopGinjectorProvider";
    private static final String DESKTOP_GINJECTOR_TEMPLATE = "com/gwtplatform/mvp/rebind/DesktopGinjector.vm";
    private static final String DESKTOP_GINJECTOR_PROVIDER_TEMPLATE
            = "com/gwtplatform/mvp/rebind/DesktopGinjectorProvider.vm";

    private static final String MOBILE_PROPERTY_NAME = "gin.ginjector.module.mobile";
    private static final String MOBILE_GINJECTOR_IMPL_NAME = "MobileGinjector";
    private static final String MOBILE_GINJECTOR_PROVIDER_IMPL_NAME = "MobileGinjectorProvider";
    private static final String MOBILE_GINJECTOR_TEMPLATE = "com/gwtplatform/mvp/rebind/MobileGinjector.vm";
    private static final String MOBILE_GINJECTOR_PROVIDER_TEMPLATE
            = "com/gwtplatform/mvp/rebind/MobileGinjectorProvider.vm";

    private static final String TABLET_PROPERTY_NAME = "gin.ginjector.module.tablet";
    private static final String TABLET_GINJECTOR_IMPL_NAME = "TabletGinjector";
    private static final String TABLET_GINJECTOR_PROVIDER_IMPL_NAME = "TabletGinjectorProvider";
    private static final String TABLET_GINJECTOR_TEMPLATE = "com/gwtplatform/mvp/rebind/TabletGinjector.vm";
    private static final String TABLET_GINJECTOR_PROVIDER_TEMPLATE
            = "com/gwtplatform/mvp/rebind/TabletGinjectorProvider.vm";

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
        FormFactorGinjectorProviderGenerator ginjectorProvider
                = formFactorGinjectorFactory.createGinjectorProvider(
                GINJECTOR_PROVIDER_TEMPLATE,
                GINJECTOR_PROVIDER_IMPL_NAME);
        ginjectorProvider.generate();
    }

    private void generateDesktopFormFactorGenerators() throws Exception {
        FormFactorGinjectorProviderGenerator desktopGinjector
                = formFactorGinjectorFactory.createGinjector(
                DESKTOP_GINJECTOR_TEMPLATE,
                DESKTOP_PROPERTY_NAME,
                DESKTOP_GINJECTOR_IMPL_NAME);
        desktopGinjector.generate();

        FormFactorGinjectorProviderGenerator desktopGinjectorProvider
                = formFactorGinjectorFactory.createGinjectorProvider(
                DESKTOP_GINJECTOR_PROVIDER_TEMPLATE,
                DESKTOP_GINJECTOR_PROVIDER_IMPL_NAME);
        desktopGinjectorProvider.generate();
    }

    private void generateMobileFormFactorGenerators() throws Exception {
        FormFactorGinjectorProviderGenerator mobileGinjector
                = formFactorGinjectorFactory.createGinjector(
                MOBILE_GINJECTOR_TEMPLATE,
                MOBILE_PROPERTY_NAME,
                MOBILE_GINJECTOR_IMPL_NAME);
        mobileGinjector.generate();

        FormFactorGinjectorProviderGenerator mobileGinjectorProvider
                = formFactorGinjectorFactory.createGinjectorProvider(
                MOBILE_GINJECTOR_PROVIDER_TEMPLATE,
                MOBILE_GINJECTOR_PROVIDER_IMPL_NAME);
        mobileGinjectorProvider.generate();
    }

    private void generateTabletFormFactorGenerators() throws Exception {
        FormFactorGinjectorProviderGenerator tabletGinjector
                = formFactorGinjectorFactory.createGinjector(
                TABLET_GINJECTOR_TEMPLATE,
                TABLET_PROPERTY_NAME,
                TABLET_GINJECTOR_IMPL_NAME);
        tabletGinjector.generate();

        FormFactorGinjectorProviderGenerator tabletGinjectorProvider
                = formFactorGinjectorFactory.createGinjectorProvider(
                TABLET_GINJECTOR_PROVIDER_TEMPLATE,
                TABLET_GINJECTOR_PROVIDER_IMPL_NAME);
        tabletGinjectorProvider.generate();
    }
}
