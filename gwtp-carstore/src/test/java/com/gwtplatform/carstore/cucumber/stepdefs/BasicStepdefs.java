package com.gwtplatform.carstore.cucumber.stepdefs;

import javax.inject.Inject;

import org.openqa.selenium.WebDriver;

import com.google.common.base.Function;
import com.gwtplatform.carstore.cucumber.application.ApplicationPage;
import com.gwtplatform.carstore.cucumber.application.login.LoginPage;

import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.runtime.PendingException;

import static org.junit.Assert.assertTrue;

public class BasicStepdefs {
    private static final String baseUrl = "http://127.0.0.1:8888/";
    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "qwerty";
    private static final String INVALID_USERNAME = "--";
    private static final String INVALID_PASSWORD = "--";

    private final WebDriver webDriver;
    private final LoginPage loginPage;
    private final ApplicationPage applicationPage;

    @Inject
    public BasicStepdefs(WebDriver webDriver,
            LoginPage loginPage,
            ApplicationPage applicationPage) {
        this.webDriver = webDriver;
        this.loginPage = loginPage;
        this.applicationPage = applicationPage;
    }

    @After
    public void cleanup() {
        webDriver.quit();
    }

    @Given("^I navigate to the (\\S+) page$")
    public void iNavigateTo(String nameToken) {
        String url = baseUrl + "#" + nameToken;

        applicationPage.getUrl(url);
        applicationPage.waitUntilDomIsLoaded(nameToken);
    }

    @Given("^I'm logged in$")
    public void iAmLoggedIn() {
        iNavigateTo("login");
        enterValidCredential("valid");
        applicationPage.waitUntilDomIsLoaded("manufacturer");
    }

    @When("^I enter (\\S+) credential$")
    public void enterValidCredential(String valid) {
        if (valid.equals("valid")) {
            loginPage.setUsername(VALID_USERNAME);
            loginPage.setPassword(VALID_PASSWORD);
        } else {
            loginPage.setUsername(INVALID_USERNAME);
            loginPage.setPassword(INVALID_PASSWORD);
        }
        loginPage.submitLoginForm();
    }

    @Then("^I should be on the (\\S+) page$")
    public void iShouldBeOnThePage(String nameToken) throws Throwable {
        String url = baseUrl + "#" + nameToken;

        applicationPage.waitUntilDomIsLoaded(nameToken);

        assertTrue(webDriver.getCurrentUrl().startsWith(url));
    }
}
