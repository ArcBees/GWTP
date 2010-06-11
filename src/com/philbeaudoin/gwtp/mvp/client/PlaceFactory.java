package com.philbeaudoin.gwtp.mvp.client;

import com.philbeaudoin.gwtp.mvp.client.proxy.Place;

public interface PlaceFactory {
  public Place create(String nameToken);
}
