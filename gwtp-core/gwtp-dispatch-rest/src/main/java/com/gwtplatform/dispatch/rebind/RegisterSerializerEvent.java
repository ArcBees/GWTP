package com.gwtplatform.dispatch.rebind;

public class RegisterSerializerEvent {
    private final String serializerId;
    private final String serializerClass;

    public RegisterSerializerEvent(String serializerId, String serializerClass) {
        this.serializerId = serializerId;
        this.serializerClass = serializerClass;
    }

    public String getSerializerId() {
        return serializerId;
    }

    public String getSerializerClass() {
        return serializerClass;
    }
}
