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
