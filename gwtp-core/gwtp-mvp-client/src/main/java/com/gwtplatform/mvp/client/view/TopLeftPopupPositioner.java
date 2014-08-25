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

/**
 *  Positions the popup at the left and top coordinates.
 */
public class TopLeftPopupPositioner extends PopupPositioner {
    private final int left;
    private final int top;

    public TopLeftPopupPositioner(final int left, final int top) {
        super();
        this.left = left;
        this.top = top;
    }

    @Override
    public PopupPosition getPopupPosition(final int popupWidth, final int popupHeight) {
        return new PopupPosition(left, top);
    }
}
