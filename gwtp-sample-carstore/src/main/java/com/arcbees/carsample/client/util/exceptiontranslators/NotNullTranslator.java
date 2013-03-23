package com.arcbees.carsample.client.util.exceptiontranslators;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;


public class NotNullTranslator implements Translator {
    private static final String translationPattern = "The %s must be specified.";
    private static final RegExp regExp = RegExp.compile("Column '(\\w+)' cannot be null", "i");

    private final MatchResult matchResult;

    public NotNullTranslator(final String message) {
        matchResult = regExp.exec(message);
    }

    @Override
    public Boolean isMatching() {
        return matchResult != null;
    }

    @Override
    public String getTranslatedMessage() {
        String field = matchResult.getGroup(1);

        return translationPattern.replace("%s", field);
    }
}
