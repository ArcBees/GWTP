package com.gwtplatform.carstore.cucumber.application.login;

import javax.inject.Inject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.gwtplatform.carstore.client.place.NameTokens;
import com.gwtplatform.carstore.cucumber.application.BasePage;

import static com.google.gwt.user.client.ui.UIObject.DEBUG_ID_PREFIX;

public class LoginPage extends BasePage {
    private static final String LOGIN_ID = DEBUG_ID_PREFIX + "login";

    @FindBy(id = DEBUG_ID_PREFIX + "username")
    private WebElement username;
    @FindBy(id = DEBUG_ID_PREFIX + "password")
    private WebElement password;
    @FindBy(id = LOGIN_ID)
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
