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

package com.gwtplatform.processors.tools.outputter;

import java.io.IOException;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.logger.Logger;

public enum OutputType {
    JAVA {
        @Override
        FileObject createFileObject(Logger logger, Filer filer, Type type) throws IOException {
            return filer.createSourceFile(type.getQualifiedName());
        }
    },
    GWT {
        @Override
        FileObject createFileObject(Logger logger, Filer filer, Type type) throws IOException {
            return new GwtFileObject(logger, filer, type);
        }
    },
    META_INF {
        @Override
        FileObject createFileObject(Logger logger, Filer filer, Type type) throws IOException {
            return filer.createResource(
                    StandardLocation.CLASS_OUTPUT,
                    type.getPackageName(),
                    "META-INF/" + type.getSimpleName());
        }
    },
    PROPERTIES {
        @Override
        FileObject createFileObject(Logger logger, Filer filer, Type type) throws IOException {
            return filer.createResource(
                    StandardLocation.CLASS_OUTPUT,
                    type.getPackageName(),
                    type.getSimpleName() + ".properties");
        }
    };

    abstract FileObject createFileObject(Logger logger, Filer filer, Type type) throws IOException;
}
