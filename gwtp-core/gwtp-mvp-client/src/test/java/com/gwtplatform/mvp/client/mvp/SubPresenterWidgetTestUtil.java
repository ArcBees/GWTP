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

package com.gwtplatform.mvp.client.mvp;

import com.google.web.bindery.event.shared.EventBus;
import com.google.inject.Inject;

import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

/**
 * This is the test presenter.
 *
 * @author Philippe Beaudoin
 */
public class SubPresenterWidgetTestUtil extends PresenterWidget<SubPresenterWidgetTestUtil.MyView> {

  /**
   * Presenter's view.
   */
  public interface MyView extends View {
  }

  @Inject
  public SubPresenterWidgetTestUtil(final EventBus eventBus, final MyView view) {
    super(eventBus, view);
  }

}

