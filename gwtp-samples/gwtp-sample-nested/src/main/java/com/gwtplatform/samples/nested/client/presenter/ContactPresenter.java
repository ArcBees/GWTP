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

package com.gwtplatform.samples.nested.client.presenter;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.samples.nested.client.NameTokens;

/**
 * The events are handled in {@link ContactPresenterBase}.
 *
 * @author Christian Goudreau
 * @author Philippe Beaudoin
 */
public class ContactPresenter
    extends ContactPresenterBase<ContactPresenter.MyProxy> {
  /**
   * {@link ContactPresenter}'s proxy.
   */
  @ProxyCodeSplit
  @NameToken(NameTokens.contactPage)
  public interface MyProxy extends ProxyPlace<ContactPresenter> {
  }

  @Inject
  public ContactPresenter(final EventBus eventBus, final MyView view,
      final MyProxy proxy) {
    super(eventBus, view, proxy);
  }

  @Override
  protected void revealInParent() {
    RevealContentEvent.fire(this, MainPagePresenter.TYPE_SetMainContent, this);
  }
}