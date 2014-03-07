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

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.gwtplatform.mvp.client.annotations.DefaultGatekeeper;
import com.gwtplatform.mvp.client.annotations.GatekeeperParams;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.NoGatekeeper;
import com.gwtplatform.mvp.client.annotations.Title;
import com.gwtplatform.mvp.client.annotations.TitleFunction;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.GetPlaceTitleEvent;

/**
 * Proxy outputter for a proxy that is also a place.
 */
public class ProxyPlaceOutputter extends ProxyOutputterBase {
    public static final String WRAPPED_CLASS_NAME = "WrappedProxy";

    private String[] nameTokens;
    private String getGatekeeperMethod;
    private String[] gatekeeperParams;

    private String title;
    private PresenterTitleMethod presenterTitleMethod;

    public ProxyPlaceOutputter(
            TypeOracle oracle,
            TreeLogger logger,
            ClassCollection classCollection,
            GinjectorInspector ginjectorInspector,
            PresenterInspector presenterInspector) {
        super(oracle, logger, classCollection, ginjectorInspector, presenterInspector);
    }

    @Override
    public void writeInnerClasses(SourceWriter writer) throws UnableToCompleteException {
        beginWrappedProxy(writer, ClassCollection.proxyImplClassName);
        BasicProxyOutputter basicOutputter = new BasicProxyOutputter(oracle, logger, classCollection,
                ginjectorInspector, presenterInspector);
        basicOutputter.writeFields(writer);
        basicOutputter.writeInnerClasses(writer);
        basicOutputter.writeConstructor(writer, WRAPPED_CLASS_NAME, false);
        basicOutputter.writeMethods(writer);
        endWrappedProxy(writer);
    }

    public void beginWrappedProxy(SourceWriter writer, String wrappedProxySuperclassName) {
        writer.println();
        writer.println("public static class " + WRAPPED_CLASS_NAME);
        writer.println("extends " + wrappedProxySuperclassName + "<"
                + presenterInspector.getPresenterClassName() + "> " + "implements "
                + ClassCollection.delayedBindClassName + " {");
        writer.indent();
    }

    public void endWrappedProxy(SourceWriter writer) {
        writer.outdent();
        writer.println("}");
    }

    @Override
    String getSuperclassName() {
        return ClassCollection.proxyPlaceImplClassName;
    }

    public String[] getNameToken() {
        return nameTokens;
    }

