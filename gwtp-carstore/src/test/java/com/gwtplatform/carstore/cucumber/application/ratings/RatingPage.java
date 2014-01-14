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

package com.gwtplatform.carstore.cucumber.application.ratings;

import org.openqa.selenium.WebElement;

import com.gwtplatform.carstore.cucumber.application.PageWithEditTable;
import com.gwtplatform.carstore.cucumber.util.ByDebugId;

public class RatingPage extends PageWithEditTable {
    private static final String A_VALID_RATING = "10";

    public void clickOnCreate() {
        getCreateButton().click();
    }

    public void fillForm() {
        getRatingsInput().sendKeys(A_VALID_RATING);
        WebElement save = waitUntilElementIsClickable(ByDebugId.id("ratingSave"));
        save.click();
        waitUntilElementIsDetached(save);
    }

    public void deleteFirstRating() {
        deleteFirstRow(getRatingsTable());
    }

    public int getNumberOfLines() {
        return getNumberOfLines(getRatingsTable());
    }

    public String getCellText(String colName, int row) {
        return getCellText(getRatingsTable(), colName, row);
    }

    private WebElement getCreateButton() {
        return waitUntilElementIsClickable(ByDebugId.id("ratingCreate"));
    }

    private WebElement getRatingsTable() {
        return waitUntilElementIsVisible(ByDebugId.id("ratings"));
    }

    private WebElement getRatingsInput() {
        return waitUntilElementIsVisible(ByDebugId.id("ratingInput"));
    }
}
