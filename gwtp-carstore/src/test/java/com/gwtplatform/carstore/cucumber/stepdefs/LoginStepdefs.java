package com.gwtplatform.carstore.cucumber.stepdefs;

import com.gwtplatform.carstore.cucumber.application.ApplicationPage;
import com.gwtplatform.carstore.cucumber.application.login.LoginPage;
import com.gwtplatform.carstore.cucumber.application.widgets.HeaderWidgetPage;

import cucumber.annotation.en.Then;
import cucumber.annotation.en.When;

import javax.inject.Inject;

import static org.junit.Assert.assertTrue;

public class LoginStepdefs {
    private final HeaderWidgetPage headerWidgetPage;
    private final LoginPage loginPage;
    private final ApplicationPage applicationPage;

    @Inject
    public LoginStepdefs(BasicStepdefs basicStepdefs, HeaderWidgetPage headerWidgetPage, LoginPage loginPage,
            ApplicationPage applicationPage) {
        this.headerWidgetPage = headerWidgetPage;
        this.loginPage = loginPage;
        this.applicationPage = applicationPage;
    }

    @When("^I click on logout$")
    public void IClickOnLogout() {
        headerWidgetPage.clickOnLogOut();
    }

    @Then("^I'm connected$")
    public void ImConnected() {
        assertTrue(applicationPage.waitUntilDomIsLoaded("manufacturer"));
    }

    @Then("^I'm disconnected$")
    public void iAmDisconnected() {
        loginPage.isOnLoginPage();
    }
}
