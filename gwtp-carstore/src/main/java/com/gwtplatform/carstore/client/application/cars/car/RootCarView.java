package com.gwtplatform.carstore.client.application.cars.car;

import javax.inject.Inject;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.gwtplatform.mvp.client.ViewImpl;

public class RootCarView extends ViewImpl implements RootCarPresenter.MyView {
    public interface Binder extends UiBinder<HTMLPanel, RootCarView> {
    }

    @UiField
    SimplePanel tabBarPanel;
    @UiField
    SimplePanel contentPanel;

    @Inject
    RootCarView(Binder uiBinder) {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setInSlot(Object slot, IsWidget content) {
        if (slot == RootCarPresenter.SLOT_TAB_BAR) {
            tabBarPanel.setWidget(content);
        } else if (slot == RootCarPresenter.TYPE_SetCarContent) {
            contentPanel.setWidget(content);
        }
    }
}
