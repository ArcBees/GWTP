package com.gwtplatform.carstore.client.application.login;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class LoginMobileModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        bindPresenter(LoginPresenter.class, LoginPresenter.MyView.class, LoginMobileView.class,
                LoginPresenter.MyProxy.class);

        bind(LoginUiHandlers.class).to(LoginPresenter.class);
    }
}
