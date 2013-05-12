/**
 * Copyright 2013 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

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
