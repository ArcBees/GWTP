/*
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.carstore.cucumber.application.reports;

import java.util.HashMap;

import org.openqa.selenium.WebElement;

import com.gwtplatform.carstore.cucumber.application.PageWithEditTable;
import com.gwtplatform.carstore.cucumber.util.FindByDebugId;

public class ReportPage extends PageWithEditTable {
    private static final String MANUFACTURER_COL = "Manufacturer";
    private static final String RATING_COL = "Rating";

    @FindByDebugId("reports")
    private WebElement reports;
    private HashMap<String, AveragingCounter> averages = new HashMap<String, AveragingCounter>();

    public void addRating(String car, String rating) {
        String manufacturer = car.substring(0, car.indexOf('/'));

        if (averages.containsKey(manufacturer)) {
            averages.get(manufacturer).add(Double.valueOf(rating));
        } else {
            averages.put(manufacturer, new AveragingCounter(Double.valueOf(rating)));
        }
    }

    public boolean checkManufacturerAverages() {
        boolean match = true;

        for (int row = 1; row <= getNumberOfLines(reports); row++) {
            String manufacturer = getCellText(reports, MANUFACTURER_COL, row);
            Double average = Double.valueOf(getCellText(reports, RATING_COL, row));

            if (averages.get(manufacturer).average() != average) {
                match &= false;
            }
        }

        return match;
    }

    private class AveragingCounter {
        private double sum;
        private int count;

        AveragingCounter(double number) {
            this.sum = number;
            this.count = 1;
        }

        void add(double number) {
            this.sum += number;
            this.count++;
        }

        double average() {
            return count == 0 ? Double.NaN : sum / count;
        }
    }
}
