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
