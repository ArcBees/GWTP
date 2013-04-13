package com.gwtplatform.carstore.cucumber.application;

import com.gwtplatform.carstore.client.application.testutils.TestParameters;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BasePage {
    protected final WebDriver webDriver;

    protected BasePage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public void getUrl(String url) {
        webDriver.get(url);
    }

    public void closeUrl() {
        webDriver.close();
    }

    protected WebElement waitUntilElementIsLoaded(String id) {
        return waitUntilElementIsLoaded(By.id(id));
    }

    protected WebElement waitUntilElementIsLoaded(By locator) {
        return waitUntilElementIsLoaded(webDriver, locator);
    }

    protected void waitUntilElementIsVisible(WebElement element) {
        webDriverWait().until(ExpectedConditions.visibilityOf(element));
    }

    protected void waitUntilElementIsDettached(WebElement element) {
        webDriverWait().until(ExpectedConditions.stalenessOf(element));
    }

    protected WebElement waitUntilElementIsLoaded(final SearchContext parent, final By locator) {
        return webDriverWait().until(new ExpectedCondition<WebElement>() {
            @Override
            public WebElement apply(WebDriver input) {
                return parent.findElement(locator);
            }
        });
    }

    private WebDriverWait webDriverWait() {
        return new WebDriverWait(webDriver, TestParameters.TIME_OUT_IN_SECONDS);
    }
}
