package com.arcbees.carsample.cucumber.application;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.arcbees.carsample.client.application.testutils.TestParameters;

public class BasePage {
    protected final WebDriver webDriver;

    protected BasePage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public WebDriverWait webDriverWait(){
        return new WebDriverWait(webDriver, TestParameters.TIME_OUT_IN_SECONDS);
    }
    
    public void getUrl(String url) {
      webDriver.get(url);
    }
    
    public void closeUrl() {
      webDriver.close();
    }
}
