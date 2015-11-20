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