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

import com.google.gwt.core.ext.ConfigurationProperty;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.common.client.ProviderBundle;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplitBundle;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.PlaceManager;

import javax.inject.Provider;
import java.io.PrintWriter;
import java.util.List;

/**
 * Will generate a Ginjector from Ginjector.
 */
public class GinjectorGenerator extends AbstractGenerator {
  private static final String SINGLETON_DECLARATION = "static %s SINGLETON = %s.create(%s.class);";
  private static final String GETTER_METHOD = "%s get%s();";
  private static final String GETTER_PROVIDER_METHOD = "%s<%s> get%s();";
  private static final String GIN_MODULES = "@%s({%s})";

  private String typeName;

  @Override
  public String generate(TreeLogger treeLogger, GeneratorContext generatorContext, String typeName)
      throws UnableToCompleteException {
    this.typeName = typeName;

    setTypeOracle(generatorContext.getTypeOracle());
    setTreeLogger(treeLogger);
    setPropertyOracle(generatorContext.getPropertyOracle());

    PrintWriter printWriter;
    printWriter = tryCreatePrintWriter(generatorContext);

    if (printWriter == null) {
      return typeName;
    }

    PresenterDefinitions presenterDefinitions = new PresenterDefinitions();
    findAllPresenters(presenterDefinitions);

    ClassSourceFileComposerFactory composer = initComposer();
    writeGinModulesAnnotation(composer);
    writeMandatoryGetterImports(composer);
    writePresenterImports(composer, presenterDefinitions);

    SourceWriter sourceWriter = composer.createSourceWriter(generatorContext, printWriter);
    writeMandatoryGetter(sourceWriter);
    writePresentersGetter(sourceWriter, presenterDefinitions);

    closeDefinition(generatorContext, printWriter, sourceWriter);

    return getPackageName() + "." + getClassName();
  }

  private PrintWriter tryCreatePrintWriter(GeneratorContext generatorContext) throws UnableToCompleteException {
    setClassName(getSimpleNameFromTypeName(typeName));
    setPackageName(getPackageNameFromTypeName(typeName));

    return generatorContext.tryCreate(getTreeLogger(), getPackageName(), getClassName());
  }

  private void findAllPresenters(PresenterDefinitions presenterDefinitions) {
    for (JClassType type : getTypeOracle().getTypes()) {
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
    ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(getPackageName(), getClassName());
    composer.addImport(Ginjector.class.getCanonicalName());
    composer.makeInterface();
    composer.addImplementedInterface(Ginjector.class.getSimpleName());

    return composer;
  }

  private void writeGinModulesAnnotation(ClassSourceFileComposerFactory composer)
      throws UnableToCompleteException {
    ConfigurationProperty moduleProperty = findConfigurationProperty(GIN_MODULE_NAME);
    String moduleName = moduleProperty.getValues().get(0);
    String moduleSimpleNameClass = getSimpleNameFromTypeName(moduleName) + ".class";

    composer.addImport(moduleName);
    composer.addImport(GinModules.class.getName());

    composer.addAnnotationDeclaration(String.format(GIN_MODULES, GinModules.class.getSimpleName(), moduleSimpleNameClass));
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
    sourceWriter.println(String.format(SINGLETON_DECLARATION, getClassName(), GWT.class.getSimpleName(),
        getClassName()));
    sourceWriter.println();

    String eventBusName = EventBus.class.getSimpleName();
    sourceWriter.println(String.format(GETTER_METHOD, eventBusName, eventBusName));
    sourceWriter.println();

    String placeManagerName = PlaceManager.class.getSimpleName();
    sourceWriter.println(String.format(GETTER_METHOD, placeManagerName, placeManagerName));
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
}

