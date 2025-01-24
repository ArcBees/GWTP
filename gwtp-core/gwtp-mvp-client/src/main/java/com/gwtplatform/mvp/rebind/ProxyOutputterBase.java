/*
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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.common.client.CodeSplitBundleProvider;
import com.gwtplatform.common.client.CodeSplitProvider;
import com.gwtplatform.common.client.StandardProvider;
import com.gwtplatform.mvp.client.DelayedBindRegistry;
import com.gwtplatform.mvp.client.TabData;
import com.gwtplatform.mvp.client.TabDataBasic;
import com.gwtplatform.mvp.client.proxy.NotifyingAsyncCallback;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

/**
 * The base class of any {@link ProxyOutputter} implementation.
 */
public abstract class ProxyOutputterBase implements ProxyOutputter {

    protected final TypeOracle oracle;
    protected final TreeLogger logger;
    protected final ClassCollection classCollection;
    protected final GinjectorInspector ginjectorInspector;
    protected final PresenterInspector presenterInspector;
    private final List<ProxyEventMethod> proxyEventMethods = new ArrayList<ProxyEventMethod>();

    private JClassType proxyInterface;

    ProxyOutputterBase(TypeOracle oracle,
            TreeLogger logger,
            ClassCollection classCollection,
            GinjectorInspector ginjectorInspector,
            PresenterInspector presenterInspector) {
        this.oracle = oracle;
        this.logger = logger;
        this.classCollection = classCollection;
        this.ginjectorInspector = ginjectorInspector;
        this.presenterInspector = presenterInspector;
    }

    /**
     * Initializes this proxy outputter given the specified proxy interface.
     * You should still call {@link #findProxyEvents()} after that if you
     * want this outputter to forward any proxy event.
     */
    public void init(JClassType proxyInterface) throws UnableToCompleteException {
        this.proxyInterface = proxyInterface;
        initSubclass(proxyInterface);
    }

    public void findProxyEvents() throws UnableToCompleteException {
        presenterInspector.collectProxyEvents(proxyEventMethods);
    }

    abstract void initSubclass(JClassType proxyInterface) throws UnableToCompleteException;

    @Override
    public void initComposerFactory(ClassSourceFileComposerFactory composerFactory) {
        addImports(composerFactory);
        setInterfacesAndSuperclass(composerFactory);
    }

    /**
     * Add all the imports required to output this proxy.
     *
     * @param composerFactory The composer factory used to generate to proxy.
     */
    private void addImports(ClassSourceFileComposerFactory composerFactory) {
        // TODO Distribute among subclasses in addSubclassImports()
        composerFactory.addImport(GWT.class.getCanonicalName());
        composerFactory.addImport(Inject.class.getCanonicalName()); // Obsolete?
        composerFactory.addImport(Provider.class.getCanonicalName());
        composerFactory.addImport(NotifyingAsyncCallback.class.getCanonicalName());
        composerFactory.addImport(EventBus.class.getCanonicalName());
        composerFactory.addImport(StandardProvider.class.getCanonicalName());
        composerFactory.addImport(CodeSplitProvider.class.getCanonicalName());
        composerFactory.addImport(CodeSplitBundleProvider.class.getCanonicalName());
        composerFactory.addImport(ClassCollection.proxyImplClassName);
        composerFactory.addImport(ClassCollection.proxyPlaceImplClassName);
        composerFactory.addImport(RevealContentHandler.class.getCanonicalName());
        composerFactory.addImport(ClassCollection.delayedBindClassName);
        composerFactory.addImport(ClassCollection.tabContentProxyPlaceImplClassName);
        composerFactory.addImport(DelayedBindRegistry.class.getCanonicalName());
        composerFactory.addImport(Ginjector.class.getCanonicalName());
        composerFactory.addImport(RevealContentEvent.class.getCanonicalName()); // Obsolete?
        composerFactory.addImport(Scheduler.class.getCanonicalName());
        composerFactory.addImport(Command.class.getCanonicalName());
        composerFactory.addImport(AsyncCallback.class.getCanonicalName());
        composerFactory.addImport(TabData.class.getCanonicalName());
        composerFactory.addImport(TabDataBasic.class.getCanonicalName());
        addSubclassImports(composerFactory);
    }

    abstract void addSubclassImports(ClassSourceFileComposerFactory composerFactory);

    /**
     * Set all the interfaces implemented by this proxy and its superclass.
     *
     * @param composerFactory The composer factory used to generate to proxy.
     */
    private void setInterfacesAndSuperclass(ClassSourceFileComposerFactory composerFactory) {
        // Sets interfaces and superclass
        composerFactory.addImplementedInterface(proxyInterface.getParameterizedQualifiedSourceName());
        composerFactory.addImplementedInterface(ClassCollection.delayedBindClassName);
        composerFactory.setSuperclass(getSuperclassName() + "<"
                + presenterInspector.getPresenterClassName() + ">");

        // Add all implemented handlers
        for (ProxyEventMethod proxyEventMethod : proxyEventMethods) {
            proxyEventMethod.addImplementedInterface(composerFactory);
        }
    }

    /**
     * Access the name of the superclass from which the proxy implementation
     * should inherit.
     *
     * @return The name of the superclass.
     */
    abstract String getSuperclassName();

    @Override
    public final void writeFields(SourceWriter writer) {
        writer.println();
        writer.println("private " + ginjectorInspector.getGinjectorClassName() + " ginjector;");
    }

    @Override
    public final void writeConstructor(SourceWriter writer, String className, boolean registerDelayedBind) {
        writer.println();
        writer.println("public " + className + "() {");
        if (registerDelayedBind) {
            writer.indent();
            writer.println("DelayedBindRegistry.register(this);");
            writer.outdent();
        }
        writer.println("}");
    }

    @Override
    public final void writeMethods(SourceWriter writer) throws UnableToCompleteException {
        // Write delayedBind
        writer.println();
        writer.println("@Override");
        writer.println("public void delayedBind(Ginjector baseGinjector) {");
        writer.indent();
        writeGinjectorAssignation(writer, ginjectorInspector.getGinjectorClassName());
        writer.println("bind(ginjector.getPlaceManager(),");
        writer.println("    ginjector.getEventBus());");
        writeSubclassDelayedBind(writer);
        writeAddHandlerForProxyEvents(writer);
        writer.outdent();
        writer.println("}");
        writeHandlerMethodsForProxyEvents(writer);

        writeSubclassMethods(writer);
    }

    abstract void writeSubclassDelayedBind(SourceWriter writer);

    abstract void writeSubclassMethods(SourceWriter writer) throws UnableToCompleteException;

    /**
     * Writes all the calls to {@code addHandler} needed to register all the
     * proxy events.
     *
     * @param writer The {@link SourceWriter}.
     */
    private void writeAddHandlerForProxyEvents(SourceWriter writer) {
        for (ProxyEventMethod proxyEventMethod : proxyEventMethods) {
            proxyEventMethod.writeAddHandler(writer);
        }
    }

    /**
     * Writes all the handlers needed to handle the proxy events.
     *
     * @param writer The {@link SourceWriter}.
     */
    private void writeHandlerMethodsForProxyEvents(SourceWriter writer) {
        for (ProxyEventMethod proxyEventMethod : proxyEventMethods) {
            proxyEventMethod.writeHandlerMethod(writer);
        }
    }

    /**
     * Writes a local ginjector variable to the source writer.
     */
    private void writeGinjectorAssignation(SourceWriter writer, String ginjectorClassName) {
        writer.println("ginjector = (" + ginjectorClassName + ")baseGinjector;");
    }
}
