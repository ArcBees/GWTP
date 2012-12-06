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
 * Base generator to use.
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
