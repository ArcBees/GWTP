package com.arcbees.carsample.shared.dispatch;

import com.arcbees.carsample.shared.dto.Dto;
import com.gwtplatform.dispatch.shared.Result;

public class GetResult<T extends Dto> implements Result {
    private T result;

    protected GetResult() {
    }

    public GetResult(T result) {
        this.result = result;
    }

    public T getResult() {
        return result;
    }
}
