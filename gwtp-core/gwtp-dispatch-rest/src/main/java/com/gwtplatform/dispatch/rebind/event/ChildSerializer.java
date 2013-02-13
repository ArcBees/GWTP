package com.gwtplatform.dispatch.rebind.event;

public class ChildSerializer {
    private String serializerClassName;

    public ChildSerializer(String serializerClassName) {
        this.serializerClassName = serializerClassName;
    }

    public String getSerializerClassName() {
        return serializerClassName;
    }
}
