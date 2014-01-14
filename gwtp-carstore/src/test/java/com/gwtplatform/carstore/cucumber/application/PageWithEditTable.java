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

package com.gwtplatform.carstore.cucumber.application;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class PageWithEditTable extends BasePage {
    protected void deleteFirstRow(WebElement table) {
        WebElement delete = waitUntilElementIsClickable(table, By.cssSelector("tbody td:last-child button"));
        chooseOkOnNextConfirm();
        delete.click();
        waitUntilElementIsDetached(delete);
    }

    protected void editFirstRow(WebElement table) {
        WebElement edit = waitUntilElementIsClickable(table, By.cssSelector("tbody td:nth-last-child(2) button"));
        edit.click();
        waitUntilElementIsDetached(edit);
    }

    protected int getNumberOfLines(WebElement table) {
        return table.findElements(By.xpath("tbody[1]/tr")).size();
    }

    protected String getCellText(WebElement table, String columnName, int row) {
        int columnIndex = getColumnIndex(table, columnName);

        return table.findElement(By.xpath("tbody[1]/tr[" + row + "]/td[" + columnIndex + "]")).getText();
    }

    protected int getColumnIndex(WebElement table, String columnName) {
        List<WebElement> tableHeaders = table.findElements(By.cssSelector("thead th"));

        int index = 1;
        for (WebElement tableHeader : tableHeaders) {
            if (columnName.equals(tableHeader.getText())) {
                return index;
            }
            index++;
        }

        return 0;
    }
}
