package com.gwtplatform.carstore.client.application.login;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.carstore.client.application.ApplicationPresenter;
import com.gwtplatform.carstore.client.application.event.ActionBarVisibilityEvent;
import com.gwtplatform.carstore.client.application.event.DisplayMessageEvent;
import com.gwtplatform.carstore.client.application.event.UserLoginEvent;
import com.gwtplatform.carstore.client.application.widget.message.Message;
import com.gwtplatform.carstore.client.application.widget.message.MessageStyle;
import com.gwtplatform.carstore.client.place.NameTokens;
import com.gwtplatform.carstore.client.security.CurrentUser;
import com.gwtplatform.carstore.shared.dispatch.ActionType;
import com.gwtplatform.carstore.shared.dispatch.LogInAction;
import com.gwtplatform.carstore.shared.dispatch.LogInResult;
import com.gwtplatform.carstore.shared.dto.CurrentUserDto;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;

public class LoginPresenter extends Presenter<LoginPresenter.MyView, LoginPresenter.MyProxy> implements LoginUiHandlers {
    public interface MyView extends View, HasUiHandlers<LoginUiHandlers> {
        void setLoginButtonEnabled(boolean enabled);
    }

    @ProxyStandard
    @NameToken(NameTokens.login)
    public interface MyProxy extends ProxyPlace<LoginPresenter> {
    }

    public static final String LOGIN_COOKIE_NAME = "LoggedInCookie";

    private static final Logger logger = Logger.getLogger(LoginPresenter.class.getName());
    private final PlaceManager placeManager;
    private final DispatchAsync dispatchAsync;
    private final CurrentUser currentUser;
    private final LoginMessages messages;

    @Inject
    public LoginPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
            DispatchAsync dispatchAsync, CurrentUser currentUser, LoginMessages messages) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.dispatchAsync = dispatchAsync;
        this.currentUser = currentUser;
        this.messages = messages;
        
        getView().setUiHandlers(this);
    }

    @Override
    public void login(String username, String password) {
        LogInAction loginAction = new LogInAction(username, password);
        callServerLoginAction(loginAction);
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, ApplicationPresenter.TYPE_SetMainContent, this);
    }

    @Override
    protected void onReveal() {
        ActionBarVisibilityEvent.fire(this, false);

        if (!Strings.isNullOrEmpty(getLoggedInCookie())) {
            tryLoggingInWithCookieFirst();
        }
    }

    private void callServerLoginAction(LogInAction loginAction) {
        dispatchAsync.execute(loginAction, new AsyncCallback<LogInResult>() {
            @Override
            public void onFailure(Throwable e) {
                DisplayMessageEvent.fire(LoginPresenter.this, new Message(messages.unableToContactServer(),
                        MessageStyle.ERROR));

                logger.log(Level.SEVERE, "callServerLoginAction(): Server failed to process login call.", e);
                e.printStackTrace();
            }

            @Override
            public void onSuccess(LogInResult result) {
                if (result.getCurrentUserDto().isLoggedIn()) {
                    setLoggedInCookie(result.getLoggedInCookie());
                }

                if (result.getActionType() == ActionType.VIA_COOKIE) {
                    onLoginCallSuceededForCookie(result.getCurrentUserDto());
                } else {
                    onLoginCallSuceeded(result.getCurrentUserDto());
                }
            }
        });
    }

    private void onLoginCallSuceededForCookie(CurrentUserDto currentUserDto) {
        getView().setLoginButtonEnabled(true);

        if (currentUserDto.isLoggedIn()) {
            onLoginCallSuceeded(currentUserDto);
        }
    }

    private void onLoginCallSuceeded(CurrentUserDto currentUserDto) {
        if (currentUserDto.isLoggedIn()) {
            currentUser.fromCurrentUserDto(currentUserDto);

            PlaceRequest homePlaceRequest = new PlaceRequest(NameTokens.getOnLoginDefaultPage());
            placeManager.revealPlace(homePlaceRequest);

            UserLoginEvent.fire(this);
            DisplayMessageEvent.fire(this, new Message(messages.onSuccessfulLogin(), MessageStyle.SUCCESS));
        } else {
            DisplayMessageEvent.fire(this, new Message(messages.invalidEmailOrPassword(), MessageStyle.ERROR));
        }
    }

    private void setLoggedInCookie(String value) {
        Cookies.removeCookie(LOGIN_COOKIE_NAME);

        Date expires = new Date();
        CalendarUtil.addDaysToDate(expires, 14);
        String domain = getDomain();
        String path = "/";
        boolean secure = false;
        Cookies.setCookie(LOGIN_COOKIE_NAME, value, expires, domain, path, secure);

        logger.info("LoginPresenter.setLoggedInCookie() Set client cookie=" + value);
    }

    private String getDomain() {
        String domain = GWT.getHostPageBaseURL();
        domain = domain.replaceAll(".*//", "");
        domain = domain.replaceAll("/", "");
        domain = domain.replaceAll(":.*", "");

        return domain;
    }

    private void tryLoggingInWithCookieFirst() {
        getView().setLoginButtonEnabled(false);
        LogInAction loginAction = new LogInAction(getLoggedInCookie());
        callServerLoginAction(loginAction);
    }

    private String getLoggedInCookie() {
        return Cookies.getCookie(LOGIN_COOKIE_NAME);
    }
}
