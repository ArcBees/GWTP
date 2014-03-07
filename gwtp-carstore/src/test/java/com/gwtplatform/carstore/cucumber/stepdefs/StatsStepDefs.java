/**
 * Copyright 2014 ArcBees Inc.
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

import com.gwtplatform.carstore.cucumber.application.stats.StatsPage;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.junit.Assert.assertEquals;

public class StatsStepDefs {
    private final StatsPage statsPage;

    @Inject
    StatsStepDefs(StatsPage statsPage) {
        this.statsPage = statsPage;
    }

    @When("I select (\\d{4})-(\\d{2})-(\\d{2}) in the date picker")
    public void ISelectADateInDatePicker(int year, int month, int day) {
        statsPage.selectDate(year, month, day);
    }

    @When("^I click on Extract Year$")
    public void IClickOnExtractYear() {
        statsPage.extractYear();
    }

    @Then("^I should see (\\d{4}) as the extracted year$")
    public void IShouldSeeAsTheExtractedYear(int year) throws Throwable {
        int extractedYear = statsPage.getExtractedYear();

        assertEquals(year, extractedYear);
    }
}
