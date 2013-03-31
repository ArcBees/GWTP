package com.gwtplatform.carstore.client.application.widget.header;

import java.util.Arrays;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ValuePicker;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.carstore.client.application.event.ChangeActionBarEvent;
import com.gwtplatform.carstore.client.application.renderer.EnumCell;
import com.gwtplatform.carstore.client.resources.NavigationListStyle;
import com.gwtplatform.carstore.client.security.CurrentUser;
import com.gwtplatform.carstore.shared.domain.User;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

public class HeaderView extends ViewWithUiHandlers<HeaderUiHandlers> implements HeaderPresenter.MyView {
    public interface Binder extends UiBinder<Widget, HeaderView> {
    }

    @UiField
    InlineLabel name;
    @UiField
    Button logout;
    @UiField
    HTMLPanel userOptions;
    @UiField(provided = true)
    ValuePicker<MenuItem> menuBar;

    private final PlaceManager placeManager;

    @Inject
    public HeaderView(Binder uiBinder,
            NavigationListStyle listResources,
            PlaceManager placeManager) {
        this.placeManager = placeManager;

        CellList<MenuItem> placeList = new CellList<MenuItem>(new EnumCell<MenuItem>(), listResources);
        menuBar = new ValuePicker<MenuItem>(placeList);

        initWidget(uiBinder.createAndBindUi(this));

        menuBar.setAcceptableValues(Arrays.asList(MenuItem.values()));
        menuBar.setValue(MenuItem.MANUFACTURER);
    }

    @Override
    public void enableUserOptions(CurrentUser currentUser) {
        userOptions.setVisible(true);
        User user = currentUser.getUser();
        name.setText(user.getFirstName());
    }

    @Override
    public void disableUserOptions() {
        userOptions.setVisible(false);
    }

    @Override
    public void showActionBar(Boolean visible) {
    }

    @Override
    public void initActionBar(Boolean tabsVisible) {
    }

    @Override
    public void hideActionButtons() {
    }

    @Override
    public void showActionButton(ChangeActionBarEvent.ActionType actionType) {
    }

    @UiHandler("logout")
    @SuppressWarnings("unused")
    void onLogoutClicked(ClickEvent event) {
        getUiHandlers().logout();
    }

    @UiHandler("menuBar")
    void onMenuItemChanged(ValueChangeEvent<MenuItem> event) {
        placeManager.revealPlace(new PlaceRequest(menuBar.getValue().getPlaceToken()));
    }
}
