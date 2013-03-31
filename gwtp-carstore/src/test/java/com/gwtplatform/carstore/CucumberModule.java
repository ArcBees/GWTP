package com.gwtplatform.carstore;

import javax.inject.Singleton;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.gwtplatform.carstore.cucumber.application.ApplicationPage;
import com.gwtplatform.carstore.cucumber.application.login.LoginPage;
import com.gwtplatform.carstore.cucumber.application.ratings.RatingPage;
import com.gwtplatform.carstore.cucumber.application.widgets.HeaderWidgetPage;
import com.gwtplatform.carstore.cucumber.application.widgets.MessageWidgetPage;

public class CucumberModule extends AbstractModule {
    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    WebDriver getDefaultWebDriver() {
        return new ChromeDriver();
    }

    @Provides
    ApplicationPage getApplicationPage(WebDriver webDriver) {
        return PageFactory.initElements(webDriver, ApplicationPage.class);
    }

    @Provides
    LoginPage getLoginPage(WebDriver webDriver) {
        return PageFactory.initElements(webDriver, LoginPage.class);
    }

    @Provides
    MessageWidgetPage getMessageWidgetPage(WebDriver webDriver) {
        return PageFactory.initElements(webDriver, MessageWidgetPage.class);
    }

    @Provides
    HeaderWidgetPage getHeaderWidgetPage(WebDriver webDriver) {
        return PageFactory.initElements(webDriver, HeaderWidgetPage.class);
    }

    @Provides
    RatingPage getRatingsPage(WebDriver webDriver) {
        return PageFactory.initElements(webDriver, RatingPage.class);
    }
}
