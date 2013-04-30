package com.gwtplatform.carstore.cucumber.application.widgets;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import com.google.common.base.Strings;
import com.gwtplatform.carstore.cucumber.application.BasePage;
import com.gwtplatform.carstore.cucumber.util.ByDebugId;
import com.gwtplatform.carstore.cucumber.util.FindByDebugId;

public class MessageWidgetPage extends BasePage {
    @FindByDebugId("successMessage")
    private WebElement successMessage;
    @FindByDebugId("errorMessage")
    private WebElement errorMessage;

    public Boolean hasSuccessMessage() {
        return hasSuccessMessage("");
    }

    public Boolean hasSuccessMessage(String message) {
        return messageContains("successMessage", message) && messageIsVisible(successMessage);
    }

    public Boolean hasErrorMessage() {
        return messageIsVisible(errorMessage);
    }

    public void hideSuccessMessage() {
        hideMessage(successMessage);
    }

    public void hideErrorMessage() {
        hideMessage(errorMessage);
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
}
