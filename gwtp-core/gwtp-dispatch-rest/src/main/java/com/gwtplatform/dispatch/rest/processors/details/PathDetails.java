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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.Element;
import javax.ws.rs.Path;

import static com.google.common.base.Strings.isNullOrEmpty;

public class PathDetails {
    private static final Pattern PARAMETER_REGEX_PATTERN = Pattern.compile(
            "\\{ *(\\w[\\w.-]*) *: *((?:\\\\\\{|\\\\\\}|\\[[^\\]]*\\]|[^{}]|\\{[0-9,]+\\})*)[^\\\\]?\\}");
    private static final int PARAMETER_REGEX_GROUP_NAME = 1;
    private static final int PARAMETER_REGEX_GROUP_REGEX = 2;

    private String value;
    private Map<String, String> regexes = new HashMap<>();

    public PathDetails(Element element) {
        resolvePath(element);
    }

    public PathDetails(
            Element element,
            PathDetails basePath) {
        regexes.putAll(basePath.regexes);

        resolvePath(element);
        insertPathPrefix(basePath);
    }

    private void resolvePath(Element element) {
        Path path = element.getAnnotation(Path.class);
        value = path != null ? path.value() : "";

        extractRegexes();
        value = normalize(value);
    }

    private void extractRegexes() {
        Matcher matcher = PARAMETER_REGEX_PATTERN.matcher(value);

        while (matcher.find()) {
            String parameter = matcher.group(PARAMETER_REGEX_GROUP_NAME);
            String regex = matcher.group(PARAMETER_REGEX_GROUP_REGEX);

            if (!isNullOrEmpty(parameter) && !isNullOrEmpty(regex)) {
                regexes.put(parameter, regex);
            }
        }
    }

    private void insertPathPrefix(PathDetails prefix) {
        String prefixValue = prefix.value;

        if (value.endsWith("/") && !prefixValue.isEmpty()) {
            prefixValue = prefixValue.substring(1);
        }

        value = prefixValue + value;
    }

    private String normalize(String path) {
        String newPath = path;
        if (!path.isEmpty()
                && !path.startsWith("/")
                && !path.startsWith("http://")
                && !path.startsWith("https://")) {
            newPath = "/" + path;
        }

        Matcher matcher = PARAMETER_REGEX_PATTERN.matcher(newPath);
        return matcher.replaceAll("{$" + PARAMETER_REGEX_GROUP_NAME + "}");
    }

    public String getValue() {
        return value;
    }

    public String getRegex(String paramName) {
        return regexes.get(paramName);
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
        PathDetails path = (PathDetails) o;
        return Objects.equals(value, path.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
