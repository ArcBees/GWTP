package com.gwtplatform.samples.basic.shared.dispatch;

import com.gwtplatform.dispatch.shared.Result;

public class SmallResult implements Result {
    private String myValue;

    public String getMyValue() {
        return myValue;
    }

    public void setMyValue(String myValue) {
        this.myValue = myValue;
    }
}
