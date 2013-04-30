package com.gwtplatform.carstore.cucumber.application.ratings;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;

import com.gwtplatform.carstore.cucumber.application.PageWithEditTable;
import com.gwtplatform.carstore.cucumber.util.FindByDebugId;

public class RatingPage extends PageWithEditTable {
    private static final String DELETE_BTN_CLASS_NAME = "delete";
    private static final String A_VALID_RATING = "10";

    @FindByDebugId("ratings")
    private WebElement ratings;

    @FindByDebugId("ratingCreate")
    private WebElement create;

    @FindByDebugId("ratingInput")
    private WebElement ratingInput;

    @FindByDebugId("ratingSave")
    @CacheLookup
    private WebElement save;

    public void clickOnCreate() {
        create.click();
    }

    public void fillForm() {
        ratingInput.sendKeys(A_VALID_RATING);
        save.click();
        waitUntilElementIsDetached(save);
    }

    public void deleteFirstRating() {
        deleteFirstRow(ratings);
    }

    public int getNumberOfLines() {
        return getNumberOfLines(ratings);
    }
}
