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

package com.gwtplatform.dispatch.rebind;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.Path;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

public class RestServiceGenerator extends AbstractGenerator {
    private static final String SUFFIX = "Impl";
    private static final String OVERRIDE = "@Override";
    private static final String PUBLIC = "public";
    private static final String RETURN_NEW_ACTION = "return new %s(%s);";
    private static final String METHOD_DECLARATION = "%s %s {";
    private static final String CLOSE_BLOCK = "}";

    private String baseRestPath = "";
    private Map<JMethod, String> actions = new HashMap<JMethod, String>();

    @Override
    public String generate(TreeLogger treeLogger, GeneratorContext generatorContext, String typeName)
            throws UnableToCompleteException {
        setGeneratorContext(generatorContext);
        setTypeOracle(generatorContext.getTypeOracle());
        setTreeLogger(treeLogger);
        setTypeClass(getType(typeName));
        setPackageName(getTypeClass().getPackage().getName().replace(".shared.", ".client."));

        PrintWriter printWriter = tryCreatePrintWriter("", SUFFIX);

        if (printWriter != null) {
            setTreeLogger(getTreeLogger().branch(Type.INFO, "Generating rest service " + getClassName()));

            verifyIsInterface();

            retrieveServiceAnnonations();
            generateRestActions();

            writeClass(printWriter);
        }

        return getPackageName() + "." + getClassName();
    }

    private void verifyIsInterface() throws UnableToCompleteException {
        if (getTypeClass().isInterface() == null) {
            String typeName = getTypeClass().getQualifiedSourceName();
            getTreeLogger().log(Type.ERROR, typeName + " must be an interface.");

            throw new UnableToCompleteException();
        }
    }

    private void retrieveServiceAnnonations() {
        if (getTypeClass().isAnnotationPresent(Path.class)) {
            baseRestPath = normalizePath(getTypeClass().getAnnotation(Path.class).value());
        }
    }

    private void generateRestActions() throws UnableToCompleteException {
        JMethod[] actionMethods = getTypeClass().getMethods();
        if (actionMethods != null) {
            for (JMethod actionMethod : actionMethods) {
                generateRestAction(actionMethod);
            }
        }
    }

    private void generateRestAction(JMethod actionMethod) throws UnableToCompleteException {
        try {
            String actionClassName = new RestActionGenerator(baseRestPath)
                    .generate(getTreeLogger(), getGeneratorContext(), actionMethod);

            actions.put(actionMethod, actionClassName);
        } catch (UnableToCompleteException e) {
            String readableDeclaration = actionMethod.getReadableDeclaration(true, true, true, true, true);
            getTreeLogger().log(Type.ERROR, "Unable to generate rest action for method " + readableDeclaration + ".");

            throw new UnableToCompleteException();
        }
    }

    private void writeClass(PrintWriter printWriter) {
        ClassSourceFileComposerFactory composer = initComposer();
        SourceWriter sourceWriter = composer.createSourceWriter(getGeneratorContext(), printWriter);

        writeMethods(sourceWriter);

        closeDefinition(sourceWriter);
    }

    private ClassSourceFileComposerFactory initComposer() {
        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(getPackageName(), getClassName());

        composer.addImport(getTypeClass().getQualifiedSourceName());

        composer.addImplementedInterface(getTypeClass().getName());

        return composer;
    }

    private void writeMethods(SourceWriter sourceWriter) {
        for (Map.Entry<JMethod, String> action : actions.entrySet()) {
            writeMethod(action.getKey(), action.getValue(), sourceWriter);

            sourceWriter.println();
        }
    }

    private void writeMethod(JMethod method, String returnClass, SourceWriter sourceWriter) {
        sourceWriter.println(OVERRIDE);
        sourceWriter.println(METHOD_DECLARATION, PUBLIC, method.getReadableDeclaration(true, true, true, true, true));

        sourceWriter.indent();
        {
            sourceWriter.println(RETURN_NEW_ACTION, getClassName(returnClass), getParameters(method));
        }
        sourceWriter.outdent();

        sourceWriter.println(CLOSE_BLOCK);
    }

    private String getClassName(String name) {
        return name.substring(name.lastIndexOf('.') + 1);
    }

    private String getParameters(JMethod method) {
        JParameter parameters[] = method.getParameters();
        StringBuilder sb = new StringBuilder("");

        if (parameters != null && parameters.length != 0) {
            for (JParameter parameter : parameters) {
                sb.append(parameter.getName())
                        .append(", ");
            }

            sb.delete(sb.length() - 2, sb.length());
        }

        return sb.toString();
    }
}
