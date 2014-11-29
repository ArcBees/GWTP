/**
 * Copyright 2014 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.rebind.utils;

import javax.ws.rs.Path;

import com.google.gwt.core.ext.typeinfo.HasAnnotations;

public class PathResolver {
    public static String resolve(String basePath, HasAnnotations type) {
        String path = resolve(type);
        path = concatenate(basePath, path);

        return path;
    }

    public static String resolve(HasAnnotations type) {
        String path = "";

        if (type.isAnnotationPresent(Path.class)) {
            path = type.getAnnotation(Path.class).value();
        }

        return normalize(path);
    }

    private static String concatenate(String path1, String path2) {
        String newPath1 = normalize(path1);
        String newPath2 = normalize(path2);

        if (newPath1.endsWith("/") && !newPath2.isEmpty()) {
            newPath2 = newPath2.substring(1);
        }

        return newPath1 + newPath2;
    }

    private static String normalize(String path) {
        String newPath = path;
        if (!path.isEmpty()
                && !path.startsWith("/")
                && !path.startsWith("http://")
                && !path.startsWith("https://")) {
            newPath = "/" + path;
        }

        return newPath;
    }
}
