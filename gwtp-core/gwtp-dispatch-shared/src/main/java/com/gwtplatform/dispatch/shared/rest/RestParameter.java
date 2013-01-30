package com.gwtplatform.dispatch.shared.rest;

import java.io.Serializable;

public class RestParameter implements Serializable {
    private String name;
    private Serializable object;

    RestParameter() {
    }

    public RestParameter(String name, Serializable object) {
        this.name = name;
        this.object = object;
    }

    public String getName() {
        return name;
    }

    public Serializable getObject() {
        return object;
    }
}
