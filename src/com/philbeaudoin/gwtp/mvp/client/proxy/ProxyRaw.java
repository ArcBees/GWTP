/**
 * Copyright 2010 Gwt-Platform
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

package com.philbeaudoin.gwtp.mvp.client.proxy;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.philbeaudoin.gwtp.mvp.client.Presenter;

/**
 * This is the unparameterized base interface for proxy. It is 
 * provided as a work around since GIN/Guice cannot inject 
 * parameterized types. For most purposes you should use
 * {@link Proxy}.
 * 
 * @author Philippe Beaudoin
 */
public interface ProxyRaw {

  /**
   * Get the associated {@link Presenter}. The presenter can only be obtained in an asynchronous
   * manner to support code splitting when needed. To access the presenter, pass a callback.
   * <p />
   * The difference between this method and {@link Proxy#getPresenter(Callback)} is that the 
   * latter one gets the specific parameterised {@link Presenter} type.
   * 
   * @param callback The callback in which the {@link Presenter} will be passed as a parameter.
   */
  public void getRawPresenter( AsyncCallback<Presenter> callback );
  
  /**
   * Called by this proxy's presenter whenever it has changed in a way that would require 
   * the parameters in the HistoryToken bar to be changed. If you override, make sure you call
   * your parent onPresenterChanged().
   * 
   * @param presenter The {@link Presenter} that has just changed.
   */
  public abstract void onPresenterChanged(Presenter presenter);

  /**
   * Called by this proxy's presenter whenever it has been revealed. If you override, make sure you call
   * your parent onPresenterRevealed().
   * 
   * @param presenter The {@link Presenter} that has just been revealed.
   */
  public abstract void onPresenterRevealed(Presenter presenter);

}