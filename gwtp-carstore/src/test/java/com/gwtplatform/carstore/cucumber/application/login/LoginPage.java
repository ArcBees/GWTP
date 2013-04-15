package com.gwtplatform.carstore.cucumber.application.login;

import javax.inject.Inject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.gwtplatform.carstore.client.place.NameTokens;
import com.gwtplatform.carstore.cucumber.application.BasePage;
import com.gwtplatform.carstore.cucumber.util.FindByDebugId;

public class LoginPage extends BasePage {
    @FindByDebugId("username")
    private WebElement username;
    @FindByDebugId("password")
    private WebElement password;
    @FindByDebugId("login")
    private WebElement login;

    @Inject
    public LoginPage(WebDriver webDriver) {
        super(webDriver);
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
        return webDriver.getCurrentUrl().contains("#" + NameTokens.login);
    }
}
