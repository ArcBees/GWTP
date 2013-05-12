/**
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.carstore.client.application.widget.message.ui;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.query.client.Function;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.assistedinject.Assisted;
import com.gwtplatform.carstore.client.application.widget.message.Message;
import com.gwtplatform.carstore.client.application.widget.message.MessageCloseDelay;
import com.gwtplatform.carstore.client.resources.AppResources;

import static com.google.gwt.query.client.GQuery.$;

public class MessageWidget extends Composite {
    interface Binder extends UiBinder<Widget, MessageWidget> {
    }

    @UiField
    HTMLPanel messageBox;
    @UiField
    InlineHTML close;
    @UiField(provided = true)
    AppResources appResources;
    @UiField
    InlineLabel messageLabel;

    private final Message message;

    private Timer closeTimer = new Timer() {
        @Override
        public void run() {
            close();
        }
    };

    @Inject
    public MessageWidget(Binder binder,
                         AppResources appResources,
                         @Assisted Message message) {
        this.appResources = appResources;
        this.message = message;

        initWidget(binder.createAndBindUi(this));
        initContent();
        initTimeout();
    }

    @Override
    protected void onLoad() {
        $(messageBox).fadeIn();
    }

    @SuppressWarnings("unused")
    @UiHandler("close")
    void onCloseAnchorClicked(ClickEvent event) {
        close();
    }

    private void close() {
        $(messageBox).fadeOut(new Function() {
            @Override
            public void f() {
                MessageWidget.this.removeFromParent();
            }
        });
    }

    private void initContent() {
        messageLabel.setText(message.getMessage());

        switch (message.getStyle()) {
            case SUCCESS:
                messageBox.addStyleName(appResources.styles().success());
                HTMLPanel.ensureDebugId(messageBox.getElement(), "successMessage");
                break;
            case ERROR:
                messageBox.addStyleName(appResources.styles().error());
                HTMLPanel.ensureDebugId(messageBox.getElement(), "errorMessage");
                break;
        }
    }

    private void initTimeout() {
        if (!message.getCloseDelay().equals(MessageCloseDelay.NEVER)) {
            closeTimer.schedule(message.getCloseDelay().getDelay());
        }
    }
}
