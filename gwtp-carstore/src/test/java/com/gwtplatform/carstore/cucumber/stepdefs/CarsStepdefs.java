package com.gwtplatform.carstore.cucumber.stepdefs;

import javax.inject.Inject;

import com.gwtplatform.carstore.cucumber.application.cars.CarsPage;
import com.gwtplatform.carstore.cucumber.application.widgets.MessageWidgetPage;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CarsStepdefs {
    private final CarsPage carsPage;
    private final MessageWidgetPage messageWidgetPage;
    private int numberOfLines;

    @Inject
    public CarsStepdefs(CarsPage carsPage, MessageWidgetPage messageWidgetPage) {
        this.carsPage = carsPage;
        this.messageWidgetPage = messageWidgetPage;
    }

    @Given("^I click on the create car button$")
    public void iClickOnTheCreateCarButton() throws Throwable {
        numberOfLines = carsPage.getNumberOfLines();
        carsPage.clickOnCreate();
    }

    @When("^I fill the car form$")
    public void iFillTheCarForm() {
        carsPage.fillForm();
    }

    @Then("^A car is created$")
    public void aCarIsCreated() {
        assertTrue(messageWidgetPage.hasSuccessMessage());
        assertEquals(numberOfLines + 1, carsPage.getNumberOfLines());
    }

    @When("^I delete the first car$")
    public void iDeleteTheFirstCar() {
        numberOfLines = carsPage.getNumberOfLines();
        carsPage.deleteFirstCar();
    }

    @Then("^The first car gets removed$")
    public void theFirstCarGetsRemoved() {
        assertEquals(numberOfLines - 1, carsPage.getNumberOfLines());
    }
}
