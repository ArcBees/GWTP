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

package com.gwtplatform.mvp.client.proxy;

import com.google.gwt.event.shared.GwtEvent;
import com.gwtplatform.mvp.client.EventBus;

/**
 * 
 * This event is fired whenever the user wants to have access to the title
 * of a place.
 * <p />
 * <b>Important!</b> You should never fire that event directly. Instead,
 * use {@link PlaceManager#getCurrentTitle(SetPlaceTitleHandler)} or
 * {@link PlaceManager#getCurrentTitle(int, SetPlaceTitleHandler)}.
 * 
 * @author Philippe Beaudoin
 */
public class GetPlaceTitleEvent extends GwtEvent<GetPlaceTitleHandler> {

  private static Type<GetPlaceTitleHandler> TYPE;

  public static Type<GetPlaceTitleHandler> getType() {
    if ( TYPE == null )
      TYPE = new Type<GetPlaceTitleHandler>();
    return TYPE;
  }

  private final PlaceRequest request;
  private final SetPlaceTitleHandler handler;
  
  /**
   * The handled flag can let others know when the event has been handled.
   * Handlers should call {@link setHandled()} as soon as they figure they
   * are be responsible for this event. Handlers should not process
   * this event if {@link isHandled()} return {@code true}. 
   */
  private boolean handled = false;

  public GetPlaceTitleEvent( PlaceRequest request, SetPlaceTitleHandler handler ) {
    this.request = request;
    this.handler = handler;
  }

  @Override
  protected void dispatch( GetPlaceTitleHandler handler ) {
    handler.onGetPlaceTitle( this );
  }

  @Override
  public Type<GetPlaceTitleHandler> getAssociatedType() {
    return getType();
  }

  public PlaceRequest getRequest() {
    return request;
  }
  
  public SetPlaceTitleHandler getHandler() {
    return handler;
  }

  /**
   * Indicates that the event was handled and that other handlers
   * should not process it.
   */
  public void setHandled() {
    handled = true;
  }

  /**
   * Checks if the event was handled. If it was, then it should not
   * be processed further.
   * 
   * @return {@code true} if the event was handled. {@code false} otherwise.
   */
  public boolean isHandled() {
    return handled;
  }
  
  /**
   * Fires a {@link GetPlaceTitleEvent} into the {@link EventBus}.
   *
   * @param eventBus  The event bus.
   * @param request   The {@link PlaceRequest} for which to obtain the title.
   * @param handler   The {@link SetPlaceTitleHandler} that will be invoked when the title is obtained.
   */
  public static void fire( EventBus eventBus, PlaceRequest request, SetPlaceTitleHandler handler ) {
    eventBus.fireEvent( new GetPlaceTitleEvent( request, handler ) );
  }
}
