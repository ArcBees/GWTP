package com.philbeaudoin.gwtp.mvp.client.annotations;

import com.philbeaudoin.gwtp.mvp.client.PlaceFactory;

public @interface PlaceProvider {
  Class<? extends PlaceFactory> value();
}
