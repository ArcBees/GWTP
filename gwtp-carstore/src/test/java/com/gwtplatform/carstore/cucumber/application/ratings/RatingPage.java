package com.gwtplatform.carstore.cucumber.application.ratings;

import com.gwtplatform.carstore.cucumber.application.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import javax.inject.Inject;

import static com.google.gwt.user.client.ui.UIObject.DEBUG_ID_PREFIX;

public class RatingPage extends BasePage {
    private static final String DELETE_BTN_CLASS_NAME = "delete";
    private static final String A_VALID_RATING = "10";
    private static final String RATINGS_ID = DEBUG_ID_PREFIX + "ratings";
    private static final String SAVE_BTN_ID = DEBUG_ID_PREFIX + "ratingSave";
    private static final String CREATE_ID = DEBUG_ID_PREFIX + "ratingCreate";

    @FindBy(id = RATINGS_ID)
    private WebElement ratings;

    @FindBy(id = CREATE_ID)
    private WebElement create;

    @FindBy(id = DEBUG_ID_PREFIX + "ratingInput")
    private WebElement ratingInput;

    @FindBy(id = SAVE_BTN_ID)
    private WebElement save;

    @Inject
    public RatingPage(WebDriver webDriver) {
        super(webDriver);
    }

    public void clickOnCreate() {
        webDriverWait().until(ExpectedConditions.presenceOfElementLocated(By.id(RATINGS_ID)));
        create.click();
    }

    public void fillForm() {
        webDriverWait().until(ExpectedConditions.presenceOfElementLocated(By.id(SAVE_BTN_ID)));
        ratingInput.sendKeys(A_VALID_RATING);
        save.click();
    }

    public void deleteFirstRating() {
        webDriverWait().until(ExpectedConditions.presenceOfElementLocated(By.id(RATINGS_ID)));
        WebElement delete = ratings.findElement(By.className(DELETE_BTN_CLASS_NAME)).findElement(By.tagName("button"));
        delete.click();
        webDriver.switchTo().alert().accept();
        webDriverWait().until(ExpectedConditions.stalenessOf(delete));
    }

    public int getNumberOfLines() {
        webDriverWait().until(ExpectedConditions.presenceOfElementLocated(By.id(RATINGS_ID)));
        return ratings.findElements(By.className("delete")).size();
    }
}
