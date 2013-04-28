package com.gwtplatform.carstore.cucumber.application;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class PageWithEditTable extends BasePage {
    protected void deleteFirstRow(WebElement table) {
        WebElement delete = waitUntilElementIsLoaded(table, By.cssSelector("td:last-child button"));
        chooseOkOnNextConfirm();
        delete.click();
        waitUntilElementIsDetached(delete);
    }

    public int getNumberOfLines(WebElement table) {
        return table.findElements(By.cssSelector("tr")).size();
    }
}
