package com.gwtplatform.carstore.client.application.cars.car.navigation;

import javax.inject.Inject;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class NavigationTabView extends ViewWithUiHandlers<NavigationUiHandlers> implements
        NavigationTabPresenter.MyView {
    public interface Binder extends UiBinder<Widget, NavigationTabView> {
    }

    @UiField
    TabBar tabBar;

    @Inject
    public NavigationTabView(final Binder uiBinder) {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void createTab(Widget tabElement) {
        tabBar.addTab(tabElement);
    }

    @Override
    public void removeTab(int index) {
        tabBar.removeTab(index);
    }

    @Override
    public void selectTab(int index) {
        tabBar.selectTab(index);
    }

    @UiHandler("tabBar")
    void onTabBarSelection(SelectionEvent<Integer> event) {
        getUiHandlers().onTabSelected(event.getSelectedItem());
    }
}
