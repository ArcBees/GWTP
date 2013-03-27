package com.arcbees.carsample.client.gin;

import com.arcbees.carsample.client.resources.AppResources;
import com.google.inject.Inject;

public class ResourceLoader {
    @Inject
    public ResourceLoader(AppResources resources) {
        resources.styles().ensureInjected();
    }
}