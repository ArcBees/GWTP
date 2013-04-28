/*
 * Copyright (c) 2013 by Zafin Labs, All rights reserved.
 * This source code, and resulting software, is the confidential and proprietary information
 * ("Proprietary Information") and is the intellectual property ("Intellectual Property")
 * of Zafin Labs ("The Company"). You shall not disclose such Proprietary Information and
 * shall use it only in accordance with the terms and conditions of any and all license
 * agreements you have entered into with The Company.
 */

package com.gwtplatform.carstore.cucumber.util;

import java.lang.reflect.Field;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.Annotations;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.gwt.user.client.ui.UIObject;

public class CarStoreElementLocator implements ElementLocator {
    private final WebDriver webDriver;

    private boolean shouldCache;
    private By by;
    private long timeout;
    private WebElement cachedElement;
    private List<WebElement> cachedElementList;

    public CarStoreElementLocator(WebDriver webDriver, Field field) {
        this.webDriver = webDriver;

        processAnnotations(field);
    }

    @Override
    public WebElement findElement() {
        if (cachedElement != null && shouldCache) {
            return cachedElement;
        }

        WebElement element = doFindElement();
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

        List<WebElement> elements = doFindElements();
        if (shouldCache) {
            cachedElementList = elements;
        }

        return elements;
    }

    private WebElement doFindElement() {
        if (timeout > 0) {
            return new WebDriverWait(webDriver, timeout)
                    .until(ExpectedConditions.presenceOfElementLocated(by));
        } else {
            return webDriver.findElement(by);
        }
    }

    private List<WebElement> doFindElements() {
        if (timeout > 0) {
            return new WebDriverWait(webDriver, timeout)
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
        } else {
            return webDriver.findElements(by);
        }
    }

    private void processAnnotations(Field field) {
        Annotations annotations = new Annotations(field);
        processFindBy(field, annotations);
        processWaitTimeout(field);
        shouldCache = annotations.isLookupCached();
    }

    private void processWaitTimeout(Field field) {
        LongImplicitWait longImplicitWait = field.getAnnotation(LongImplicitWait.class);
        if (longImplicitWait != null) {
            timeout = longImplicitWait.timeout();
        }
    }

    private void processFindBy(Field field, Annotations annotations) {
        FindByDebugId findByDebugId = field.getAnnotation(FindByDebugId.class);
        if (findByDebugId != null) {
            by = By.id(UIObject.DEBUG_ID_PREFIX + findByDebugId.value());
        } else {
            by = annotations.buildBy();
        }
    }
}
