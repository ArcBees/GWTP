/*
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.carstore.client.util.exceptiontranslators;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;


public class NotNullTranslator implements Translator {
    private static final String translationPattern = "The %s must be specified.";
    private static final RegExp regExp = RegExp.compile("Column '(\\w+)' cannot be null", "i");

    private final MatchResult matchResult;

    public NotNullTranslator(String message) {
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
