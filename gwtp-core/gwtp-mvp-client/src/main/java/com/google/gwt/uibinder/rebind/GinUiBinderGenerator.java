/*
 * Copyright 2008 Google Inc.
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
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.PropertyOracle;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.dev.resource.Resource;
import com.google.gwt.dev.resource.ResourceOracle;
import com.google.gwt.dev.util.Util;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.uibinder.rebind.messages.MessagesWriter;
import com.google.gwt.uibinder.rebind.model.ImplicitClientBundle;

import org.w3c.dom.Document;
import org.xml.sax.SAXParseException;

import java.io.PrintWriter;

/**
 * Generator for implementations of
 * {@link com.google.gwt.uibinder.client.UiBinder}. This
 * is a slightly modified version of {@link UiBinderGenerator}
 * that allows fields to be instantiated using gin dependency
 * injection. Modifications are clearly indicated by
 * {@code MODIFICATION} comments.
 * <p />
 * To use this you need to use the following in your module file:
 * <pre> &lt;inherits name="com.google.gwt.uibinder.GinUiBinder" /&gt;</pre>
 * instead of {@code com.google.gwt.uibinder.UiBinde}.
 * Then you need to identify your ginjector class in your module
 * by defining a {@code gin.gingector} configuration property. For example:
 * <pre> &lt;define-configuration-property name="gin.ginjector" is-multi-valued="false" /&gt;
 * &lt;set-configuration-property name="gin.ginjector" value="com.mycompany.project.client.MyGinjector" /&gt;</pre>
 * Finally, you need to make sure every widget that participates in dependency
 * injection can be created via a method of your ginjector interface.
 *
 * @author Google
 * @author Philippe Beaudoin (philippe.beaudoin@gmail.com)
 */
public class GinUiBinderGenerator extends Generator {

  private static final String TEMPLATE_SUFFIX = ".ui.xml";

  static final String BINDER_URI = "urn:ui:com.google.gwt.uibinder";

  /**
   * Given a UiBinder interface, return the path to its ui.xml file, suitable
   * for any classloader to find it as a resource.
   */
  private static String deduceTemplateFile(MortalLogger logger,
      JClassType interfaceType) throws UnableToCompleteException {
    String templateName = null;
    UiTemplate annotation = interfaceType.getAnnotation(UiTemplate.class);
    if (annotation == null) {
      // if the interface is defined as a nested class, use the name of the
      // enclosing type
      if (interfaceType.getEnclosingType() != null) {
        interfaceType = interfaceType.getEnclosingType();
      }
      return slashify(interfaceType.getQualifiedSourceName()) + TEMPLATE_SUFFIX;
    } else {
      templateName = annotation.value();
      if (!templateName.endsWith(TEMPLATE_SUFFIX)) {
        logger.die("Template file name must end with " + TEMPLATE_SUFFIX);
      }

      /*
       * If the template file name (minus suffix) has no dots, make it relative
       * to the binder's package, otherwise slashify the dots
       */
      String unsuffixed = templateName.substring(0,
          templateName.lastIndexOf(TEMPLATE_SUFFIX));
      if (!unsuffixed.contains(".")) {
        templateName = slashify(interfaceType.getPackage().getName()) + "/"
            + templateName;
      } else {
        templateName = slashify(unsuffixed) + TEMPLATE_SUFFIX;
      }
    }
    return templateName;
  }

  private static String slashify(String s) {
    return s.replace(".", "/");
  }

  private final UiBinderContext uiBinderCtx = new UiBinderContext();

  @Override
  public String generate(TreeLogger logger, GeneratorContext genCtx,
      String fqInterfaceName) throws UnableToCompleteException {
    TypeOracle oracle = genCtx.getTypeOracle();
    ResourceOracle resourceOracle = genCtx.getResourcesOracle();

    JClassType interfaceType;
    try {
      interfaceType = oracle.getType(fqInterfaceName);
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    }

    DesignTimeUtils designTime;
    if (DesignTimeUtilsImpl.isDesignTime(fqInterfaceName)) {
      designTime = new DesignTimeUtilsImpl();
    } else {
      designTime = DesignTimeUtilsStub.EMPTY;
    }

    String implName = interfaceType.getName().replace('.', '_') + "Impl";
    implName = designTime.getImplName(implName);

    String packageName = interfaceType.getPackage().getName();
    PrintWriterManager writers = new PrintWriterManager(genCtx, logger,
        packageName);
    PrintWriter printWriter = writers.tryToMakePrintWriterFor(implName);

    if (printWriter != null) {
      // BEGIN MODIFICATION
      PropertyOracle propertyOracle = genCtx.getPropertyOracle();
      generateOnce(interfaceType, implName, printWriter, logger, oracle,
          resourceOracle, propertyOracle, writers, designTime);
      // END MODIFICATION
    }
    return packageName + "." + implName;
  }

