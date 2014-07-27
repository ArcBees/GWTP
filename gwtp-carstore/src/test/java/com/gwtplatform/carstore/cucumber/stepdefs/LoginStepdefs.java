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

package com.gwtplatform.carstore.cucumber.stepdefs;

import javax.inject.Inject;

import com.gwtplatform.carstore.cucumber.application.ApplicationPage;
import com.gwtplatform.carstore.cucumber.application.UnauthorizedPage;
import com.gwtplatform.carstore.cucumber.application.login.LoginPage;
import com.gwtplatform.carstore.cucumber.application.widgets.HeaderWidgetPage;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.junit.Assert.assertTrue;

public class LoginStepdefs {
    private final HeaderWidgetPage headerWidgetPage;
    private final LoginPage loginPage;
    private final ApplicationPage applicationPage;
    private final UnauthorizedPage unauthorizedPage;

    @Inject
    LoginStepdefs(HeaderWidgetPage headerWidgetPage,
                  LoginPage loginPage,
                  ApplicationPage applicationPage,
                  UnauthorizedPage unauthorizedPage) {
        this.headerWidgetPage = headerWidgetPage;
        this.loginPage = loginPage;
        this.applicationPage = applicationPage;
        this.unauthorizedPage = unauthorizedPage;
    }

    @When("^I click on logout$")
    public void IClickOnLogout() {
        headerWidgetPage.clickOnLogOut();
    }

    @Then("^I'm connected$")
    public void ImConnected() {
        assertTrue(applicationPage.waitUntilDomIsLoaded("manufacturer"));
    }

    @Then("^I'm connected on the (\\S+) page$")
    public void ImConnectedOnThePage(String nameToken) {
        loginPage.waitUntilLoggedIn();
        assertTrue(applicationPage.waitUntilDomIsLoaded(nameToken));
    }

    @Then("^I'm disconnected$")
    public void iAmDisconnected() {
        loginPage.isOnLoginPage();
    }

    @When("^I click on the link to the login page$")
    public void IClickOnTheLinkToTheLoginPage() {
        unauthorizedPage.clickOnLink();
    }
}
