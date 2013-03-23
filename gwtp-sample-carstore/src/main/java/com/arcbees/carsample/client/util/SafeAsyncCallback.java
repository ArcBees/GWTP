package com.arcbees.carsample.client.util;

import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class SafeAsyncCallback<T> implements AsyncCallback<T> {
    @Override
    public void onFailure(Throwable caught) {
    }
}
