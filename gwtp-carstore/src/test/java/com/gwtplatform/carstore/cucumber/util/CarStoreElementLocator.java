package com.gwtplatform.carstore.cucumber.util;

import java.lang.reflect.Field;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.Annotations;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import com.google.gwt.user.client.ui.UIObject;

public class CarStoreElementLocator implements ElementLocator {
    private final SearchContext searchContext;
    private final boolean shouldCache;
    private final By by;
    private WebElement cachedElement;
    private List<WebElement> cachedElementList;

    public CarStoreElementLocator(SearchContext searchContext, Field field) {
        this.searchContext = searchContext;

        Annotations annotations = new Annotations(field);
        shouldCache = annotations.isLookupCached();

        FindByDebugId findByDebugId = field.getAnnotation(FindByDebugId.class);
        by = By.id(UIObject.DEBUG_ID_PREFIX + findByDebugId.value());
    }

    @Override
    public WebElement findElement() {
        if (cachedElement != null && shouldCache) {
            return cachedElement;
        }

        WebElement element = searchContext.findElement(by);
        if (shouldCache) {
            cachedElement = element;
        }

        return element;
    }

    @Override
    public List<WebElement> findElements() {
        if (cachedElementList != null && shouldCache) {
            return cachedElementList;
        }

        List<WebElement> elements = searchContext.findElements(by);
        if (shouldCache) {
            cachedElementList = elements;
        }

        return elements;
    }
}
