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
     * 
     * If there is enough space to the right, the left edge of the popup will be positioned flush with
     * the left edge of the widget.<p>
     * <pre>
     *     --------
     *     |widget|
     *     -------------
     *     |popup panel|
     *     -------------
     * </pre>
     *
     * Otherwise if there is enough space on the left the right edge of the popup will be positioned
     * flush with the right edge of the widget.<p>
     * <pre>
     *          --------
     *          |widget|
     *     -------------
     *     |popup panel|
     *     -------------
     * </pre>
     * 
     * If there is not enough space to the left or the right and clipToWindow is on. The popup will be
     * positioned on the left edge of the screen.<p>
     * <pre>
     *      |   --------
     *      |   |widget|
     *      |-------------
     *      ||popup panel|
     *      |-------------
     * </pre>
     * 
     * If you would prefer the popupPanel to always be flush with the widget call
     * {@link #RelativeToWidgetPopupPositioner(IsWidget, boolean)}
     * and set clipToWindow to false
     */
    public RelativeToWidgetPopupPositioner(IsWidget widget) {
        this(widget, true);
    }

    /**
     * @param widget - the widget relative to which the popup will be shown.
     * @param clipToWindow - set to false to always position the popup flush to an edge of the widget.
     * 
     * If there is enough space to the right, the left edge of the popup will be positioned flush with
     * the left edge of the widget.<p>
     * <pre>
     *     --------
     *     |widget|
     *     -------------
     *     |popup panel|
     *     -------------
     * </pre>
     *
     * Otherwise if there is enough space on the left the right edge of the popup will be positioned
     * flush with the right edge of the widget.<p>
     * <pre>
     *          --------
     *          |widget|
     *     -------------
     *     |popup panel|
     *     -------------
     * </pre>
     * 
     * If there is not enough space to the left or the right and clipToWindow is on. The popup will be
     * positioned on the left edge of the screen.<p>
     * <pre>
     *      |   --------
     *      |   |widget|
     *      |-------------
     *      ||popup panel|
     *      |-------------
     * </pre>
     * 
     * Set clipToWindow to false to always position the popup flush to an edge of the widget and expand
     * the screen when it will not fit.
     */
    public RelativeToWidgetPopupPositioner(IsWidget widget, boolean clipToWindow) {
        this.widget = widget.asWidget();
        this.clipToWindow = clipToWindow;
    }

    @Override
    protected int getTop(int popupHeight) {
        int top = widget.getAbsoluteTop();

        int windowTop = Window.getScrollTop();
        int windowBottom = Window.getScrollTop() + Window.getClientHeight();

        int distanceFromWindowTop = top - windowTop;

        int distanceToWindowBottom = windowBottom - (top + widget.getOffsetHeight());

        if (distanceToWindowBottom < popupHeight && distanceFromWindowTop >= popupHeight) {
            top -= popupHeight;
        } else {
            top += widget.getOffsetHeight();
        }
        return top;
    }

    @Override
    protected int getLeft(int popupWidth) {
        return LocaleInfo.getCurrentLocale().isRTL() ? getRtlLeft(popupWidth) : getLtrLeft(popupWidth);
    }

    protected int getLtrLeft(int popupWidth) {
        if (!canFitOnLeftEdge(popupWidth) && (canFitOnRightEdge(popupWidth) || clipToWindow)) {
            return Math.max(0, getRightEdge(popupWidth));
        } else {
            return widget.getAbsoluteLeft();
        }
    }

    protected int getRtlLeft(int popupWidth) {
        if (!canFitOnRightEdge(popupWidth) && (canFitOnLeftEdge(popupWidth) || !clipToWindow)) {
            return widget.getAbsoluteLeft();
        } else {
            return Math.max(0,  getRightEdge(popupWidth));
        }
    }

    private int getRightEdge(int popupWidth) {
        return widget.getAbsoluteLeft() + widget.getOffsetWidth() - popupWidth;
    }

    private boolean canFitOnLeftEdge(int popupWidth) {
        int windowRight = Window.getClientWidth() + Window.getScrollLeft();
        return windowRight - popupWidth > widget.getAbsoluteLeft();
    }

    private boolean canFitOnRightEdge(int popupWidth) {
        return getRightEdge(popupWidth) >= Window.getScrollLeft();
    }
}
