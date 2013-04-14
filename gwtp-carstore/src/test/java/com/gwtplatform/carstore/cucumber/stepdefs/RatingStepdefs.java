package com.gwtplatform.carstore.cucumber.stepdefs;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import com.gwtplatform.carstore.cucumber.application.ratings.RatingPage;
import com.gwtplatform.carstore.cucumber.application.widgets.MessageWidgetPage;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class RatingStepdefs {
    private final RatingPage ratingPage;
    private final MessageWidgetPage messageWidgetPage;
    private int numberOfLines;

    @Inject
    public RatingStepdefs(RatingPage ratingPage, MessageWidgetPage messageWidgetPage) {
        this.ratingPage = ratingPage;
        this.messageWidgetPage = messageWidgetPage;
    }

    @When("^I fill the rating form$")
    public void iFillTheRatingForm() {
        numberOfLines = ratingPage.getNumberOfLines();
        ratingPage.clickOnCreate();
        ratingPage.fillForm();
    }

    @Then("^A rating is created$")
    public void aRatingIsCreated() {
        messageWidgetPage.hasSuccessMessage();
        assertEquals(numberOfLines + 1, ratingPage.getNumberOfLines());
    }

    @When("^I delete the first rating$")
    public void iDeleteTheFirstRating() {
        numberOfLines = ratingPage.getNumberOfLines();
        ratingPage.deleteFirstRating();
    }

    @Then("^It get removed$")
    public void ItGetRemoved() {
        assertEquals(numberOfLines - 1, ratingPage.getNumberOfLines());
    }
}
