package com.gwtplatform.mvp.client.proxy;

public class PlaceWithGatekeeper extends PlaceImpl {

  private final Gatekeeper gatekeeper;
  
  public PlaceWithGatekeeper(String nameToken, Gatekeeper gatekeeper) {
    super(nameToken);
    this.gatekeeper = gatekeeper;
  }
  
  @Override
  public boolean canReveal() {
    return gatekeeper.canReveal();
  }
}
