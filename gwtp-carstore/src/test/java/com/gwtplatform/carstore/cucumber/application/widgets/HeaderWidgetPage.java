package com.gwtplatform.carstore.cucumber.application.widgets;

import com.gwtplatform.carstore.cucumber.application.BasePage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import javax.inject.Inject;

import static com.google.gwt.user.client.ui.UIObject.DEBUG_ID_PREFIX;

public class HeaderWidgetPage extends BasePage {
    private static final String LOGOUT_ID = DEBUG_ID_PREFIX + "logout";

    @FindBy(id = LOGOUT_ID)
    private WebElement logout;

    @Inject
    public HeaderWidgetPage(WebDriver webDriver) {
        super(webDriver);
    }

    public void clickOnLogOut() {
        webDriverWait().until(ExpectedConditions.visibilityOf(logout));
        logout.click();
    }
}
