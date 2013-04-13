package com.gwtplatform.carstore.cucumber.application.widgets;

import com.gwtplatform.carstore.cucumber.application.BasePage;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

import javax.inject.Inject;

import static com.google.gwt.user.client.ui.UIObject.DEBUG_ID_PREFIX;

public class MessageWidgetPage extends BasePage {
    @Inject
    public MessageWidgetPage(WebDriver webDriver) {
        super(webDriver);
    }

    public Boolean hasSuccessMessage() {
        try {
            waitUntilElementIsLoaded(DEBUG_ID_PREFIX + "successMessage");
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public Boolean hasErrorMessage() {
        try {
            waitUntilElementIsLoaded(DEBUG_ID_PREFIX + "errorMessage");
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
