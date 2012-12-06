package com.gwtplatform.samples.basic.rebind;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.GinModule;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.common.client.ProviderBundle;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplitBundle;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.samples.basic.client.gin.GWTPGinModules;

import javax.inject.Provider;
import java.io.PrintWriter;
import java.util.List;

public class GinjectorGenerator extends Generator {
  private static final String SINGLETON_DECLARATION = "static %s SINGLETON = %s.create(%s.class);";
  private static final String GETTER_METHOD = "%s get%s();";
  private static final String GETTER_PROVIDER_METHOD = "%s<%s> get%s();";
  private static final String GIN_MODULES = "@%s({%s})";

  private String packageName = "";
  private String className = "";
  private String typeName = "";
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

    PresenterDefinitions presenterDefinitions = new PresenterDefinitions();
    findAllPresenters(presenterDefinitions);

    ClassSourceFileComposerFactory composer = initComposer();
    writeGinModulesAnnotation(composer);
    writeMandatoryGetterImports(composer);
    writePresenterImports(composer, presenterDefinitions);

    SourceWriter sourceWriter = initSourceWriter(composer, generatorContext, printWriter);
    writeMandatoryGetter(sourceWriter);
    writePresentersGetter(sourceWriter, presenterDefinitions);

    closeDefinition(generatorContext, printWriter, sourceWriter);

    return packageName + "." + className;
  }

  private PrintWriter tryCreate(GeneratorContext generatorContext, String typeName) throws UnableToCompleteException {
    JClassType classType = getType(typeName);
    packageName = classType.getPackage().getName();
    className = classType.getSimpleSourceName() + "Generated";

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

  private void findAllPresenters(PresenterDefinitions presenterDefinitions) {
    for (JClassType type : typeOracle.getTypes()) {
      if (type.isAnnotationPresent(ProxyStandard.class)) {
        presenterDefinitions.addStandardPresenter(type.getEnclosingType());
      } else if (type.isAnnotationPresent(ProxyCodeSplit.class)) {
        presenterDefinitions.addCodeSplitPresenter(type.getEnclosingType());
      } else if (type.isAnnotationPresent(ProxyCodeSplitBundle.class)) {
        presenterDefinitions.addCodeSplitBundlePresenter(type.getEnclosingType());
      }
    }
  }

  private ClassSourceFileComposerFactory initComposer() {
    ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, className);
    composer.addImplementedInterface(typeName);
    composer.makeInterface();

    return composer;
  }

  private void writeGinModulesAnnotation(ClassSourceFileComposerFactory composer) throws UnableToCompleteException {
    JClassType ginjector = getType(typeName);

    GWTPGinModules ginModules = ginjector.getAnnotation(GWTPGinModules.class);
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < ginModules.value().length; i++) {
      Class<? extends GinModule> module = ginModules.value()[i];

      stringBuilder.append(module.getSimpleName()).append(".class");
      composer.addImport(module.getName());

      if (ginModules.value().length > 1 && i < ginModules.value().length - 1) {
        stringBuilder.append(", ");
      }
    }

    composer.addAnnotationDeclaration(String.format(GIN_MODULES, GinModules.class.getSimpleName(),
        stringBuilder.toString()));
    composer.addImport(GinModules.class.getName());
  }

  private void writeMandatoryGetterImports(ClassSourceFileComposerFactory composer) {
    composer.addImport(GWT.class.getCanonicalName());
    composer.addImport(EventBus.class.getCanonicalName());
    composer.addImport(PlaceManager.class.getCanonicalName());
  }

  private void writePresenterImports(ClassSourceFileComposerFactory composer,
      PresenterDefinitions presenterDefinitions) {
    writePresenterImportsFromList(composer, presenterDefinitions.getStandardPresenters());
    writePresenterImportsFromList(composer, presenterDefinitions.getCodeSplitPresenters());
    writePresenterImportsFromList(composer, presenterDefinitions.getCodeSplitBundlePresenters());

    if (presenterDefinitions.getStandardPresenters().size() > 0) {
      composer.addImport(Provider.class.getCanonicalName());
    }

    if (presenterDefinitions.getCodeSplitPresenters().size() > 0) {
      composer.addImport(AsyncProvider.class.getCanonicalName());
    }

    if (presenterDefinitions.getCodeSplitBundlePresenters().size() > 0) {
      composer.addImport(ProviderBundle.class.getCanonicalName());
    }
  }

  private void writePresenterImportsFromList(ClassSourceFileComposerFactory composer, List<JClassType> presenters) {
    for (JClassType presenter : presenters) {
      composer.addImport(presenter.getQualifiedSourceName());
    }
  }

  private void writeMandatoryGetter(SourceWriter sourceWriter) {
    sourceWriter.println(String.format(SINGLETON_DECLARATION, className, GWT.class.getSimpleName(), className));
    sourceWriter.println();

    String eventBusName = EventBus.class.getSimpleName();
    sourceWriter.println(String.format(GETTER_METHOD, eventBusName, eventBusName));
    sourceWriter.println();

    String placeManagerName = PlaceManager.class.getSimpleName();
    sourceWriter.println(String.format(GETTER_METHOD, placeManagerName, placeManagerName));
  }

  private SourceWriter initSourceWriter(ClassSourceFileComposerFactory composer, GeneratorContext generatorContext,
      PrintWriter printWriter) {
    return composer.createSourceWriter(generatorContext, printWriter);
  }

  private void writePresentersGetter(SourceWriter sourceWriter, PresenterDefinitions presenterDefinitions) {
    writePresenterGettersFromList(sourceWriter, presenterDefinitions.getStandardPresenters(),
        Provider.class.getSimpleName());
    writePresenterGettersFromList(sourceWriter, presenterDefinitions.getCodeSplitPresenters(),
        AsyncProvider.class.getSimpleName());
    writePresenterGettersFromList(sourceWriter, presenterDefinitions.getCodeSplitBundlePresenters(),
        ProviderBundle.class.getSimpleName());

  }

  private void writePresenterGettersFromList(SourceWriter sourceWriter, List<JClassType> presenters,
      String providerTypeName) {
    for (JClassType presenter : presenters) {
      String presenterName = presenter.getName();

      sourceWriter.println();
      sourceWriter.println(String.format(GETTER_PROVIDER_METHOD, providerTypeName, presenterName, presenterName));
    }
  }

  private void closeDefinition(GeneratorContext generatorContext, PrintWriter printWriter,
      SourceWriter sourceWriter) {
    sourceWriter.outdent();
    sourceWriter.println("}");

    generatorContext.commit(treeLogger, printWriter);
  }
}
