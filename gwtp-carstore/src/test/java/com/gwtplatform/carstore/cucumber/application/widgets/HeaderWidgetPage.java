package com.gwtplatform.carstore.cucumber.application.widgets;

import org.openqa.selenium.WebElement;

import com.gwtplatform.carstore.cucumber.application.BasePage;
import com.gwtplatform.carstore.cucumber.util.FindByDebugId;

public class HeaderWidgetPage extends BasePage {
    @FindByDebugId("logout")
    private WebElement logout;

    public void clickOnLogOut() {
        waitUntilElementIsVisible(logout);
        logout.click();
    }
}
