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

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 *  Positions the popup relative to a widget.
 */
public class RelativeToWidgetPopupPositioner extends PopupPositioner {
    private final Widget widget;
    private final boolean clipToWindow;

    /**
     * @param widget - the widget relative to which the popup will be shown.
     * By default the popup will be clipped to left edge of the screen if there
     * is not enough space for it.
     * call {@link #RelativeToWidgetPopupPositioner(IsWidget, boolean)} if you
     * want the popup to always be shown relative to the widget and the
     * page to be expanded if there is no space.
     */
    public RelativeToWidgetPopupPositioner(IsWidget widget) {
        this(widget, true);
    }

    /**
     * @param widget - the widget relative to which the popup will be shown.
     * @param clipToWindow - set to true and the popup will be positioned on the left edge
     * of the screen if it cannot fit.
     */
    public RelativeToWidgetPopupPositioner(IsWidget widget, boolean clipToWindow) {
        this.widget = widget.asWidget();
        this.clipToWindow = clipToWindow;
    }

    /**
     * Positions the popup, called after the offset width and height of the popup
     * are known.
     *
     * @param offsetWidth the drop down's offset width
     * @param offsetHeight the drop down's offset height
     */
    @Override
    public PopupPosition getPopupPosition(int offsetWidth, int offsetHeight) {
        int offsetWidthDiff = offsetWidth - widget.getOffsetWidth();
        int left;

        if (LocaleInfo.getCurrentLocale().isRTL()) { // RTL case

            left = widget.getAbsoluteLeft() - offsetWidthDiff;

            if (offsetWidthDiff > 0) {
                int windowRight = Window.getClientWidth() + Window.getScrollLeft();
                int windowLeft = Window.getScrollLeft();

                int rightEdge = widget.getAbsoluteLeft() + widget.getOffsetWidth();

                int distanceToWindowRight = windowRight - rightEdge;
                int distanceFromWindowLeft = rightEdge - windowLeft;

                if (distanceFromWindowLeft < offsetWidth &&
                        (clipToWindow || distanceToWindowRight >= offsetWidthDiff)) {
                     left = widget.getAbsoluteLeft();
                     if (clipToWindow) {
                         left = Math.min(windowRight - offsetWidth, left);
                         left = Math.max(0, left);
                     }
                }
            }
        } else {
           left = widget.getAbsoluteLeft();

            if (offsetWidthDiff > 0) {
                int windowRight = Window.getClientWidth() + Window.getScrollLeft();
                int windowLeft = Window.getScrollLeft();

                int distanceToWindowRight = windowRight - left;
                int distanceFromWindowLeft = left - windowLeft;

                if (distanceToWindowRight < offsetWidth &&
                        (clipToWindow || distanceFromWindowLeft >= offsetWidthDiff)) {
                    left -= offsetWidthDiff;
                    if (clipToWindow) {
                        left = Math.max(0, left);
                    }
                }
            }
        }

        int top = widget.getAbsoluteTop();

        int windowTop = Window.getScrollTop();
        int windowBottom = Window.getScrollTop() + Window.getClientHeight();

        int distanceFromWindowTop = top - windowTop;

        int distanceToWindowBottom = windowBottom - (top + widget.getOffsetHeight());

        if (distanceToWindowBottom < offsetHeight && distanceFromWindowTop >= offsetHeight) {
            top -= offsetHeight;
        } else {
            top += widget.getOffsetHeight();
        }
        return new PopupPosition(left, top);
    }
}
