package com.gwtplatform.carstore.cucumber.application.widgets;

import javax.inject.Inject;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.gwtplatform.carstore.cucumber.application.BasePage;
import com.gwtplatform.carstore.cucumber.util.FindByDebugId;

public class MessageWidgetPage extends BasePage {
    @FindByDebugId("successMessage")
    private WebElement successMessage;
    @FindByDebugId("errorMessage")
    private WebElement errorMessage;

    @Inject
    public MessageWidgetPage(WebDriver webDriver) {
        super(webDriver);
    }

    public Boolean hasSuccessMessage() {
        return messageIsVisible(successMessage);
    }

    public Boolean hasErrorMessage() {
        return messageIsVisible(errorMessage);
    }

    private Boolean messageIsVisible(WebElement element) {
        try {
            waitUntilElementIsVisible(element);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
