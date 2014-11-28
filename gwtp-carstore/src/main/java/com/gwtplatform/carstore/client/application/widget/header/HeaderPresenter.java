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
import com.gwtplatform.carstore.client.application.event.DisplayMessageEvent;
import com.gwtplatform.carstore.client.application.event.UserLoginEvent;
import com.gwtplatform.carstore.client.application.login.LoginPresenter;
import com.gwtplatform.carstore.client.application.widget.message.Message;
import com.gwtplatform.carstore.client.application.widget.message.MessageStyle;
import com.gwtplatform.carstore.client.resources.HeaderMessages;
import com.gwtplatform.carstore.client.security.CurrentUser;
import com.gwtplatform.carstore.shared.api.SessionResource;
import com.gwtplatform.dispatch.rest.delegates.client.ResourceDelegate;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.proxy.NavigationEvent;
import com.gwtplatform.mvp.client.proxy.NavigationHandler;
import com.gwtplatform.mvp.client.proxy.PlaceManager;

public class HeaderPresenter extends PresenterWidget<HeaderPresenter.MyView>
        implements HeaderUiHandlers, UserLoginEvent.UserLoginHandler, NavigationHandler {

    public interface MyView extends View, HasUiHandlers<HeaderUiHandlers> {
        void enableUserOptions(CurrentUser currentUser);

        void disableUserOptions();

        void setMenuItemActive(String nameToken);
    }

    private static final Logger logger = Logger.getLogger(HeaderPresenter.class.getName());

    private final ResourceDelegate<SessionResource> sessionDelegate;
    private final PlaceManager placeManager;
    private final CurrentUser currentUser;
    private final HeaderMessages messages;

    @Inject
    HeaderPresenter(
            EventBus eventBus,
            MyView view,
            ResourceDelegate<SessionResource> sessionDelegate,
            PlaceManager placeManager,
            CurrentUser currentUser,
            HeaderMessages messages) {
        super(eventBus, view);

        this.sessionDelegate = sessionDelegate;
        this.placeManager = placeManager;
        this.currentUser = currentUser;
        this.messages = messages;

        getView().setUiHandlers(this);
    }

    @Override
    public void logout() {
        sessionDelegate
                .withCallback(new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        DisplayMessageEvent.fire(HeaderPresenter.this, new Message(messages.errorLoggingOut(),
                                MessageStyle.ERROR));
                    }

                    @Override
                    public void onSuccess(Void nothing) {
                        onLogoutSuccess();
                    }
                })
                .logout();
    }

    @Override
    public void onLogin(UserLoginEvent event) {
        getView().enableUserOptions(currentUser);
    }

    @Override
    public void onNavigation(NavigationEvent navigationEvent) {
        getView().setMenuItemActive(navigationEvent.getRequest().getNameToken());
    }

    @Override
    protected void onBind() {
        addRegisteredHandler(UserLoginEvent.getType(), this);
        addRegisteredHandler(NavigationEvent.getType(), this);

        getView().enableUserOptions(currentUser);
        getView().setMenuItemActive(placeManager.getCurrentPlaceRequest().getNameToken());
    }

    private void onLogoutSuccess() {
        resetLoggedInCookie();

        currentUser.reset();
        getView().disableUserOptions();

        placeManager.revealDefaultPlace();
    }

    private void resetLoggedInCookie() {
        Cookies.removeCookie(LoginPresenter.LOGIN_COOKIE_NAME);

        logger.info("HeaderPresenter.resetLoggedInCookie(): The cookie was removed from client.");
    }
}
