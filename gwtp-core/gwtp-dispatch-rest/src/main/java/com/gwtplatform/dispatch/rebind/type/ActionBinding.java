package com.gwtplatform.dispatch.rebind.type;

import com.google.gwt.core.ext.typeinfo.JParameter;

public class ActionBinding {
    private final String actionClass;
    private final String methodName;
    private final String resultClass;
    private final JParameter[] parameters;

    public ActionBinding(String actionClass, String methodName, String resultClass, JParameter[] parameters) {
        this.actionClass = actionClass;
        this.methodName = methodName;
        this.resultClass = resultClass;
        this.parameters = parameters;
    }

    public String getActionClass() {
        return actionClass;
    }

    public String getResultClass() {
        return resultClass;
    }

    public JParameter[] getParameters() {
        return parameters;
    }

    public String getMethodName() {
        return methodName;
    }
}
