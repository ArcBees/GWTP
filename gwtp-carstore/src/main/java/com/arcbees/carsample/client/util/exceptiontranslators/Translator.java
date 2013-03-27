package com.arcbees.carsample.client.util.exceptiontranslators;

public interface Translator {
    Boolean isMatching();

    String getTranslatedMessage();
}
