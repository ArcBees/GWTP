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

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.util.Elements;

import org.junit.Before;
import org.junit.Test;

import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.utils.Utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class GwtSourceFilterTest {
    private static final String MODULE1 = "com.gwtplatform.processors.tools.module1.GwtSourceFilterTestModule";
    private static final String MODULE4 = "com.gwtplatform.processors.tools.module4.GwtSourceFilterTestModule";
    private static final String MODULE4_SOURCE_PATH = "com.gwtplatform.processors.tools.module4.test";

    private Logger logger;
    private Utils utils;
    private Elements elements;

    @Before
    public void setUp() {
        logger = mock(Logger.class);
        utils = mock(Utils.class);
        elements = mock(Elements.class);

        given(utils.getElements()).willReturn(elements);
    }

    @Test
    public void load_module1() {
        // when
        GwtSourceFilter filter = new GwtSourceFilter(logger, utils, MODULE1);

        // then
        assertThat(filter.getSourcePackages()).containsOnly(
                "com.gwtplatform.processors.tools.module1.client",
                "com.gwtplatform.processors.tools.module1.client.potatoes.red",
                "com.gwtplatform.processors.tools.module2.client",
                "com.gwtplatform.processors.tools.module2.shared",
                "com.gwtplatform.processors.tools.module3",
                MODULE4_SOURCE_PATH
        );
    }

    @Test
    public void load_module4() {
        // when
        GwtSourceFilter filter = new GwtSourceFilter(logger, utils, MODULE4);

        // then
        assertThat(filter.getSourcePackages()).containsOnly(MODULE4_SOURCE_PATH);
    }

    @Test
    public void load_module4_twice() {
        // given
        GwtSourceFilter filter = new GwtSourceFilter(logger, utils, MODULE4);

        // when
        filter.addModule(MODULE4);

        // then
        assertThat(filter.getSourcePackages()).containsOnly(MODULE4_SOURCE_PATH);
    }

    @Test
    public void elementIsPartOfGwtSource_notInSource() {
        // given
        GwtSourceFilter filter = new GwtSourceFilter(logger, utils, MODULE1);
        Element element = createElementStubWithPackage("org.jukito");

        // when
        boolean result = filter.elementIsPartOfGwtSource(element);

        // then
        assertThat(result).isFalse();
    }

    @Test
    public void elementIsPartOfGwtSource_sameSource() {
        // given
        GwtSourceFilter filter = new GwtSourceFilter(logger, utils, MODULE1);
        Element element = createElementStubWithPackage(MODULE4_SOURCE_PATH);

        // when
        boolean result = filter.elementIsPartOfGwtSource(element);

        // then
        assertThat(result).isTrue();
    }

    @Test
    public void elementIsPartOfGwtSource_subPackage() {
        // given
        GwtSourceFilter filter = new GwtSourceFilter(logger, utils, MODULE1);
        Element element = createElementStubWithPackage(MODULE4_SOURCE_PATH + ".sub.subsub");

        // when
        boolean result = filter.elementIsPartOfGwtSource(element);

        // then
        assertThat(result).isTrue();
    }

    private Element createElementStubWithPackage(String packageName) {
        Element element = mock(Element.class);
        PackageElement packageElement = mock(PackageElement.class);
        Name name = mock(Name.class);

        given(elements.getPackageOf(element)).willReturn(packageElement);
        given(packageElement.getQualifiedName()).willReturn(name);
        given(name.toString()).willReturn(packageName);

        return element;
    }
}
