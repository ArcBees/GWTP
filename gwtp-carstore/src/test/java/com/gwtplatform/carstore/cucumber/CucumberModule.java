package com.gwtplatform.carstore.cucumber;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.gwtplatform.carstore.cucumber.application.BasePage;
import com.gwtplatform.carstore.cucumber.util.CarStoreElementLocatorFactory;

public class CucumberModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ElementLocatorFactory.class).to(CarStoreElementLocatorFactory.class).in(Singleton.class);
        requestStaticInjection(BasePage.class);
    }

    @Provides
    @Singleton
    WebDriver getDefaultWebDriver() {
        ChromeDriver chromeDriver = new ChromeDriver();
        chromeDriver.manage().timeouts().implicitlyWait(2L, TimeUnit.SECONDS);
        return chromeDriver;
    }
}
