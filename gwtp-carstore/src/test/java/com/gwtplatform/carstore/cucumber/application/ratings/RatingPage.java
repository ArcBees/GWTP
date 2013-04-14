package com.gwtplatform.carstore.cucumber.application.ratings;

import javax.inject.Inject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;

import com.gwtplatform.carstore.cucumber.application.BasePage;
import com.gwtplatform.carstore.cucumber.util.FindByDebugId;

public class RatingPage extends BasePage {
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

    @Inject
    public RatingPage(WebDriver webDriver) {
        super(webDriver);
    }

    public void clickOnCreate() {
        create.click();
    }

    public void fillForm() {
        ratingInput.sendKeys(A_VALID_RATING);
        save.click();
        waitUntilElementIsDetached(save);
    }

    public void deleteFirstRating() {
        WebElement delete = waitUntilElementIsLoaded(ratings, By.className(DELETE_BTN_CLASS_NAME))
                .findElement(By.tagName("button"));
        chooseOkOnNextConfirm();
        delete.click();
        waitUntilElementIsDetached(delete);
    }

    public int getNumberOfLines() {
        return ratings.findElements(By.className(DELETE_BTN_CLASS_NAME)).size();
    }
}
