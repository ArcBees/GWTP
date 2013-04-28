package com.gwtplatform.carstore.cucumber.application;

import org.openqa.selenium.TimeoutException;

import static com.google.gwt.user.client.ui.UIObject.DEBUG_ID_PREFIX;

public class ApplicationPage extends BasePage {
    public Boolean waitUntilDomIsLoaded(String nameToken) {
        try {
            waitUntilPlaceIsLoaded(nameToken);
            waitUntilElementIsLoaded(DEBUG_ID_PREFIX + "dom");
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
