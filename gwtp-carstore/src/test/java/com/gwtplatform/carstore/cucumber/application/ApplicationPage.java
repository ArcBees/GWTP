package com.gwtplatform.carstore.cucumber.application;

import javax.inject.Inject;

import org.openqa.selenium.TimeoutException;

import com.gwtplatform.carstore.client.application.widget.header.MenuItem;
import com.gwtplatform.carstore.cucumber.application.widgets.HeaderWidgetPage;
import com.gwtplatform.carstore.cucumber.application.widgets.MessageWidgetPage;
import com.gwtplatform.carstore.cucumber.util.TestParameters;

import static com.google.gwt.user.client.ui.UIObject.DEBUG_ID_PREFIX;

public class ApplicationPage extends BasePage {
    private final HeaderWidgetPage headerWidgetPage;
    private final MessageWidgetPage messageWidgetPage;

    @Inject
    ApplicationPage(
            HeaderWidgetPage headerWidgetPage,
            MessageWidgetPage messageWidgetPage) {
        this.headerWidgetPage = headerWidgetPage;
        this.messageWidgetPage = messageWidgetPage;
    }

    public Boolean waitUntilDomIsLoaded(String nameToken) {
        try {
            waitUntilPlaceIsLoaded(nameToken);
            waitUntilElementIsLoaded(DEBUG_ID_PREFIX + "dom");
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void navigateTo(String page) {
        MenuItem menuItem = getMenuItem(page);
        if (menuItem != null) {
            headerWidgetPage.navigateTo(page);
            waitUntilDomIsLoaded(menuItem.getPlaceToken());
        } else {
            getUrl(TestParameters.BASE_URL + "#" + page);
        }
    }

    public boolean successMessageIsPresent(String message) {
        return messageWidgetPage.hasSuccessMessage(message);
    }

    private MenuItem getMenuItem(String page) {
        for (MenuItem menuItem : MenuItem.values()) {
            if (menuItem.toString().equals(page)) {
                return menuItem;
            }
        }

        return null;
    }
}
