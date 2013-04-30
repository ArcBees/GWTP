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

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.PropertyOracle;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.inject.client.Ginjector;

/**
 * Generator for implementations of
 * {@link com.google.gwt.uibinder.client.UiBinder}. This
 * is a slightly modified version of {@link UiBinderGenerator}
 * that allows fields to be instantiated using gin dependency
 * injection. Modifications are clearly indicated by
 * {@code MODIFICATION} comments.
 * <p/>
 * To use this you need to use the following in your module file:
 * <pre> &lt;inherits name="com.google.gwt.uibinder.GinUiBinder" /&gt;</pre>
 * instead of {@code com.google.gwt.uibinder.UiBinder}.
 * Then you need to identify your ginjector class in your module
 * by defining a {@code gin.gingector} configuration property. For example:
 * <pre> &lt;define-configuration-property name="gin.ginjector" is-multi-valued="false" /&gt;
 * &lt;set-configuration-property name="gin.ginjector" value="com.mycompany.project.client.MyGinjector" /&gt;</pre>
 * Finally, you need to make sure every widget that participates in dependency
 * injection can be created via a method of your ginjector interface.
 */
public class GinUiBinderGenerator extends UiBinderGenerator {
    @Override
    protected FieldManager getFieldManager(TypeOracle oracle, MortalLogger logger,
            PropertyOracle propertyOracle, boolean useLazyWidgetBuilder)
            throws UnableToCompleteException {

        // Find ginjector
        FieldManager fieldManager;
        try {
            String ginjectorClassName = propertyOracle.getConfigurationProperty("gin.ginjector").getValues().get(0);

            JClassType ginjectorClass = oracle.findType(ginjectorClassName);
            if (ginjectorClass == null || !ginjectorClass.isAssignableTo(oracle.findType(Ginjector.class.getCanonicalName()))) {
                logger.die("The configuration property 'gin.ginjector' is '%s' " +
                        " which doesn't identify a type inheriting from 'Ginjector'.", ginjectorClassName);
            }
            fieldManager = new GinFieldManager(oracle, logger, ginjectorClass, useLazyWidgetBuilder);

        } catch (BadPropertyValueException e) {
            logger.warn(
                    "The configuration property 'gin.ginjector' was not found, it is required to use " +
                            "gin injection for UiBinder fields. If you don't need this consider using " +
                            "UiBinder.gwt.xml instead of GinUiBinder.gwt.xml in your module.");
            fieldManager = new FieldManager(oracle, logger, useLazyWidgetBuilder);
        }

        return fieldManager;
    }
}
