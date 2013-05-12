/**
 * Copyright 2013 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.gwtplatform.carstore.cucumber.stepdefs;

import javax.inject.Inject;

import com.gwtplatform.carstore.cucumber.application.manufacturers.ManufacturersEditPage;
import com.gwtplatform.carstore.cucumber.application.manufacturers.ManufacturersPage;
import com.gwtplatform.carstore.cucumber.application.widgets.MessageWidgetPage;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ManufacturersStepDefs extends StepDefs {
    private static final int animationTimeout = 1500;

    private final ManufacturersPage manufacturersPage;
    private final ManufacturersEditPage manufacturersEditPage;
    private final MessageWidgetPage messageWidgetPage;

    private int numberOfLines;

    @Inject
    ManufacturersStepDefs(
            ManufacturersPage manufacturersPage,
            ManufacturersEditPage manufacturersEditPage,
            MessageWidgetPage messageWidgetPage) {
        this.manufacturersPage = manufacturersPage;
        this.manufacturersEditPage = manufacturersEditPage;
        this.messageWidgetPage = messageWidgetPage;
    }

    @Given("^I click on the create manufacturer button$")
    public void I_click_on_create_manufacturer_button() {
        numberOfLines = manufacturersPage.getNumberOfLines();
        manufacturersPage.clickOnCreate();
        sleep(animationTimeout);
    }

    @When("^I click on the first manufacturer edit button$")
    public void I_click_on_first_manufacturer_edit_button() {
        manufacturersPage.editFirstManufacturer();
        sleep(animationTimeout);
    }

    @When("^I enter (\\S+) as the car manufacturer$")
    public void I_enter_as_manufacturer(String manufacturer) {
        manufacturersEditPage.setManufacturer(manufacturer);
    }

    @When("^I click on the save button$")
    public void I_click_on_the_save_button() {
        manufacturersEditPage.clickOnSave();
    }

    @When("^I click on the close button$")
    public void I_click_on_the_close_button() {
        manufacturersEditPage.clickOnClose();
    }

    @When("^I delete the first manufacturer$")
    public void I_delete_first_car() {
        numberOfLines = manufacturersPage.getNumberOfLines();
        manufacturersPage.deleteFirstManufacturer();
    }

    @Then("^I see the manufacturer editor$")
    public void I_see_manufacturer_editor() {
        assertTrue(manufacturersEditPage.isManufacturerEditorVisible());
    }

    @Then("^The shown manufacturer should match the edited manufacturer$")
    public void The_shown_manufacturer_should_match_the_edited_manufacturer() {
        assertEquals(manufacturersEditPage.getManufacturer(), manufacturersPage.getFirstManufacturer());
    }

    @Then("^The manufacturer table should show (\\S+) as the first manufacturer$")
    public void Manufacturer_table_should_show_as_first_manufacturer(String manufacturer) {
        assertEquals(manufacturersPage.getFirstManufacturer(), manufacturer);
    }

    @Then("^The manufacturer table should show (\\S+) as the last manufacturer$")
    public void Manufacturer_table_should_show_as_last_manufacturer(String manufacturer) {
        assertEquals(manufacturersPage.getLastManufacturer(), manufacturer);
    }

    @Then("^The first manufacturer gets removed$")
    public void First_car_gets_removed() {
        assertEquals(numberOfLines - 1, manufacturersPage.getNumberOfLines());
    }

    @Then("^The number of rows is unchanged$")
    public void Number_of_rows_is_unchanged() {
        assertEquals(manufacturersPage.getNumberOfLines(), numberOfLines);
    }

    @Then("^The number of rows is incremented$")
    public void Number_of_rows_is_incremented() {
        assertTrue(manufacturersPage.getNumberOfLines() > numberOfLines);
    }
}
