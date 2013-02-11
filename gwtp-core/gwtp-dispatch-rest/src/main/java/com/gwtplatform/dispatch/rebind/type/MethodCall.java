package com.gwtplatform.dispatch.rebind.type;

import com.google.gwt.core.ext.typeinfo.JParameter;

public class MethodCall {
    private final String methodName;
    private String fieldName;
    private final JParameter parameter;

    public MethodCall(String methodName, String fieldName, JParameter parameter) {
        this.methodName = methodName;
        this.fieldName = fieldName;
        this.parameter = parameter;
    }

    public String getMethodName() {
        return methodName;
    }

    public JParameter getParameter() {
        return parameter;
    }

    public String getFieldName() {
        return fieldName;
    }
}
