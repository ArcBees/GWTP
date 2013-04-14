package com.gwtplatform.carstore.cucumber.application.widgets;

import javax.inject.Inject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.gwtplatform.carstore.cucumber.application.BasePage;

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
        waitUntilElementIsVisible(logout);
        logout.click();
    }
}
