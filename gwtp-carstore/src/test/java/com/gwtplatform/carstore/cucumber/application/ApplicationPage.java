package com.gwtplatform.carstore.cucumber.application;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

import javax.inject.Inject;

import static com.google.gwt.user.client.ui.UIObject.DEBUG_ID_PREFIX;

public class ApplicationPage extends BasePage {
    @Inject
    public ApplicationPage(WebDriver webDriver) {
        super(webDriver);
    }

    public Boolean waitUntilDomIsLoaded(String nameToken) {
        try {
            waitUntilElementIsLoaded(DEBUG_ID_PREFIX + nameToken + "Panel");
            waitUntilElementIsLoaded(DEBUG_ID_PREFIX + "dom");
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
