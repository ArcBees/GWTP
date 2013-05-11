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

package com.gwtplatform.carstore.rebind;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.gwtplatform.carstore.client.columninitializer.Column;
import com.gwtplatform.carstore.client.columninitializer.ColumnsDefinition;

public class ColumnsInitializerDefinitions {
    private ColumnsDefinition columnsInitializer;
    private List<ColumnTuple> columns;

    public List<ColumnTuple> getColumns() {
        return columns;
    }

    public String getDtoCanonicalName() {
        return columnsInitializer.definitionFor().getCanonicalName();
    }

    public static ColumnsInitializerDefinitions createFrom(JClassType type) {
        ColumnsInitializerDefinitions columnsInitializerDefinitions = new ColumnsInitializerDefinitions();

        columnsInitializerDefinitions.columnsInitializer = type.getAnnotation(ColumnsDefinition.class);

        List<ColumnTuple> columns = new ArrayList<ColumnTuple>();
        JMethod[] jMethods = type.getMethods();
        for (int i = 0; i < jMethods.length; i++) {
            JMethod method = jMethods[i];

            if (method.isAnnotationPresent(Column.class)) {
                ColumnTuple columnTuple = ColumnTuple.createFrom(method);

                columns.add(columnTuple);
            }
        }

        columnsInitializerDefinitions.columns = columns;

        return columnsInitializerDefinitions;
    }
}
