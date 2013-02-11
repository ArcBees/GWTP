package com.gwtplatform.samples.basic.shared.dispatch;

import java.util.List;

import com.gwtplatform.dispatch.shared.Result;

public class BigResult implements Result {
    private List<String> myResults;

    public List<String> getMyResults() {
        return myResults;
    }

    public void setMyResults(List<String> myResults) {
        this.myResults = myResults;
    }
}
