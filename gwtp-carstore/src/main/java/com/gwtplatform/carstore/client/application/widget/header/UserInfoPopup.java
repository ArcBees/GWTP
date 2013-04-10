package com.gwtplatform.carstore.client.application.widget.header;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.carstore.client.resources.AppResources;

public class UserInfoPopup extends PopupPanel {
    public interface Binder extends UiBinder<Widget, UserInfoPopup> {
    }

    public interface Handler {
        void onLogout();
    }

    @UiField
    Label username;

    private Handler handler;

    @Inject
    public UserInfoPopup(final Binder uiBinder, final AppResources appResources) {
        setWidget(uiBinder.createAndBindUi(this));
        setAutoHideEnabled(true);
        setStyleName(appResources.styles().gwtPopupPanel());
    }

    public void setUsername(String login) {
        username.setText(login);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @UiHandler("logout")
    void onLogoutClicked(ClickEvent event) {
        handler.onLogout();
        hide();
    }
}
