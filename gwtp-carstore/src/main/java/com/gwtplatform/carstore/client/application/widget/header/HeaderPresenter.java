package com.gwtplatform.carstore.client.application.widget.header;

import java.util.logging.Logger;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.carstore.client.application.event.ActionBarEvent;
import com.gwtplatform.carstore.client.application.event.ActionBarVisibilityEvent;
import com.gwtplatform.carstore.client.application.event.ChangeActionBarEvent;
import com.gwtplatform.carstore.client.application.event.DisplayMessageEvent;
import com.gwtplatform.carstore.client.application.event.GoBackEvent;
import com.gwtplatform.carstore.client.application.event.UserLoginEvent;
import com.gwtplatform.carstore.client.application.event.ChangeActionBarEvent.ActionType;
import com.gwtplatform.carstore.client.application.login.LoginPresenter;
import com.gwtplatform.carstore.client.application.widget.message.Message;
import com.gwtplatform.carstore.client.application.widget.message.MessageStyle;
import com.gwtplatform.carstore.client.security.CurrentUser;
import com.gwtplatform.carstore.shared.dispatch.LogoutAction;
import com.gwtplatform.carstore.shared.dispatch.LogoutResult;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.DefaultPlace;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

public class HeaderPresenter extends PresenterWidget<HeaderPresenter.MyView> implements HeaderUiHandlers,
        UserLoginEvent.UserLoginHandler, ChangeActionBarEvent.ChangeActionBarHandler,
        ActionBarVisibilityEvent.ActionBarVisibilityHandler {
    public interface MyView extends View, HasUiHandlers<HeaderUiHandlers> {
        void enableUserOptions(CurrentUser currentUser);

        void disableUserOptions();

        void showActionBar(Boolean visible);

        void initActionBar(Boolean tabsVisible);

        void hideActionButtons();

        void showActionButton(ActionType actionType);
    }

    private static final Logger logger = Logger.getLogger(HeaderPresenter.class.getName());

    private final DispatchAsync dispatchAsync;
    private final String defaultPlaceNameToken;
    private final PlaceManager placeManager;
    private final CurrentUser currentUser;
    private final HeaderMessages messages;

    @Inject
    public HeaderPresenter(final EventBus eventBus, final MyView view, final DispatchAsync dispatchAsync,
            @DefaultPlace final String defaultPlaceNameToken, final PlaceManager placeManager,
            final CurrentUser currentUser, final HeaderMessages messages) {
        super(eventBus, view);

        this.dispatchAsync = dispatchAsync;
        this.defaultPlaceNameToken = defaultPlaceNameToken;
        this.placeManager = placeManager;
        this.currentUser = currentUser;
        this.messages = messages;
        
        getView().setUiHandlers(this);
    }

    @Override
    public void logout() {
        dispatchAsync.execute(new LogoutAction(), new AsyncCallback<LogoutResult>() {
            @Override
            public void onFailure(Throwable caught) {
                DisplayMessageEvent.fire(HeaderPresenter.this, new Message(messages.errorLoggingOut(),
                        MessageStyle.ERROR));
            }

            @Override
            public void onSuccess(LogoutResult result) {
                onLogoutSuccess();
            }
        });
    }

    @Override
    public void onLogin(UserLoginEvent event) {
        getView().enableUserOptions(currentUser);
    }

    @Override
    public void onActionBarVisible(ActionBarVisibilityEvent event) {
        getView().showActionBar(event.isVisible());
    }

    @Override
    public void onChangeActionBar(ChangeActionBarEvent event) {
        getView().initActionBar(event.getTabsVisible());
        getView().hideActionButtons();
        for (ActionType actionType : event.getActions()) {
            getView().showActionButton(actionType);
        }
    }

    @Override
    public void onAction(ActionType actionType) {
        String sourceToken = placeManager.getCurrentPlaceRequest().getNameToken();
        ActionBarEvent.fire(this, actionType, sourceToken);
    }

    @Override
    public void onGoBack() {
        GoBackEvent.fire(this);
    }

    @Override
    protected void onBind() {
        addRegisteredHandler(UserLoginEvent.getType(), this);
        addRegisteredHandler(ActionBarVisibilityEvent.getType(), this);
        addRegisteredHandler(ChangeActionBarEvent.getType(), this);

        if (currentUser.isLoggedIn()) {
            getView().enableUserOptions(currentUser);
        }
    }

    private void onLogoutSuccess() {
        resetLoggedInCookie();

        currentUser.reset();
        getView().disableUserOptions();

        PlaceRequest placeRequest = new PlaceRequest(defaultPlaceNameToken);
        placeManager.revealPlace(placeRequest);
    }

    private void resetLoggedInCookie() {
        Cookies.removeCookie(LoginPresenter.LOGIN_COOKIE_NAME);

        logger.info("HeaderPresenter.resetLoggedInCookie(): The cookie was removed from client.");
    }
}
