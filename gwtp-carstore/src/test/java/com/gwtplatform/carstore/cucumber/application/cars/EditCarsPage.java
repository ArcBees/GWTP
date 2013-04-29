package com.gwtplatform.carstore.cucumber.application.cars;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.gwtplatform.carstore.cucumber.application.BasePage;
import com.gwtplatform.carstore.cucumber.util.FindByDebugId;

public class EditCarsPage extends BasePage {
    @FindByDebugId("cars-tab-panel")
    private WebElement carsTabs;

    @FindByDebugId("car-model-input")
    private WebElement carModelInput;

    @FindByDebugId("car-manufacturer-input")
    private WebElement carManufacturerInput;

    @FindByDebugId("car-properties-editor")
    private WebElement carPropertiesEditor;

    @FindByDebugId("car-save")
    private WebElement saveButton;

    @FindByDebugId("car-close")
    private WebElement closeButton;

    public String getCurrentCarTabName() {
        WebElement selectedTab = carsTabs.findElement(By.className("gwt-TabBarItem-selected"));
        WebElement tabNameContainer = selectedTab.findElement(By.cssSelector("span"));
        return tabNameContainer.getText();
    }

    public void setCarManufacturer(String manufacturer) {
        Select manufacturerSelect = new Select(carManufacturerInput);
        manufacturerSelect.selectByVisibleText(manufacturer);
    }

    public void setCarModel(String model) {
        carModelInput.clear();
        carModelInput.sendKeys(model);
    }

    public void clickOnSave() {
        saveButton.click();
    }

    public void clickOnClose() {
        closeButton.click();
    }
}