    public String getGatekeeperParamsString() {
        if (gatekeeperParams == null) {
            return "null";
        }
        StringBuilder builder = new StringBuilder("new String[] {");
        for (String param : gatekeeperParams) {
            builder.append("\"").append(param).append("\",");
        }
        if (',' == builder.charAt(builder.length() - 1)) {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("}");
        return builder.toString();
    }

    @Override
    void initSubclass(JClassType proxyInterface) throws UnableToCompleteException {
        findNameTokens(proxyInterface);
        findGatekeeperMethod(proxyInterface);
        findGatekeeperParams(proxyInterface);
        findTitle(proxyInterface);
    }

    @Override
    void addSubclassImports(ClassSourceFileComposerFactory composerFactory) {
        if (title != null || presenterTitleMethod != null) {
            composerFactory.addImport(GetPlaceTitleEvent.class.getCanonicalName());
            composerFactory.addImport(AsyncCallback.class.getCanonicalName());
            composerFactory.addImport(Throwable.class.getCanonicalName());
        }
    }

    private void findNameTokens(JClassType proxyInterface) throws UnableToCompleteException {
        NameToken nameTokenAnnotation = proxyInterface.getAnnotation(NameToken.class);
        if (nameTokenAnnotation == null) {
            logger.log(TreeLogger.ERROR,
                    String.format("The proxy for '%s' is a Place, but is not annotated with @' +%s.",
                            presenterInspector.getPresenterClassName(), NameToken.class.getSimpleName()));
            throw new UnableToCompleteException();
        }
        nameTokens = nameTokenAnnotation.value();
        if (nameTokens.length == 0) {
            logger.log(TreeLogger.ERROR,
                    String.format("The proxy for '%s' is annotated with '@%s', but has no name token specified.",
                            presenterInspector.getPresenterClassName(), NameToken.class.getSimpleName()));
            throw new UnableToCompleteException();
        }
    }

    private void findGatekeeperMethod(JClassType proxyInterface) throws UnableToCompleteException {
        UseGatekeeper gatekeeperAnnotation = proxyInterface.getAnnotation(UseGatekeeper.class);
        if (gatekeeperAnnotation != null) {
            String gatekeeperName = gatekeeperAnnotation.value().getCanonicalName();
            JClassType customGatekeeperClass = oracle.findType(gatekeeperName);
            if (customGatekeeperClass == null) {
                logger.log(TreeLogger.ERROR, String.format("The class '%s' provided to @%s can't be found.",
                        gatekeeperName, UseGatekeeper.class.getSimpleName()));
                throw new UnableToCompleteException();
            }
            if (!customGatekeeperClass.isAssignableTo(classCollection.gatekeeperClass)) {
                logger.log(TreeLogger.ERROR, String.format("The class '%s' provided to @%s does not inherit from '%s'.",
                        gatekeeperName, UseGatekeeper.class.getSimpleName(), ClassCollection.gatekeeperClassName));
                throw new UnableToCompleteException();
            }
            // Find the appropriate get method in the Ginjector
            getGatekeeperMethod = ginjectorInspector.findGetMethod(customGatekeeperClass);
            if (getGatekeeperMethod == null) {
                logger.log(TreeLogger.ERROR,
                        String.format("The Ginjector '%s' does not have a get() method returning '%s'. This is " +
                                "required when using @%s.", ginjectorInspector.getGinjectorClassName(), gatekeeperName,
                                UseGatekeeper.class.getSimpleName()));
                throw new UnableToCompleteException();
            }
        }
        if (getGatekeeperMethod == null && proxyInterface.getAnnotation(NoGatekeeper.class) == null) {
            // No Gatekeeper specified, see if there is a DefaultGatekeeper defined in the ginjector
            getGatekeeperMethod = ginjectorInspector.findAnnotatedGetMethod(
                    classCollection.gatekeeperClass, DefaultGatekeeper.class, true);
        }
    }

    private void findGatekeeperParams(JClassType proxyInterface) {
        GatekeeperParams gatekeeperParamsAnnotation = proxyInterface.getAnnotation(GatekeeperParams.class);
        if (gatekeeperParamsAnnotation != null) {
            gatekeeperParams = gatekeeperParamsAnnotation.value();
        }
    }

    private void findTitle(JClassType proxyInterface)
            throws UnableToCompleteException {
        presenterTitleMethod = presenterInspector.findPresenterTitleMethod();
        Title titleAnnotation = proxyInterface.getAnnotation(Title.class);
        if (titleAnnotation != null) {
            title = titleAnnotation.value();
        }
        if (presenterTitleMethod != null && title != null) {
            logger.log(TreeLogger.ERROR, String.format(
                    "The proxy for '%s' is annotated with @' +%s and its presenter has a method annotated with @%s. " +
                            "Only once can be used.", presenterInspector.getPresenterClassName(),
                    Title.class.getSimpleName(), TitleFunction.class.getSimpleName()));
            throw new UnableToCompleteException();
        }
    }

    private String getPlaceInstantiationString() {
        if (getGatekeeperMethod == null) {
            return "new " + ClassCollection.placeImplClassName + "( nameToken )";
        } else {
            if (gatekeeperParams == null) {
                return "new " + ClassCollection.placeWithGatekeeperClassName
                        + "( nameToken, ginjector." + getGatekeeperMethod + "() )";
            } else {
                return "new " + ClassCollection.placeWithGatekeeperWithParamsClassName
                        + "( nameToken, ginjector." + getGatekeeperMethod + "(), gatekeeperParams )";
            }
        }
    }

    /**
     * Writes the method {@code protected void getPlaceTitle(final GetPlaceTitleEvent event)} if
     * one is needed.
     *
     * @param writer The {@link SourceWriter}.
     */
    private void writeGetPlaceTitleMethod(SourceWriter writer) {
        if (title != null) {
            writeGetPlaceTitleMethodConstantText(writer);
        } else if (presenterTitleMethod != null) {
            presenterTitleMethod.writeProxyMethod(writer);
        }
    }

    private void writeGetPlaceTitleMethodConstantText(SourceWriter writer) {
        writer.println();
        writer.println("protected void getPlaceTitle(GetPlaceTitleEvent event) {");
        writer.indent();
        writer.println("event.getHandler().onSetPlaceTitle( \"" + title
                + "\" );");
        writer.outdent();
        writer.println("}");
    }

    protected String createInitNameTokens() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (String nameToken : nameTokens) {
            sb.append('"').append(nameToken).append('"').append(',');
        }
        sb.setLength(sb.length() - 1);
        sb.append('}');
        return sb.toString();
    }

    @Override
    void writeSubclassDelayedBind(SourceWriter writer) {
        writer.println(WRAPPED_CLASS_NAME + " wrappedProxy = GWT.create(" + WRAPPED_CLASS_NAME + ".class);");
        writer.println("wrappedProxy.delayedBind( ginjector ); ");
        writer.println("setProxy(wrappedProxy); ");
        writer.println("String[] nameToken = " + createInitNameTokens() + "; ");
        writer.println("String[] gatekeeperParams = " + getGatekeeperParamsString() + ";");
        writer.println("setPlace(" + getPlaceInstantiationString() + ");");
    }

    @Override
    void writeSubclassMethods(SourceWriter writer) {
        writeGetPlaceTitleMethod(writer);
    }
}