  private void generateOnce(JClassType interfaceType, String implName,
      PrintWriter binderPrintWriter, TreeLogger treeLogger, TypeOracle oracle,
      ResourceOracle resourceOracle,
      PropertyOracle propertyOracle,  // MODIFICATION
      PrintWriterManager writerManager,
      DesignTimeUtils designTime) throws UnableToCompleteException {

    MortalLogger logger = new MortalLogger(treeLogger);
    String templatePath = deduceTemplateFile(logger, interfaceType);
    MessagesWriter messages = new MessagesWriter(BINDER_URI, logger,
        templatePath, interfaceType.getPackage().getName(), implName);

    FieldManager fieldManager = getFieldManager(logger, oracle,
        propertyOracle); // MODIFICATION

    UiBinderWriter uiBinderWriter = new UiBinderWriter(interfaceType, implName,
        templatePath, oracle, logger,
        fieldManager, // MODIFICATION
        messages, designTime, uiBinderCtx);

    Document doc = getW3cDoc(logger, designTime, resourceOracle, templatePath);
    designTime.rememberPathForElements(doc);

    uiBinderWriter.parseDocument(doc, binderPrintWriter);

    if (messages.hasMessages()) {
      messages.write(writerManager.makePrintWriterFor(messages.getMessagesClassName()));
    }

    ImplicitClientBundle bundleClass = uiBinderWriter.getBundleClass();
    new BundleWriter(bundleClass, writerManager, oracle, logger).write();

    writerManager.commit();
  }

  // BEGIN MODIFICATION
  private FieldManager getFieldManager(MortalLogger logger,
      TypeOracle oracle, PropertyOracle propertyOracle)
      throws UnableToCompleteException {

    // Find ginjector
    FieldManager fieldManager;
    try {
      String ginjectorClassName = propertyOracle.getConfigurationProperty(
          "gin.ginjector").getValues().get(0);


      JClassType ginjectorClass = oracle.findType(ginjectorClassName);
      if (ginjectorClass == null
          || !ginjectorClass.isAssignableTo(oracle.findType(Ginjector.class.getCanonicalName()))) {
        logger.die(
            "The configuration property 'gin.ginjector' is '%s' "
                + " which doesn't identify a type inheriting from 'Ginjector'.", ginjectorClassName );
      }
      fieldManager = new GinFieldManager(oracle, logger, ginjectorClass);

    } catch (BadPropertyValueException e) {
      logger.warn(
          "The configuration property 'gin.ginjector' was not found, it is required to use " +
          "gin injection for UiBinder fields. If you don't need this consider using " +
          "UiBinder.gwt.xml instead of GinUiBinder.gwt.xml in your module." );
      fieldManager = new FieldManager(oracle, logger);
    }

    return fieldManager;
  }
  // END MODIFICATION

  private Document getW3cDoc(MortalLogger logger, DesignTimeUtils designTime,
      ResourceOracle resourceOracle, String templatePath)
      throws UnableToCompleteException {

    Resource resource = resourceOracle.getResourceMap().get(templatePath);
    if (null == resource) {
      logger.die("Unable to find resource: " + templatePath);
    }

    Document doc = null;
    try {
      String content = designTime.getTemplateContent(templatePath);
      if (content == null) {
        content = Util.readStreamAsString(resource.openContents());
      }
      doc = new W3cDomHelper(logger.getTreeLogger(), resourceOracle).documentFor(
          content, resource.getPath());
    } catch (SAXParseException e) {
      logger.die(
          "Error parsing XML (line " + e.getLineNumber() + "): "
              + e.getMessage(), e);
    }
    return doc;
  }
}
