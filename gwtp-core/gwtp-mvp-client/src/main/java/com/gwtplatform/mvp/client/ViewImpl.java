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

package com.gwtplatform.mvp.client;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * A simple implementation of {@link View} that simply disregard every call to {@link #setInSlot(Object, IsWidget)},
 * {@link #addToSlot(Object, IsWidget)}, and {@link #removeFromSlot(Object, IsWidget)}.
 * <p/>
 * Feel free not to inherit from this if you need another base class (such as {@link
 * com.google.gwt.user.client.ui.Composite Composite}), but you will have to define the above methods.
 * <p/>
 * * <b>Important</b> call {@link #initWidget(IsWidget)} in your {@link View}'s constructor.
 */
public abstract class ViewImpl implements View, Handler {
    private Widget widget;

    @Override
    public void addToSlot(Object slot, IsWidget content) {
    }

    @Override
    public void removeFromSlot(Object slot, IsWidget content) {
    }

    @Override
    public void setInSlot(Object slot, IsWidget content) {
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void onAttachOrDetach(AttachEvent event) {
        if (event.isAttached()) {
            onAttach();
        } else {
            onDetach();
        }
    }

    protected void initWidget(IsWidget widget) {
        if (widget == null) {
            throw new NullPointerException("Widget cannot be null.");
        }

        this.widget = widget.asWidget();

        asWidget().addAttachHandler(this);
    }

    /**
     * Method called after the view is attached to the DOM.
     * <p/>
     * You should override this method to perform any ui related initialization that needs to be done after
     * that the view is attached <b>and that the presenter doesn't have to be aware of</b> (attach event handlers
     * for instance)
     */
    protected void onAttach() {
    }

    /**
     * Method called after the view is detached to the DOM.
     * <p/>
     * You should override this method to release any resources created directly or indirectly during the
     * call to {@link #onAttach()}
     */
    protected void onDetach() {
    }
}
