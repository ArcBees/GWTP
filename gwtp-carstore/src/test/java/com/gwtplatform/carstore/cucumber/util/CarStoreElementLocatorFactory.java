package com.gwtplatform.carstore.cucumber.util;

import java.lang.reflect.Field;

import javax.inject.Inject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.DefaultElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

public class CarStoreElementLocatorFactory implements ElementLocatorFactory {
    private final WebDriver driver;

    @Inject
    public CarStoreElementLocatorFactory(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public ElementLocator createLocator(Field field) {
        if (field.isAnnotationPresent(FindByDebugId.class)) {
            return new CarStoreElementLocator(driver, field);
        } else {
            return new DefaultElementLocator(driver, field);
        }
    }
}
