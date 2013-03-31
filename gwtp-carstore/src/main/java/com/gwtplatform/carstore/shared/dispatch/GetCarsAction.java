package com.gwtplatform.carstore.shared.dispatch;

import com.gwtplatform.carstore.shared.domain.Car;

public class GetCarsAction extends ActionImpl<GetResults<Car>> {
    private Integer limit = null;
    private Integer offset = null;

    public GetCarsAction() {
    }

    public GetCarsAction(Integer offset, Integer limit) {
        this.limit = limit;
        this.offset = offset;
    }

    @Override
    public boolean isSecured() {
        return false;
    }

    public Integer getLimit() {
        return limit;
    }

    public Integer getOffset() {
        return offset;
    }
}
