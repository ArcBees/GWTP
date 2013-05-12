/**
 * Copyright 2013 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

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
