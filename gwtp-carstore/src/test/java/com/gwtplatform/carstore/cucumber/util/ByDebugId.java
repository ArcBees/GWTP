package com.gwtplatform.carstore.cucumber.util;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.google.gwt.user.client.ui.UIObject;

public class ByDebugId extends By {
    private final String debugId;

    public static By id(String debugId) {
        return new ByDebugId(debugId);
    }

    public ByDebugId(String debugId) {
        this.debugId = debugId;
    }

    @Override
    public WebElement findElement(SearchContext context) {
        return context.findElement(By.id(UIObject.DEBUG_ID_PREFIX + debugId));
    }

    @Override
    public List<WebElement> findElements(SearchContext context) {
        throw new UnsupportedOperationException();
    }
}
