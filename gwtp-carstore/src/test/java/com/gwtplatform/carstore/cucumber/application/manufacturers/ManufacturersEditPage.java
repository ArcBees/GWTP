/*
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.carstore.cucumber.application.manufacturers;

import org.openqa.selenium.WebElement;

import com.gwtplatform.carstore.cucumber.application.BasePage;
import com.gwtplatform.carstore.cucumber.util.FindByDebugId;

public class ManufacturersEditPage extends BasePage {
    @FindByDebugId("manufacturer-input")
    private WebElement manufacturerInput;
    @FindByDebugId("manufacturer-save")
    private WebElement saveButton;
    @FindByDebugId("manufacturer-close")
    private WebElement closeButton;
    @FindByDebugId("manufacturer-editor")
    private WebElement manufacturerEditor;

    public void setManufacturer(String manufacturer) {
        manufacturerInput.clear();
        manufacturerInput.sendKeys(manufacturer);
    }

    public String getManufacturer() {
        return manufacturerInput.getText();
    }

    public void clickOnSave() {
        saveButton.click();
    }

    public void clickOnClose() {
        closeButton.click();
    }

    public boolean isManufacturerEditorVisible() {
        return manufacturerEditor.isDisplayed();
    }
}
