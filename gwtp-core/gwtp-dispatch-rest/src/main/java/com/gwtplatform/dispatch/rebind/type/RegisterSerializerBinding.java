package com.gwtplatform.dispatch.rebind.type;

import com.gwtplatform.dispatch.client.rest.SerializedType;

public class RegisterSerializerBinding {
    private final String actionClass;
    private final SerializedType serializedType;
    private final String serializerClass;

    public RegisterSerializerBinding(String actionClass, SerializedType serializedType, String serializerClass) {
        this.actionClass = actionClass;
        this.serializedType = serializedType;
        this.serializerClass = serializerClass;
    }

    public String getActionClass() {
        return actionClass;
    }

    public SerializedType getSerializedType() {
        return serializedType;
    }

    public String getSerializerClass() {
        return serializerClass;
    }
}
