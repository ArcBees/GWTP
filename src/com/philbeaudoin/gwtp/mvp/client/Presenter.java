/**
 * Copyright 2010 Philippe Beaudoin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.philbeaudoin.gwtp.mvp.client;

import com.google.inject.Singleton;
import com.philbeaudoin.gwtp.mvp.client.proxy.Place;
import com.philbeaudoin.gwtp.mvp.client.proxy.Proxy;
import com.philbeaudoin.gwtp.mvp.client.proxy.RevealContentEvent;

/**
 * A singleton presenter, one of the basic building block of
 * the <a href="http://code.google.com/intl/nl/events/io/2009/sessions/GoogleWebToolkitBestPractices.html">
 * model-view-presenter</a> architecture. Each page in your
 * application should correspond to a singleton {@link Presenter}
 * and it should have an accompanying singleton {@link View} and
 * {@link Proxy}.
 * 
 * @author Philippe Beaudoin
 */
@Singleton
public interface Presenter extends PresenterWidget {

  /**
   * Requests the presenter to reveal itself on screen. This call will
   * fail on presenters for which the {@link Proxy} is not a {@link Place},
   * since such presenters are not expected to be revealable.
   * Nothing happens if the presenter is currently visible (see 
   * {@link #isVisible()}). Upon being revealed, the presenter will ask to 
   * be inserted within its parent presenter by firing a {@link 
   * RevealContentEvent}. This will cause the parent to be revealed 
   * too, if necessary.
   */
  public void reveal();
  
  /**
   * Returns the {@link Proxy} for the current presenter.
   *
   * @return The proxy.
   */
  public Proxy<?> getProxy();

}
