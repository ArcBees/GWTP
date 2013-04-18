package com.gwtplatform.carstore.client.application.testutils;

import com.google.inject.TypeLiteral;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

@SuppressWarnings("GwtInconsistentSerializableClass")
public class ActionImpl<T extends Result> implements Action<T> {
    private final TypeLiteral<Action<T>> typeLiteral;

    public ActionImpl(TypeLiteral<Action<T>> typeLiteral) {
        this.typeLiteral = typeLiteral;
    }

    @Override
    public String getServiceName() {
        return "";
    }

    @Override
    public boolean isSecured() {
        return false;
    }

    public TypeLiteral<Action<T>> getTypeLiteral() {
        return typeLiteral;
    }
}
