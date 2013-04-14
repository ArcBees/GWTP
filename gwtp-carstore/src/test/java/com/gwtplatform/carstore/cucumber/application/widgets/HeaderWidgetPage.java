package com.gwtplatform.carstore.cucumber.application.widgets;

import javax.inject.Inject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.gwtplatform.carstore.cucumber.application.BasePage;
import com.gwtplatform.carstore.cucumber.util.FindByDebugId;

import static com.google.gwt.user.client.ui.UIObject.DEBUG_ID_PREFIX;

public class HeaderWidgetPage extends BasePage {
    @FindByDebugId("logout")
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
