/**
 * Copyright 2014 ArcBees Inc.
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
package com.gwtplatform.mvp.client.view;

import com.google.gwt.user.client.Window;

/**
 * Positions the popup in the center of the screen
 */
public class CenterPopupPositioner extends PopupPositioner {
    @Override
    public PopupPosition getPopupPosition(int popupWidth, int popupHeight) {
        int left = (Window.getClientWidth() - popupWidth) / 2;
        int top = (Window.getClientHeight() - popupHeight) / 2;
        return new PopupPosition(left, top);
    }
}
