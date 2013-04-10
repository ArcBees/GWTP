package com.gwtplatform.carstore.cucumber.application.widgets;

import com.gwtplatform.carstore.cucumber.application.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import javax.inject.Inject;

import static com.google.gwt.user.client.ui.UIObject.DEBUG_ID_PREFIX;

public class MessageWidgetPage extends BasePage {
    @Inject
    public MessageWidgetPage(WebDriver webDriver) {
        super(webDriver);
    }

    public Boolean hasSuccessMessage() {
        try {
            webDriverWait().until(ExpectedConditions.presenceOfElementLocated(By.id(DEBUG_ID_PREFIX + "successMessage")));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public Boolean hasErrorMessage() {
        try {
            webDriverWait().until(ExpectedConditions.presenceOfElementLocated(By.id(DEBUG_ID_PREFIX + "errorMessage")));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
