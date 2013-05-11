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

package com.gwtplatform.carstore.client.place;

public class NameTokens {
    public static final String login = "login";
    public static final String manufacturer = "manufacturer";
    public static final String detailManufacturer = "detailManufacturer";
    public static final String rating = "rating";
    public static final String detailRating = "detailRating";
    public static final String cars = "cars";
    public static final String report = "report";
    public static final String newCar = "newCar";

    public static String getOnLoginDefaultPage() {
        return manufacturer;
    }

    public static String getManufacturer() {
        return manufacturer;
    }

    public static String getDetailManufacturer() {
        return detailManufacturer;
    }

    public static String getRating() {
        return rating;
    }

    public static String getDetailRating() {
        return detailRating;
    }

    public static String getCars() {
        return cars;
    }

    public static String getReport() {
        return report;
    }
}
