/**
 * Copyright 2011 ArcBees Inc.
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

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;

public class FieldWriterOfInjectedType extends FieldWriterOfExistingType {

  private final JClassType ginjectorClass;
  private final String ginjectorMethod;

  public FieldWriterOfInjectedType(JClassType type, String name, JClassType ginjectorClass,
      String ginjectorMethod, MortalLogger logger) {
    super(type, name, logger);
    this.ginjectorClass = ginjectorClass;
    this.ginjectorMethod = ginjectorMethod;
  }

  public void write(IndentedWriter w) throws UnableToCompleteException {
    // Preempt creation of initializer, provide our own based on gin.
    setInitializer( String.format("(%1$s) (((%2$s)com.gwtplatform.mvp.client.DelayedBindRegistry.getGinjector()).%3$s())",
        getQualifiedSourceName(), ginjectorClass.getQualifiedSourceName(), ginjectorMethod) );
    super.write(w);
  }

}
