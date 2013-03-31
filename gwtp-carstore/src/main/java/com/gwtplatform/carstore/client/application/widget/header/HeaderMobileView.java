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
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ValuePicker;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.carstore.client.application.event.ChangeActionBarEvent.ActionType;
import com.gwtplatform.carstore.client.application.renderer.EnumCell;
import com.gwtplatform.carstore.client.resources.MobileNavigationListStyle;
import com.gwtplatform.carstore.client.security.CurrentUser;
import com.gwtplatform.carstore.shared.domain.User;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

public class HeaderMobileView extends ViewWithUiHandlers<HeaderUiHandlers>
        implements HeaderPresenter.MyView, UserInfoPopup.Handler {
    public interface Binder extends UiBinder<Widget, HeaderMobileView> {
    }

    @UiField
    HTMLPanel actionBar;
    @UiField
    Button back;
    @UiField
    Button done;
    @UiField
    Button add;
    @UiField
    Button delete;
    @UiField
    Button edit;
    @UiField
    Button menu;
    @UiField(provided = true)
    ValuePicker<MenuItem> menuBar;

    private final UserInfoPopup userInfoPopup;
    private final PlaceManager placeManager;

    @Inject
    public HeaderMobileView(final Binder uiBinder, final MobileNavigationListStyle listResources,
                            final PlaceManager placeManager, final UserInfoPopup userInfoPopup) {
        this.placeManager = placeManager;
        this.userInfoPopup = userInfoPopup;

        CellList<MenuItem> placeList = new CellList<MenuItem>(new EnumCell<MenuItem>(), listResources);
        menuBar = new ValuePicker<MenuItem>(placeList);

        initWidget(uiBinder.createAndBindUi(this));

        menuBar.setAcceptableValues(Arrays.asList(MenuItem.values()));
        menuBar.setValue(MenuItem.MANUFACTURER);
        userInfoPopup.setHandler(this);
    }

    @Override
    public void showActionBar(Boolean visible) {
        actionBar.setVisible(visible);
        menuBar.setVisible(visible);
    }

    @Override
    public void initActionBar(Boolean tabsVisible) {
        back.setVisible(!tabsVisible);
        menuBar.setVisible(tabsVisible);
    }

    @Override
    public void hideActionButtons() {
        done.setVisible(false);
        edit.setVisible(false);
        add.setVisible(false);
        delete.setVisible(false);
    }

    @Override
    public void showActionButton(ActionType actionType) {
        switch (actionType) {
            case ADD:
                add.setVisible(true);
                break;
            case DONE:
                done.setVisible(true);
                break;
            case UPDATE:
                edit.setVisible(true);
                break;
            case DELETE:
                delete.setVisible(true);
                break;
        }
    }

    @Override
    public void enableUserOptions(CurrentUser currentUser) {
        menu.setVisible(true);
        User user = currentUser.getUser();
        userInfoPopup.setUsername(user.getFirstName());
    }

    @Override
    public void disableUserOptions() {
        menu.setVisible(false);
    }

    @Override
    public void onLogout() {
        getUiHandlers().logout();
    }

    @UiHandler("menuBar")
    void onMenuItemChanged(ValueChangeEvent<MenuItem> event) {
        placeManager.revealPlace(new PlaceRequest(menuBar.getValue().getPlaceToken()));
    }

    @UiHandler("home")
    void onHomeClicked(ClickEvent event) {
        if (back.isVisible()) {
            getUiHandlers().onGoBack();
        }
    }

    @UiHandler("back")
    void onBackClicked(ClickEvent event) {
        getUiHandlers().onGoBack();
    }

    @UiHandler("done")
    void onDoneClicked(ClickEvent event) {
        getUiHandlers().onAction(ActionType.DONE);
    }

    @UiHandler("edit")
    void onUpdateClicked(ClickEvent event) {
        getUiHandlers().onAction(ActionType.UPDATE);
    }

    @UiHandler("add")
    void onAddClicked(ClickEvent event) {
        getUiHandlers().onAction(ActionType.ADD);
    }

    @UiHandler("delete")
    void onDeleteClicked(ClickEvent event) {
        getUiHandlers().onAction(ActionType.DELETE);
    }

    @UiHandler("menu")
    void onMenuClicked(ClickEvent event) {
        final Widget widget = (Widget) event.getSource();
        userInfoPopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            @Override
            public void setPosition(int offsetWidth, int offsetHeight) {
                int left = widget.getAbsoluteLeft() - (userInfoPopup.getOffsetWidth() - widget.getOffsetWidth());
                int top = widget.getAbsoluteTop() + widget.getOffsetHeight() + 10;
                userInfoPopup.setPopupPosition(left, top);
            }
        });
    }
}
