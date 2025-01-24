/*
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

package com.gwtplatform.mvp.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * This annotation can be used to specify a function returning the title of a
 * place as a string, given the request. It can work in one of two ways. For
 * simple hard-coded titles see {@link Title}.
 * <p>
 * 1) You can use it to annotate a static or non-static public method in your
 * presenter that returns a string (the title). This method can optionally
 * accept a {@link com.gwtplatform.mvp.shared.proxy.PlaceRequest} parameter to
 * contain the place request for which the title is desired. It can also
 * optionally accept another parameter corresponding to your custom ginjector.
 * Using a static method is more efficient, as it doesn't force instantiation of
 * the associated presenter and view. Example of use:
 * <p>
 * <pre>
 * {@code @}TitleFunction
 *  static public String getTranslatedTitle( MyGinjector ginjector ) {
 *    return ginjector.getTranslations().productTitle();
 *  }
 *
 * {@code @}TitleFunction
 *  public String getTitle( PlaceRequest placeRequest ) {
 *    prepareFromRequest( placeRequest );
 *    return "Email #" + getEmailId();
 *  }
 * </pre>
 * <p>
 * 2) You can use it to annotate a static or non-static public method in your
 * presenter that returns {@code void} but accept a parameter of type
 * {@link com.gwtplatform.mvp.client.proxy.SetPlaceTitleHandler}. In this case,
 * your method is responsible for calling this handler's {@code onSetPlaceTitle}
 * with the title for this place, or {@code null} if the place doesn't have a
 * title. This is useful if the title can only be accessed in an asynchronous
 * fashion, for example following a call to the server. As above, your method
 * can accept a {@link com.gwtplatform.mvp.shared.proxy.PlaceRequest} parameter
 * and your custom ginjector. Example of use:
 * <p>
 * <pre>
 * {@code @}TitleFunction
 *  public void getTitle( PlaceRequest placeRequest, final SetPlaceTitleHandler handler ) {
 *    prepareFromRequest( placeRequest );
 *    dispatcher.execute( new GetUserNameAction( getUserId(), new AsyncCallback&lt;GetUserNameResult&gt;(){
 *      public void onSuccess(GetUserNameResult result) {
 *        handler.onSetPlaceTitle( result.getUserName() );
 *      }
 *      public void onFailure(Throwable caught) {
 *        handler.onSetPlaceTitle(null);
 *      }
 *    }
 *  }
 * </pre>
 */
@Target(ElementType.METHOD)
public @interface TitleFunction {
}
