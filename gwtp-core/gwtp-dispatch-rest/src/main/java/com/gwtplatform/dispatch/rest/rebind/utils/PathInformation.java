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

package com.gwtplatform.dispatch.rest.rebind.utils;

import java.util.Map;

public class PathInformation {
    private final String rawServicePath;
    private final String path;
    private final Map<String, String> pathParamRegexMapping;

    public PathInformation(String rawServicePath, String path, Map<String, String> pathParamRegexMapping) {
        super();
        this.rawServicePath = rawServicePath;
        this.path = path;
        this.pathParamRegexMapping = pathParamRegexMapping;
    }

    public String getRawServicePath() {
        return rawServicePath;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getPathParamRegexMapping() {
        return pathParamRegexMapping;
    }
}
