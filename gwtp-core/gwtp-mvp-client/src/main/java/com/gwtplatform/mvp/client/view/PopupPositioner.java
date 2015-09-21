/*
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

public abstract class PopupPositioner {
    public static class PopupPosition {
        private final int top;
        private final int left;

        public PopupPosition(int left, int top) {
            this.left = left;
            this.top = top;
        }

        public int getLeft() {
            return left;
        }

        public int getTop() {
            return top;
        }
    }

    public PopupPosition getPopupPosition(int popupWidth, int popupHeight) {
        return new PopupPosition(getLeft(popupWidth), getTop(popupHeight));
    }

    protected abstract int getLeft(int popupWidth);

    protected abstract int getTop(int popupHeight);
}
