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

package com.gwtplatform.carstore.cucumber.application.manufacturers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.gwtplatform.carstore.cucumber.application.PageWithEditTable;
import com.gwtplatform.carstore.cucumber.util.ByDebugId;

public class ManufacturersPage extends PageWithEditTable {
    public void clickOnCreate() {
        getCreateButton().click();
    }

    public void deleteFirstManufacturer() {
        deleteFirstRow(getManufacturersTable());
    }

    public int getNumberOfLines() {
        return getNumberOfLines(getManufacturersTable());
    }

    public void editFirstManufacturer() {
        getFirstRowEdit().click();
    }

    public String getFirstManufacturer() {
        return getCellText(getManufacturersTable(), "Name", 1);
    }

    public String getLastManufacturer() {
        return getCellText(getManufacturersTable(), "Name", getNumberOfLines());
    }

    private WebElement getFirstRowEdit() {
        return waitUntilElementIsLoaded(getManufacturersTable(), By.cssSelector("tbody td:nth-last-child(2) button"));
    }

    private WebElement getCreateButton() {
        return waitUntilElementIsClickable(ByDebugId.id("create-manufacturer"));
    }

    private WebElement getManufacturersTable() {
        return waitUntilElementIsVisible(ByDebugId.id("manufacturers"));
    }
}
