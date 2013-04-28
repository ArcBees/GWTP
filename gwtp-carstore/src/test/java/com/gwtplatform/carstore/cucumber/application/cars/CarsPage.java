package com.gwtplatform.carstore.cucumber.application.cars;

import org.openqa.selenium.WebElement;

import com.gwtplatform.carstore.cucumber.application.PageWithEditTable;
import com.gwtplatform.carstore.cucumber.util.FindByDebugId;

public class CarsPage extends PageWithEditTable {
    @FindByDebugId("cars")
    private WebElement cars;

    @FindByDebugId("carCreate")
    private WebElement create;

    public void clickOnCreate() {
        create.click();
    }

    public void fillForm() {
    }

    public void deleteFirstCar() {
        deleteFirstRow(cars);
    }

    public int getNumberOfLines() {
        return getNumberOfLines(cars);
    }
}
