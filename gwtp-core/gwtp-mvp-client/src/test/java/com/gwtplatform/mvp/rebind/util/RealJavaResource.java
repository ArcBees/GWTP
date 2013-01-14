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

package com.gwtplatform.mvp.rebind.util;

import com.google.gwt.dev.javac.testing.impl.MockJavaResource;
import com.google.gwt.dev.util.Util;

import java.io.InputStream;

public class RealJavaResource extends MockJavaResource {
    public RealJavaResource(Class<?> clazz) {
        super(clazz.getName());
    }

    @Override
    public CharSequence getContent() {
        String resourceName = getTypeName().replace('.', '/') + ".java";
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
        return Util.readStreamAsString(stream);
    }
}