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
