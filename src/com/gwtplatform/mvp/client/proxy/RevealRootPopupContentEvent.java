/**
 * Copyright 2010 ArcBees Inc.
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

import com.google.gwt.event.shared.GwtEvent;
import com.gwtplatform.mvp.client.HasEventBus;
import com.gwtplatform.mvp.client.PopupPresenter;

/**
 * Use this type of event to reveal popup content that should get added at the
 * root of the presenter hierarchy. 
 * 
 * @author Philippe Beaudoin
 */
public final class RevealRootPopupContentEvent extends
    GwtEvent<RevealRootPopupContentHandler> {

  private static final Type<RevealRootPopupContentHandler> TYPE = new Type<RevealRootPopupContentHandler>();

  public static void fire(final HasEventBus source, final PopupPresenter<?> content) {
    source.fireEvent(new RevealRootPopupContentEvent(content));
  }

  public static Type<RevealRootPopupContentHandler> getType() {
    return TYPE;
  }

  private final PopupPresenter<?> content;

  public RevealRootPopupContentEvent(PopupPresenter<?> content) {
    this.content = content;
  }

  @Override
  public Type<RevealRootPopupContentHandler> getAssociatedType() {
    return getType();
  }

  public PopupPresenter<?> getContent() {
    return content;
  }

  @Override
  protected void dispatch(RevealRootPopupContentHandler handler) {
    handler.onRevealContent(this);
  }

}
