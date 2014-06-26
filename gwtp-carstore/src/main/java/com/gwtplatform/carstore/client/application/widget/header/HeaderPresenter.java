/**
 * Copyright 2013 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.gwtplatform.carstore.client.application.widget.header;

import java.util.logging.Logger;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.carstore.client.application.event.ActionBarEvent;
import com.gwtplatform.carstore.client.application.event.ActionBarVisibilityEvent;
import com.gwtplatform.carstore.client.application.event.ChangeActionBarEvent;
import com.gwtplatform.carstore.client.application.event.ChangeActionBarEvent.ActionType;
import com.gwtplatform.carstore.client.application.event.DisplayMessageEvent;
import com.gwtplatform.carstore.client.application.event.GoBackEvent;
import com.gwtplatform.carstore.client.application.event.UserLoginEvent;
import com.gwtplatform.carstore.client.application.login.LoginPresenter;
import com.gwtplatform.carstore.client.application.widget.message.Message;
import com.gwtplatform.carstore.client.application.widget.message.MessageStyle;
import com.gwtplatform.carstore.client.resources.HeaderMessages;
import com.gwtplatform.carstore.client.rest.SessionService;
import com.gwtplatform.carstore.client.security.CurrentUser;
import com.gwtplatform.dispatch.rest.shared.RestDispatch;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.DefaultPlace;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

public class HeaderPresenter extends PresenterWidget<HeaderPresenter.MyView>
        implements HeaderUiHandlers, UserLoginEvent.UserLoginHandler, ChangeActionBarEvent.ChangeActionBarHandler,
        ActionBarVisibilityEvent.ActionBarVisibilityHandler {

    public interface MyView extends View, HasUiHandlers<HeaderUiHandlers> {
        void enableUserOptions(CurrentUser currentUser);

        void disableUserOptions();

        void showActionBar(Boolean visible);

        void initActionBar(Boolean tabsVisible);

        void hideActionButtons();

        void showActionButton(ActionType actionType);

        void setMenuItem(MenuItem menuItem);
    }

    private static final Logger logger = Logger.getLogger(HeaderPresenter.class.getName());

    private final RestDispatch dispatchAsync;
    private final SessionService sessionService;
    private final String defaultPlaceNameToken;
    private final PlaceManager placeManager;
    private final CurrentUser currentUser;
    private final HeaderMessages messages;

    @Inject
    HeaderPresenter(EventBus eventBus,
                    MyView view,
                    RestDispatch dispatchAsync,
                    SessionService sessionService,
                    @DefaultPlace String defaultPlaceNameToken,
                    PlaceManager placeManager,
                    CurrentUser currentUser,
                    HeaderMessages messages) {
        super(eventBus, view);

        this.dispatchAsync = dispatchAsync;
        this.sessionService = sessionService;
        this.defaultPlaceNameToken = defaultPlaceNameToken;
        this.placeManager = placeManager;
        this.currentUser = currentUser;
        this.messages = messages;

        getView().setUiHandlers(this);
    }

    @Override
    public void logout() {
        dispatchAsync.execute(sessionService.logout(), new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                DisplayMessageEvent.fire(HeaderPresenter.this, new Message(messages.errorLoggingOut(),
                        MessageStyle.ERROR));
            }

            @Override
            public void onSuccess(Void nothing) {
                onLogoutSuccess();
            }
        });
    }

    @Override
    public void onLogin(UserLoginEvent event) {
        getView().enableUserOptions(currentUser);
        getView().setMenuItem(MenuItem.fromNameToken(getCurrentNameToken()));
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
        String sourceToken = getCurrentNameToken();
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

        PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken(defaultPlaceNameToken).build();
        placeManager.revealPlace(placeRequest);
    }

    private void resetLoggedInCookie() {
        Cookies.removeCookie(LoginPresenter.LOGIN_COOKIE_NAME);

        logger.info("HeaderPresenter.resetLoggedInCookie(): The cookie was removed from client.");
    }

    private String getCurrentNameToken() {
        return placeManager.getCurrentPlaceRequest().getNameToken();
    }
}
