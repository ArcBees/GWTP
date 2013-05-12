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

package com.gwtplatform.carstore.client.application.widget.message;

public class Message {
    private final String message;
    private final MessageStyle style;
    private final MessageCloseDelay closeDelay;

    public Message(String message,
                   MessageStyle style) {
        this(message, style, MessageCloseDelay.DEFAULT);
    }

    public Message(String message,
                   MessageStyle style,
                   MessageCloseDelay closeDelay) {
        this.message = message;
        this.style = style;
        this.closeDelay = closeDelay;
    }

    public MessageStyle getStyle() {
        return style;
    }

    public String getMessage() {
        return message;
    }

    public MessageCloseDelay getCloseDelay() {
        return closeDelay;
    }
}
