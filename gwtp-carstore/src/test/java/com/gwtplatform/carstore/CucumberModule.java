package com.gwtplatform.carstore;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.gwtplatform.carstore.cucumber.application.ApplicationPage;
import com.gwtplatform.carstore.cucumber.application.login.LoginPage;
import com.gwtplatform.carstore.cucumber.application.ratings.RatingPage;
import com.gwtplatform.carstore.cucumber.application.widgets.HeaderWidgetPage;
import com.gwtplatform.carstore.cucumber.application.widgets.MessageWidgetPage;
import com.gwtplatform.carstore.cucumber.util.CarStoreElementLocatorFactory;

public class CucumberModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ElementLocatorFactory.class).to(CarStoreElementLocatorFactory.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    WebDriver getDefaultWebDriver() {
        ChromeDriver chromeDriver = new ChromeDriver();
        chromeDriver.manage().timeouts().implicitlyWait(2L, TimeUnit.SECONDS);
        return chromeDriver;
    }

    @Provides
    ApplicationPage getApplicationPage(WebDriver webDriver, ElementLocatorFactory elementLocatorFactory) {
        return initPage(elementLocatorFactory, webDriver, ApplicationPage.class);
    }

    @Provides
    LoginPage getLoginPage(WebDriver webDriver, ElementLocatorFactory elementLocatorFactory) {
        return initPage(elementLocatorFactory, webDriver, LoginPage.class);
    }

    @Provides
    MessageWidgetPage getMessageWidgetPage(WebDriver webDriver, ElementLocatorFactory elementLocatorFactory) {
        return initPage(elementLocatorFactory, webDriver, MessageWidgetPage.class);
    }

    @Provides
    HeaderWidgetPage getHeaderWidgetPage(WebDriver webDriver, ElementLocatorFactory elementLocatorFactory) {
        return initPage(elementLocatorFactory, webDriver, HeaderWidgetPage.class);
    }

    @Provides
    RatingPage getRatingsPage(WebDriver webDriver, ElementLocatorFactory elementLocatorFactory) {
        return initPage(elementLocatorFactory, webDriver, RatingPage.class);
    }

    <T> T initPage(ElementLocatorFactory factory, WebDriver webDriver, Class<T> pageClass) {
        T page = instantiatePage(webDriver, pageClass);
        PageFactory.initElements(factory, page);
        return page;
    }

    <T> T instantiatePage(WebDriver webDriver, Class<T> pageClass) {
        try {
            try {
                Constructor<T> constructor = pageClass.getConstructor(WebDriver.class);
                return constructor.newInstance(webDriver);
            } catch (NoSuchMethodException e) {
                return pageClass.newInstance();
            }
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
