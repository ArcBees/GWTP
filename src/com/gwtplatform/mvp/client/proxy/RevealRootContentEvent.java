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
import com.gwtplatform.mvp.client.Presenter;

/**
 * Use this type of event to reveal content that should get added as the
 * {@link com.google.gwt.user.client.ui.RootPanel}. This type of content is
 * usually meant to use the browser like a regular webpage, adding a vertical
 * scrollbar as the content overflow.
 * 
 * @author Philippe Beaudoin
 */
public final class RevealRootContentEvent extends
    GwtEvent<RevealRootContentHandler> {

  private static final Type<RevealRootContentHandler> TYPE = new Type<RevealRootContentHandler>();

  public static void fire(final HasEventBus source, final Presenter<?> content) {
    source.fireEvent(new RevealRootContentEvent(content));
  }

  public static Type<RevealRootContentHandler> getType() {
    return TYPE;
  }

  private final Presenter<?> content;

  public RevealRootContentEvent(Presenter<?> content) {
    this.content = content;
  }

  @Override
  public Type<RevealRootContentHandler> getAssociatedType() {
    return getType();
  }

  public Presenter<?> getContent() {
    return content;
  }

  @Override
  protected void dispatch(RevealRootContentHandler handler) {
    handler.onRevealContent(this);
  }

}
