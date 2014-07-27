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

package com.gwtplatform.carstore.cucumber.application.login;

import org.openqa.selenium.WebElement;

import com.google.inject.Inject;
import com.gwtplatform.carstore.client.place.NameTokens;
import com.gwtplatform.carstore.cucumber.application.BasePage;
import com.gwtplatform.carstore.cucumber.application.widgets.HeaderWidgetPage;
import com.gwtplatform.carstore.cucumber.util.FindByDebugId;

public class LoginPage extends BasePage {
    @FindByDebugId("username")
    private WebElement username;
    @FindByDebugId("password")
    private WebElement password;
    @FindByDebugId("login")
    private WebElement login;

    private final HeaderWidgetPage headerWidgetPage;

    @Inject
    LoginPage(HeaderWidgetPage headerWidgetPage) {
        this.headerWidgetPage = headerWidgetPage;
    }

    public void setUsername(String username) {
        waitUntilElementIsVisible(this.username);
        this.username.sendKeys(username);
    }

    public void setPassword(String password) {
        waitUntilElementIsVisible(this.password);
        this.password.sendKeys(password);
    }

    public void submitLoginForm() {
        waitUntilElementIsVisible(login);
        login.click();
    }

    public Boolean isOnLoginPage() {
        return webDriver.getCurrentUrl().contains("#" + NameTokens.LOGIN);
    }

    public void waitUntilLoggedIn() {
        headerWidgetPage.waitUntilLogoutIsClickable();
    }
}
