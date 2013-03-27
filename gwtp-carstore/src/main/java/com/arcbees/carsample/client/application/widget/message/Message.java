package com.arcbees.carsample.client.application.widget.message;

public class Message {
    private final String message;
    private final MessageStyle style;
    private final MessageCloseDelay closeDelay;

    public Message(final String message, final MessageStyle style) {
        this(message, style, MessageCloseDelay.DEFAULT);
    }

    public Message(final String message, final MessageStyle style, final MessageCloseDelay closeDelay) {
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
