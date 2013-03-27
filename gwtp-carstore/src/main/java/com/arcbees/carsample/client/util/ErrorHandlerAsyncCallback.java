package com.arcbees.carsample.client.util;

import com.arcbees.carsample.client.application.event.DisplayMessageEvent;
import com.arcbees.carsample.client.application.widget.message.Message;
import com.arcbees.carsample.client.application.widget.message.MessageStyle;
import com.arcbees.carsample.client.util.exceptiontranslators.ForeignTranslator;
import com.arcbees.carsample.client.util.exceptiontranslators.NotNullTranslator;
import com.arcbees.carsample.client.util.exceptiontranslators.Translator;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.shared.Result;

public abstract class ErrorHandlerAsyncCallback<R extends Result> implements AsyncCallback<R> {
    private final HasHandlers hasHandlers;

    public ErrorHandlerAsyncCallback(final HasHandlers hasHandlers) {
        this.hasHandlers = hasHandlers;
    }

    @Override
    public void onFailure(Throwable caught) {
        Message message = new Message(translateCauses(caught), MessageStyle.ERROR);
        DisplayMessageEvent.fire(hasHandlers, message);
    }

    private String translateCauses(Throwable caught) {
        StringBuilder sb = new StringBuilder(translateCause(caught));

        for (Throwable t = caught.getCause(); t != null; t = t.getCause()) {
            sb = sb.append(translateCause(t)).append("<br />");
        }

        return sb.toString();
    }

    private String translateCause(Throwable caught) {
        String message = caught.getMessage();

        Translator translator = new NotNullTranslator(message);
        if (translator.isMatching()) {
            return translator.getTranslatedMessage();
        }

        translator = new ForeignTranslator(message);
        if (translator.isMatching()) {
            return translator.getTranslatedMessage();
        }

        return message;
    }
}
