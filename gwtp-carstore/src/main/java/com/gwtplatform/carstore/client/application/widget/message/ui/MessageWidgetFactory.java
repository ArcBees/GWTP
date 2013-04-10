package com.gwtplatform.carstore.client.application.widget.message.ui;

import com.gwtplatform.carstore.client.application.widget.message.Message;

public interface MessageWidgetFactory {
    public MessageWidget createMessage(Message message);
}
