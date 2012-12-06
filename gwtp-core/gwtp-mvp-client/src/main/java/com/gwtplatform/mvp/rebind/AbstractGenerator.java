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

package com.gwtplatform.mvp.rebind;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.SourceWriter;

import java.io.PrintWriter;

/**
 * Base generator
 */
public abstract class AbstractGenerator extends Generator {
  private TreeLogger treeLogger;
  private TypeOracle typeOracle;
  private JClassType typeClass;

  private String packageName = "";
  private String className = "";

  public void setTreeLogger(TreeLogger treeLogger) {
    this.treeLogger = treeLogger;
  }

  public TreeLogger getTreeLogger() {
    return treeLogger;
  }

  public TypeOracle getTypeOracle() {
    return typeOracle;
  }

  public void setTypeOracle(TypeOracle typeOracle) {
    this.typeOracle = typeOracle;
  }

  public JClassType getTypeClass() {
    return typeClass;
  }

  public void setTypeClass(JClassType typeName) {
    this.typeClass = typeName;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  protected PrintWriter tryCreatePrintWriter(GeneratorContext generatorContext,
      String suffix) throws UnableToCompleteException {
    setPackageName(getTypeClass().getPackage().getName());
    setClassName(getTypeClass().getName() + suffix);

    return generatorContext.tryCreate(getTreeLogger(), getPackageName(), getClassName());
  }

  protected JClassType getType(String typeName) throws UnableToCompleteException {
    try {
      return typeOracle.getType(typeName);
    } catch (NotFoundException e) {
      treeLogger.log(TreeLogger.ERROR, "Cannot find " + typeName, e);
      throw new UnableToCompleteException();
    }
  }

  protected void closeDefinition(GeneratorContext generatorContext, PrintWriter printWriter,
      SourceWriter sourceWriter) {
    sourceWriter.outdent();
    sourceWriter.println("}");

    generatorContext.commit(treeLogger, printWriter);
  }
}
