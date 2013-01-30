package com.gwtplatform.dispatch.shared.rest;

import java.io.Serializable;

public class ResponseParameter implements Serializable {
    private String serializerId;

    ResponseParameter() {
    }

    public ResponseParameter(String serializerId) {
        this.serializerId = serializerId;
    }

    public String getSerializerId() {
        return serializerId;
    }
}
