package com.gwtplatform.carstore.client.application.login;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class LoginMobileView extends ViewWithUiHandlers<LoginUiHandlers> implements LoginPresenter.MyView {
    public interface Binder extends UiBinder<Widget, LoginMobileView> {
    }

    @UiField
    Button login;
    @UiField
    PasswordTextBox password;
    @UiField
    TextBox username;

    @Inject
    public LoginMobileView(Binder uiBinder) {
        initWidget(uiBinder.createAndBindUi(this));

        username.getElement().setAttribute("placeholder", "Username");
        password.getElement().setAttribute("placeholder", "Password");
    }

    @UiHandler("login")
    void onLoginClicked(ClickEvent event) {
        String username = this.username.getValue();
        String password = this.password.getValue();

        getUiHandlers().login(username, password);
    }

    @Override
    public void setLoginButtonEnabled(boolean enabled) {
        login.setEnabled(enabled);
    }
}
