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

package com.gwtplatform.dispatch.rest.processors.details;

import java.util.Objects;

import javax.lang.model.element.Element;

import static com.google.auto.common.MoreElements.isAnnotationPresent;

public class Path {
    private final String value;

    public Path(Element element) {
        value = resolvePath(element);
    }

    public Path(Element element, Path basePath) {
        String path = resolvePath(element);
        value = concatenate(basePath.getValue(), path);
    }

    private String resolvePath(Element element) {
        String path = "";

        if (isAnnotationPresent(element, javax.ws.rs.Path.class)) {
            path = element.getAnnotation(javax.ws.rs.Path.class).value();
        }

        return normalize(path);
    }

    private String concatenate(String path1, String path2) {
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

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Path path = (Path) o;
        return Objects.equals(value, path.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
