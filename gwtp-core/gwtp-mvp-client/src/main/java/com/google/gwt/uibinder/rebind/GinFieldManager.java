/*
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
package com.google.gwt.uibinder.rebind;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.TypeOracle;

/**
 * This version of {@link FieldManager} makes it possible for UiBinder
 * files to use widgets that need to be instantiated with gin.
 * See {@link GinUiBinderGenerator} for details. This
 * is a slightly modified version of {@link FieldManager}
 * that allows fields to be instantiated using gin dependency
 * injection. Modifications are clearly indicated by
 * {@code MODIFICATION} comments.
 */
public class GinFieldManager extends FieldManager {
    private Map<JClassType, String> ginjectorMethods = new HashMap<JClassType, String>();
    private JClassType ginjectorClass;

    public GinFieldManager(TypeOracle typeOracle, MortalLogger logger, JClassType ginjectorClass,
            boolean useLazyWidgetBuilders) {
        super(typeOracle, logger, useLazyWidgetBuilders);

        this.ginjectorClass = ginjectorClass;
        for (JMethod method : ginjectorClass.getInheritableMethods()) {
            JClassType returnType = method.getReturnType().isClassOrInterface();
            if (method.getParameters().length == 0 && returnType != null) {
                ginjectorMethods.put(returnType, method.getName());
            }
        }
    }

    public FieldWriter registerField(FieldWriterType fieldWriterType,
            JClassType fieldType, String fieldName) throws UnableToCompleteException {
        String ginjectorMethod = ginjectorMethods.get(fieldType);

        FieldWriter field;
        if (ginjectorMethod != null) {
            // If the ginjector lets us create that fieldType then we use gin to instantiate it
            field = new FieldWriterOfInjectedType(this, fieldWriterType, fieldType, fieldName, ginjectorClass,
                    ginjectorMethod, logger);
        } else {
            // Otherwise
            field = new FieldWriterOfExistingType(this, fieldWriterType, fieldType, fieldName, logger);
        }

        return registerField(fieldName, field);
    }
}
