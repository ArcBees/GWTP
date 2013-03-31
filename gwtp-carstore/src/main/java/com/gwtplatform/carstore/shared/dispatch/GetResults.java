package com.gwtplatform.carstore.shared.dispatch;

import java.util.List;

import com.gwtplatform.carstore.shared.dto.Dto;
import com.gwtplatform.dispatch.shared.Result;

public class GetResults<T extends Dto> implements Result {
    private List<T> results;

    @SuppressWarnings("unused")
    protected GetResults() {
    }

    public GetResults(List<T> results) {
        this.results = results;
    }

    public List<T> getResults() {
        return results;
    }
}
