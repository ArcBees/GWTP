package com.gwtplatform.carstore.shared.dispatch;

public class GetCurrentUserAction extends ActionImpl<GetCurrentUserResult> {
    public GetCurrentUserAction() {
    }

    @Override
    public boolean isSecured() {
        return false;
    }
}
