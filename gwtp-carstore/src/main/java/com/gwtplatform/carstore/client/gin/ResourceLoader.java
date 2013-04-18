package com.gwtplatform.carstore.client.gin;

import com.google.inject.Inject;
import com.gwtplatform.carstore.client.resources.AppResources;

public class ResourceLoader {
    @Inject
    public ResourceLoader(AppResources resources) {
        resources.styles().ensureInjected();
    }
}
