package com.gwtplatform.carstore.client.util.exceptiontranslators;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

public class ForeignTranslator implements Translator {
    private static final String translationPattern = "The row cannot be deleted. Some %s are referencing this row.";
    private static final RegExp regExp = RegExp.compile("a foreign key constraint fails \\(`\\w+`\\.`(\\w+)`.*\\)",
            "i");

    private final MatchResult matchResult;

    public ForeignTranslator(final String message) {
        matchResult = regExp.exec(message);
    }

    @Override
    public Boolean isMatching() {
        return matchResult != null;
    }

    @Override
    public String getTranslatedMessage() {
        String foreign = matchResult.getGroup(1);

        return translationPattern.replace("%s", foreign);
    }
}
