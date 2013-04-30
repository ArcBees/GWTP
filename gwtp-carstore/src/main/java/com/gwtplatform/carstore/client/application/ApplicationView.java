package com.gwtplatform.carstore.client.application;

import javax.inject.Inject;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

public class ApplicationView extends ViewImpl implements ApplicationPresenter.MyView {
    public interface Binder extends UiBinder<Widget, ApplicationView> {
    }

    @UiField
    HTMLPanel container;
    @UiField
    SimplePanel main;
    @UiField
    SimplePanel header;
    @UiField
    SimplePanel messages;

    @Inject
    ApplicationView(Binder uiBinder) {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setInSlot(Object slot, IsWidget content) {
        if (slot == ApplicationPresenter.SLOT_MAIN_CONTENT) {
            main.setWidget(content);
        } else if (slot == ApplicationPresenter.SLOT_HEADER_CONTENT) {
            header.setWidget(content);
        } else if (slot == ApplicationPresenter.SLOT_MESSAGES_CONTENT) {
            messages.setWidget(content);
        }
    }

    @Override
    public void adjustActionBar(Boolean actionBarVisible) {
    }

    @Override
    public void adjustLayout(Boolean tabsVisible) {
    }
}
