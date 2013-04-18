package com.gwtplatform.carstore.cucumber.application;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gwtplatform.carstore.cucumber.util.TestParameters;

public class BasePage {
    protected final WebDriver webDriver;

    protected BasePage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public void getUrl(String url) {
        webDriver.get(url);
    }

    protected void chooseOkOnNextConfirm() {
        ((JavascriptExecutor) webDriver).executeScript("window.confirm = function(msg){return true;};");
    }

    protected void chooseCancelOnNextConfirm() {
        ((JavascriptExecutor) webDriver).executeScript("window.confirm = function(msg){return false;};");
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

    protected void waitUntilElementIsDetached(WebElement element) {
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
