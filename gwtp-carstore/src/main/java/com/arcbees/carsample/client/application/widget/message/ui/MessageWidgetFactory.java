package com.arcbees.carsample.client.application.widget.message.ui;

import com.arcbees.carsample.client.application.widget.message.Message;

public interface MessageWidgetFactory {
    public MessageWidget createMessage(Message message);
}
