package com.gwtplatform.carstore.client.application.login;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
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
    interface Binder extends UiBinder<Widget, LoginMobileView> {
    }

    @UiField
    Button login;
    @UiField
    PasswordTextBox password;
    @UiField
    TextBox username;

    @Inject
    LoginMobileView(Binder uiBinder) {
        initWidget(uiBinder.createAndBindUi(this));

        username.getElement().setAttribute("placeholder", "Username");
        password.getElement().setAttribute("placeholder", "Password");
    }

    @Override
    public void setLoginButtonEnabled(boolean enabled) {
        login.setEnabled(enabled);
    }

    @UiHandler("login")
    void onLoginClicked(ClickEvent event) {
        processLogin();
    }

    @UiHandler("password")
    void onPasswordKeyUp(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            processLogin();
        }
    }

    private void processLogin() {
        String username = this.username.getValue();
        String password = this.password.getValue();

        getUiHandlers().login(username, password);
    }
}
