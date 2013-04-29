package com.gwtplatform.carstore.cucumber.application.widgets;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.gwtplatform.carstore.cucumber.application.BasePage;
import com.gwtplatform.carstore.cucumber.util.FindByDebugId;

public class HeaderWidgetPage extends BasePage {
    @FindByDebugId("logout")
    private WebElement logout;

    @FindByDebugId("menubar")
    private WebElement menuBar;

    public void clickOnLogOut() {
        waitUntilElementIsVisible(logout);
        logout.click();
    }

    public void navigateTo(String linkText) {
        WebElement link = menuBar.findElement(By.xpath("//div[text()=\"" + linkText + "\"]"));
        link.click();
    }
}
