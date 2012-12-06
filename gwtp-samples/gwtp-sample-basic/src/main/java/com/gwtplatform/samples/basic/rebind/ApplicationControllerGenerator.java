package com.gwtplatform.samples.basic.rebind;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.gwtplatform.mvp.client.DelayedBindRegistry;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.samples.basic.client.gin.GWTPGinModules;

import java.io.PrintWriter;

public class ApplicationControllerGenerator extends Generator {
  private static final String CTOR = "public %s() {";
  private static final String DELAYED_BIND = "%s.bind(%s.SINGLETON);";
  private static final String PLACEMANAGER_REVEALCURRENTPLACE = "%s.SINGLETON.get%s().revealCurrentPlace();";

  private final GinjectorGenerator ginjectorGenerator = new GinjectorGenerator();

  private String packageName = "";
  private String className = "";
  private String typeName = "";
  private String generatorName = "";
  private TypeOracle typeOracle;
  private TreeLogger treeLogger;

  @Override
  public String generate(TreeLogger treeLogger, GeneratorContext generatorContext, String typeName)
      throws UnableToCompleteException {
    typeOracle = generatorContext.getTypeOracle();
    this.treeLogger = treeLogger;
    this.typeName = typeName;

    PrintWriter printWriter;
    printWriter = tryCreate(generatorContext, typeName);

    if (printWriter == null) {
      return null;
    }

    generateGenerator(generatorContext);

    ClassSourceFileComposerFactory composer = initComposer();
    SourceWriter sourceWriter = initSourceWriter(composer, generatorContext, printWriter);

    writeConstructor(sourceWriter);

    closeDefinition(generatorContext, printWriter, sourceWriter);

    return packageName + "." + className;
  }

  private void writeConstructor(SourceWriter sourceWriter) {
    sourceWriter.println(String.format(CTOR, className));
    sourceWriter.indent();

    sourceWriter.println(String.format(DELAYED_BIND, DelayedBindRegistry.class.getSimpleName(), generatorName));
    sourceWriter.println();

    sourceWriter.println(String.format(PLACEMANAGER_REVEALCURRENTPLACE, generatorName,
        PlaceManager.class.getSimpleName()));
    sourceWriter.outdent();
    sourceWriter.println("}");
  }

  private PrintWriter tryCreate(GeneratorContext generatorContext, String typeName) throws UnableToCompleteException{
    JClassType classType = getType(typeName);
    packageName = classType.getPackage().getName();
    className = classType.getSimpleSourceName() + "Impl";

    return generatorContext.tryCreate(treeLogger, packageName, className);
  }

  private JClassType getType(String typeName) throws UnableToCompleteException {
    try {
      return typeOracle.getType(typeName);
    } catch (NotFoundException e) {
      treeLogger.log(TreeLogger.ERROR, "Cannot find " + typeName, e);
      throw new UnableToCompleteException();
    }
  }

  private void generateGenerator(GeneratorContext generatorContext) throws UnableToCompleteException {
    JClassType ginjector = findGinjector();

    generatorName = ginjectorGenerator.generate(treeLogger, generatorContext, ginjector.getQualifiedSourceName());
  }

  private JClassType findGinjector() throws UnableToCompleteException {
    for (JClassType type : typeOracle.getTypes()) {
      if (type.isAnnotationPresent(GWTPGinModules.class)) {
        return type;
      }
    }

    treeLogger.log(TreeLogger.ERROR, "Cannot find ginjector");
    throw new UnableToCompleteException();
  }

  private ClassSourceFileComposerFactory initComposer() {
    ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, className);
    composer.addImplementedInterface(typeName);

    composer.addImport(DelayedBindRegistry.class.getCanonicalName());

    return composer;
  }

  private SourceWriter initSourceWriter(ClassSourceFileComposerFactory composer, GeneratorContext generatorContext,
      PrintWriter printWriter) {
    return composer.createSourceWriter(generatorContext, printWriter);
  }

  private void closeDefinition(GeneratorContext generatorContext, PrintWriter printWriter,
      SourceWriter sourceWriter) {
    sourceWriter.outdent();
    sourceWriter.println("}");

    generatorContext.commit(treeLogger, printWriter);
  }
}
