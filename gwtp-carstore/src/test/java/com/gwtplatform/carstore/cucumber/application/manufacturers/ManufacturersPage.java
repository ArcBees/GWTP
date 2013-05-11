package com.gwtplatform.carstore.cucumber.application.manufacturers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.gwtplatform.carstore.cucumber.application.PageWithEditTable;
import com.gwtplatform.carstore.cucumber.util.FindByDebugId;

public class ManufacturersPage extends PageWithEditTable {
    @FindByDebugId("manufacturers")
    private WebElement manufacturers;
    @FindByDebugId("create-manufacturer")
    private WebElement create;

    public void clickOnCreate() {
        create.click();
    }

    public void deleteFirstManufacturer() {
        deleteFirstRow(manufacturers);
    }

    public int getNumberOfLines() {
        return getNumberOfLines(manufacturers);
    }

    public void editFirstManufacturer() {
        getFirstRowEdit().click();
    }

    public String getFirstManufacturer() {
        return getCellText(manufacturers, "Name", 1);
    }

    public String getLastManufacturer() {
        return getCellText(manufacturers, "Name", getNumberOfLines());
    }

    private WebElement getFirstRowEdit() {
        return waitUntilElementIsLoaded(manufacturers, By.cssSelector("tbody td:nth-last-child(2) button"));
    }
}
