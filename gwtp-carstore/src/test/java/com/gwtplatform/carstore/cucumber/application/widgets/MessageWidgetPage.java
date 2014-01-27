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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import com.google.common.base.Strings;
import com.gwtplatform.carstore.cucumber.application.BasePage;
import com.gwtplatform.carstore.cucumber.util.ByDebugId;

public class MessageWidgetPage extends BasePage {
    public Boolean hasSuccessMessage() {
        return hasSuccessMessage("");
    }

    public Boolean hasSuccessMessage(String message) {
        return messageContains("successMessage", message) && messageIsVisible(getSuccessMessage());
    }

    public Boolean hasErrorMessage() {
        return messageIsVisible(getErrorMessage());
    }

    public void hideSuccessMessage() {
        hideMessage(getSuccessMessage());
    }

    public void hideErrorMessage() {
        hideMessage(getErrorMessage());
    }

    public void waitUntilSuccessMessageIsHidden(String message) {
        messageContains("successMessage", message);
        waitUntilElementIsDetached(getSuccessMessage());
    }

    private Boolean messageIsVisible(WebElement element) {
        try {
            waitUntilElementIsVisible(element);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    private boolean messageContains(String debugId, String message) {
        WebElement container = webDriver.findElement(ByDebugId.id(debugId));
        while (container != null) {
            WebElement content = container.findElement(By.cssSelector("span"));
            if (!Strings.isNullOrEmpty(content.getText()) && content.getText().contains(message)) {
                return true;
            }
            hideMessage(container);
            container = getMessageContainer(ByDebugId.id(debugId));
        }

        return false;
    }

    private void hideMessage(WebElement message) {
        WebElement close = message.findElement(By.cssSelector("span:last-child"));
        close.click();
        waitUntilElementIsDetached(message);
    }

    private WebElement getMessageContainer(By by) {
        try {
            return webDriver.findElement(by);
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private WebElement getSuccessMessage() {
        return waitUntilElementIsVisible(ByDebugId.id("successMessage"));
    }

    private WebElement getErrorMessage() {
        return waitUntilElementIsVisible(ByDebugId.id("errorMessage"));
    }
}
