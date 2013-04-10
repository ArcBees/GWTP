package com.gwtplatform.carstore.client.application.widget.header;

import com.gwtplatform.carstore.client.place.NameTokens;

public enum MenuItem {
    MANUFACTURER("Manufacturers", NameTokens.manufacturer),
    CAR("Cars", NameTokens.cars),
    RATING("Ratings", NameTokens.rating),
    REPORT("Reports", NameTokens.report);

    private String label;
    private String placeToken;

    private MenuItem(String label, String placeToken) {
        this.label = label;
        this.placeToken = placeToken;
    }

    public String getPlaceToken() {
        return placeToken;
    }

    public String toString() {
        return label;
    }
}

