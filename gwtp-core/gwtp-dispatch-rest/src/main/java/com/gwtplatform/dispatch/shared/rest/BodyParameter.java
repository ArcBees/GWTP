package com.gwtplatform.dispatch.shared.rest;

import java.io.Serializable;

public class BodyParameter implements Serializable {
    private Serializable object;
    private String serializerId;

    BodyParameter() {
    }

    public BodyParameter(Serializable object, String serializerId) {
        this.object = object;
        this.serializerId = serializerId;
    }

    public Serializable getObject() {
        return object;
    }

    public String getSerializerId() {
        return serializerId;
    }
}
