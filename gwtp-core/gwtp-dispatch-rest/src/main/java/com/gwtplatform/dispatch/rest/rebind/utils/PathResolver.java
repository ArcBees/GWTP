/*
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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.Path;

import com.google.gwt.core.ext.typeinfo.HasAnnotations;

public class PathResolver {

    /* a path with regular expression looks like: @Path("{id:[0-9]*}/subpath") */
    private static final String REGEX_MATCH_PATH_PATTERN =
            "\\{ *(\\w[\\w\\.-]*) *(\\: *(((\\\\\\{)|(\\\\\\})|(\\[[^\\]]*\\])|([^\\{\\}])|(\\{[0-9\\,]+\\}))*))?"
                    + "[^\\\\]?\\}";
    private static final int REGEX_PATH_PATTERN_GROUP_PARAMETER = 1;
    private static final int REGEX_PATH_PATTERN_GROUP_REGEX = 3;
    private static final int REGEX_PATH_PATTERN_GROUPS = 9;

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

    public static PathInformation resolvePathInformation(String path, HasAnnotations method) {
        String rawPath = PathResolver.resolve(path, method);
        String parsedPath = null;
        Map<String, String> pathParamRegexMapping = null;

        if (!isNullOrEmpty(rawPath)) {
            parsedPath = resolvePath(rawPath);

            if (!rawPath.equals(parsedPath)) {
                /*
                 * the parsed path (path without regular expression information) is different to the rawPath - so it
                 * seems a regular expression information is been removed. in this case try to extract the regex
                 * information
                 */
                pathParamRegexMapping = extractPathParameterRegex(rawPath);
            }
        }

        return new PathInformation(rawPath, parsedPath, pathParamRegexMapping);
    }

    public static Map<String, String> extractPathParameterRegex(String rawPath) {
        Map<String, String> regexMapping = new HashMap<String, String>();

        Pattern pattern = Pattern.compile(REGEX_MATCH_PATH_PATTERN);
        Matcher matcher = pattern.matcher(rawPath);

        while (matcher.find()) {
            if (matcher.groupCount() == REGEX_PATH_PATTERN_GROUPS) {
                /* extract parameter and regex */
                String parameter = matcher.group(REGEX_PATH_PATTERN_GROUP_PARAMETER);
                String regex = matcher.group(REGEX_PATH_PATTERN_GROUP_REGEX);

                /* only consider the values if both of them are set / can be retrieved */
                if (!isNullOrEmpty(parameter) && !isNullOrEmpty(regex)) {
                    regexMapping.put(parameter, regex);
                }
            }
        }

        return regexMapping;
    }

    public static String resolvePath(String rawPath) {
        Pattern pattern = Pattern.compile(REGEX_MATCH_PATH_PATTERN);
        Matcher matcher = pattern.matcher(rawPath);

        return matcher.replaceAll("{$" + REGEX_PATH_PATTERN_GROUP_PARAMETER + "}");
    }

    private static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
