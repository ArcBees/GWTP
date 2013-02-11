package com.gwtplatform.samples.basic.shared.dispatch;

import com.gwtplatform.dispatch.shared.Result;

public class SmallResult implements Result {
    private String myResult;

    public String getMyResult() {
        return myResult;
    }

    public void setMyResult(String myResult) {
        this.myResult = myResult;
    }
}
