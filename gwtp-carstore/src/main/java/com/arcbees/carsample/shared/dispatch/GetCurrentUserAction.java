package com.arcbees.carsample.shared.dispatch;

public class GetCurrentUserAction extends ActionImpl<GetCurrentUserResult> {
    public GetCurrentUserAction() {
    }

    @Override
    public boolean isSecured() {
        return false;
    }
}
