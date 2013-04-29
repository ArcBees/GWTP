package com.gwtplatform.carstore.cucumber.stepdefs;

import javax.inject.Inject;

import com.gwtplatform.carstore.cucumber.application.cars.CarsPage;
import com.gwtplatform.carstore.cucumber.application.cars.EditCarsPage;
import com.gwtplatform.carstore.cucumber.application.widgets.MessageWidgetPage;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CarsStepdefs {
    private final CarsPage carsPage;
    private final EditCarsPage editCarsPage;
    private final MessageWidgetPage messageWidgetPage;

    private int numberOfLines;
    private String carManufacturer;
    private String carModel;

    @Inject
    public CarsStepdefs(
            CarsPage carsPage,
            EditCarsPage editCarsPage,
            MessageWidgetPage messageWidgetPage) {
        this.carsPage = carsPage;
        this.editCarsPage = editCarsPage;
        this.messageWidgetPage = messageWidgetPage;
    }

    @Given("^I click on the create car button$")
    public void iClickOnTheCreateCarButton() {
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

    @When("^I click on the first car's edit button$")
    public void I_click_on_the_first_car_s_edit_button() {
        carManufacturer = carsPage.getFirstCarManufacturer();
        carModel = carsPage.getFirstCarModel();
        carsPage.editFirstCar();
    }

    @Then("^I should be on the edited car page$")
    public void I_should_be_on_the_edited_car_page() {
        String currentUrl = editCarsPage.getCurrentUrl();
        assertTrue(currentUrl.contains("#" + carManufacturer + carModel));
    }

    @And("^The shown car should match the edited car$")
    public void The_shown_car_should_match_the_edited_car() {
        assertEquals(editCarsPage.getCurrentCarTabName(), carManufacturer + " " + carModel);
    }

    @When("^I choose (.*?) as the car manufacturer$")
    public void I_type_as_the_car_manufacturer(String manufacturer) {
        editCarsPage.setCarManufacturer(manufacturer);
    }

    @And("^I type (.*?) as the car model$")
    public void I_type_as_the_car_model(String model) {
        editCarsPage.setCarModel(model);
    }

    @And("^I click to save the car$")
    public void I_click_to_save_the_car() {
        editCarsPage.clickOnSave();
    }

    @Then("^The car table should show (.*?) / (.*?) as the first car$")
    public void The_car_table_should_show_as_the_first_car(String manufacturer, String model) {
        assertEquals(manufacturer, carsPage.getFirstCarManufacturer());
        assertEquals(model, carsPage.getFirstCarModel());
    }

    @And("^I click to close the car$")
    public void I_click_to_close_the_car() throws Throwable {
        editCarsPage.clickOnClose();
    }
}
