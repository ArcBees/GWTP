package com.arcbees.carsample.client.application.cars.car.navigation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class Tab extends Composite implements CloseTabEvent.HasCloseTabHandlers {
    private static Binder uiBinder = GWT.create(Binder.class);

    interface Binder extends UiBinder<Widget, Tab> {
    }

    @UiField
    InlineLabel nameLabel;
    @UiField
    InlineLabel closeButton;

    public Tab(String name, boolean isClosable) {
        initWidget(uiBinder.createAndBindUi(this));

        nameLabel.setText(name);
        closeButton.setVisible(isClosable);
    }

    @UiHandler("closeButton")
    void onCloseButtonClicked(ClickEvent event) {
        CloseTabEvent.fire(this);
    }

    @Override
    public HandlerRegistration addCloseTabHandler(CloseTabEvent.CloseTabHandler handler) {
        return addHandler(handler, CloseTabEvent.getType());
    }
}
