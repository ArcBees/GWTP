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

package com.gwtplatform.carstore.cucumber.application.widgets;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.gwtplatform.carstore.cucumber.application.BasePage;
import com.gwtplatform.carstore.cucumber.util.ByDebugId;
import com.gwtplatform.carstore.cucumber.util.FindByDebugId;

public class HeaderWidgetPage extends BasePage {
    public void clickOnLogOut() {
        getLogoutButton().click();
    }

    public void navigateTo(String linkText) {
        WebElement link = getMenuBar().findElement(By.xpath("//div[text()=\"" + linkText + "\"]"));
        link.click();
    }

    public void waitUntilLogoutIsClickable() {
        getLogoutButton();
    }

    private WebElement getMenuBar() {
        return waitUntilElementIsVisible(ByDebugId.id("menubar"));
    }

    private WebElement getLogoutButton() {
        return waitUntilElementIsClickable(ByDebugId.id("logout"));
    }
}
