/**
 * Copyright 2011 ArcBees Inc.
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

package com.gwtplatform.mvp.client.proxy;

/**
 * @author Philippe Beaudoin
 */
public class PlaceImpl implements Place {

  private final String nameToken;

  public PlaceImpl(String nameToken) {
    this.nameToken = nameToken;
  }

  @Override
  public boolean canReveal() {
    return true;
  }

  @Override
  public final boolean equals(Object o) {
    if (o instanceof Place) {
      Place place = (Place) o;
      return getNameToken().equals(place.getNameToken());
    }
    return false;
  }

  @Override
  public String getNameToken() {
    return nameToken;
  }

  @Override
  public final int hashCode() {
    return 17 * getNameToken().hashCode();
  }

  @Override
  public final boolean matchesRequest(PlaceRequest request) {
    return request.matchesNameToken(getNameToken());
  }

  @Override
  public final String toString() {
    return getNameToken();
  }

}
