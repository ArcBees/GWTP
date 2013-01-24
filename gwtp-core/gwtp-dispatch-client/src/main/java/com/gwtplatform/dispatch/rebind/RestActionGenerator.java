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
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.gwtplatform.dispatch.client.rest.AbstractRestAction;
import com.gwtplatform.dispatch.shared.HttpMethod;
import com.gwtplatform.dispatch.shared.Action;

public class RestActionGenerator extends AbstractGenerator {
    private static final String SUFFIX = "Impl";
    private static final String SERIALIZATION_CONSTRUCTOR = "%s() { /* For serialization */ }";
    private static final String PUT_PATH_PARAM = "putPathParam(\"%s\", %s);";
    private static final String PUT_QUERY_PARAM = "putQueryParam(\"%s\", %s);";
    private static final String PUT_FORM_PARAM = "putFormParam(\"%s\", %s);";
    private static final String PUT_HEADER_PARAM = "putHeaderParam(\"%s\", %s);";
    private static final String SUPER_CLASS_CONSTRUCTOR = "super(HttpMethod.%s, \"%s\");";
    private static final String PATH_PARAM_MISSING = "%1$s has @PathParam(\"%2$s\"), but '%2$s' not found in %3$s.";
    private static final String PATH_PARAM = "{%s}";
    private static final String ACTION_CONSTRUCTOR = "public %s(%s) {";

    private HttpMethod httpMethod;
    private String path = "";

    public RestActionGenerator(String baseRestPath) {
        this.path = baseRestPath;
    }

    @Override
    public String generate(TreeLogger treeLogger, GeneratorContext generatorContext, String typeName)
            throws UnableToCompleteException {
        // TODO: Implement this method for backward compatibility (Custom actions)

        treeLogger.log(Type.ERROR, "Custom actions are not supported.");
        throw new UnableToCompleteException();
    }

    public String generate(TreeLogger treeLogger, GeneratorContext generatorContext, JMethod actionMethod)
            throws UnableToCompleteException {
        setGeneratorContext(generatorContext);
        setTypeOracle(generatorContext.getTypeOracle());
        setPropertyOracle(generatorContext.getPropertyOracle());
        setTreeLogger(treeLogger);
        setTypeClass(actionMethod.getReturnType().isClassOrInterface());

        String packageName = actionMethod.getEnclosingType().getPackage().getName();
        PrintWriter printWriter = tryCreatePrintWriter(packageName, getActionName(actionMethod), SUFFIX);

        if (printWriter != null) {
            setTreeLogger(getTreeLogger().branch(Type.INFO, "Generating rest action " + getClassName()));

            verifyIsAction();

            retrieveConfigAnnonations(actionMethod);

            writeClass(printWriter, actionMethod);
        }

        return getPackageName() + "." + getClassName();
    }

    private String getActionName(JMethod actionMethod) {
        StringBuilder stringBuilder = new StringBuilder(actionMethod.getName());
        Character firstChar = Character.toUpperCase(stringBuilder.charAt(0));

        stringBuilder.setCharAt(0, firstChar);

        stringBuilder.insert(0, "_");
        stringBuilder.insert(0, actionMethod.getEnclosingType().getName());

        return stringBuilder.toString();
    }

    private void verifyIsAction() throws UnableToCompleteException {
        JClassType actionClass;

        try {
            actionClass = getTypeOracle().getType(Action.class.getName());
        } catch (NotFoundException e) {
            getTreeLogger().log(Type.ERROR, "Unable to find interface Action.");
            throw new UnableToCompleteException();
        }

        if (!getTypeClass().isAssignableTo(actionClass)) {
            String typeName = getTypeClass().getQualifiedSourceName();
            getTreeLogger().log(Type.ERROR, typeName + " must implement Action.");

            throw new UnableToCompleteException();
        }
    }

    private void retrieveConfigAnnonations(JMethod actionMethod) throws UnableToCompleteException {
        retrieveHttpMethod(actionMethod);

        if (actionMethod.isAnnotationPresent(Path.class)) {
            path = concatenatePath(path, actionMethod.getAnnotation(Path.class).value());
        }
    }

    private void retrieveHttpMethod(JMethod actionMethod) throws UnableToCompleteException {
        Boolean moreThanOneAnnotation = false;

        if (actionMethod.isAnnotationPresent(GET.class)) {
            httpMethod = HttpMethod.GET;
        }

        if (actionMethod.isAnnotationPresent(POST.class)) {
            moreThanOneAnnotation = httpMethod != null;
            httpMethod = HttpMethod.POST;
        }

        if (actionMethod.isAnnotationPresent(PUT.class)) {
            moreThanOneAnnotation = moreThanOneAnnotation || httpMethod != null;
            httpMethod = HttpMethod.PUT;
        }

        if (actionMethod.isAnnotationPresent(DELETE.class)) {
            moreThanOneAnnotation = moreThanOneAnnotation || httpMethod != null;
            httpMethod = HttpMethod.DELETE;
        }

        if (actionMethod.isAnnotationPresent(HEAD.class)) {
            moreThanOneAnnotation = moreThanOneAnnotation || httpMethod != null;
            httpMethod = HttpMethod.HEAD;
        }

        if (httpMethod == null) {
            getTreeLogger().log(Type.ERROR, actionMethod.getName() + " has not http method annotation.");
            throw new UnableToCompleteException();
        } else if (moreThanOneAnnotation) {
            getTreeLogger().log(Type.WARN, actionMethod.getName() + " has more than one http method annotation.");
        }
    }

    private void writeClass(PrintWriter printWriter, JMethod actionMethod) {
        ClassSourceFileComposerFactory composer = initComposer();
        SourceWriter sourceWriter = composer.createSourceWriter(getGeneratorContext(), printWriter);

        writeConstructor(sourceWriter, actionMethod);

        closeDefinition(sourceWriter);
    }

    private ClassSourceFileComposerFactory initComposer() {
        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(getPackageName(), getClassName());

        String actionInterfaceParameterizedName = getTypeClass().getParameterizedQualifiedSourceName();
        String actionInterfaceName = getTypeClass().getQualifiedSourceName();

        String superclassName = AbstractRestAction.class.getSimpleName();
        superclassName = actionInterfaceParameterizedName.replace(actionInterfaceName, superclassName);

        composer.addImport(AbstractRestAction.class.getCanonicalName());
        composer.setSuperclass(superclassName);

        composer.addImport(HttpMethod.class.getCanonicalName());

        return composer;
    }

    private void writeConstructor(SourceWriter sourceWriter, JMethod actionMethod) {
        String arguments = getConstructorArgs(actionMethod);

        if (!arguments.isEmpty()) {
            sourceWriter.println(SERIALIZATION_CONSTRUCTOR, getClassName());
            sourceWriter.println();
        }

        sourceWriter.println(ACTION_CONSTRUCTOR, getClassName(), arguments);

        sourceWriter.indent();
        {
            sourceWriter.println(SUPER_CLASS_CONSTRUCTOR, httpMethod.name(), path);
            sourceWriter.println();

            writePutPathParams(sourceWriter, actionMethod);
            writePutQueryParams(sourceWriter, actionMethod);
            writePutFormParams(sourceWriter, actionMethod);
            writePutHeaderParams(sourceWriter, actionMethod);
        }
        sourceWriter.outdent();

        sourceWriter.println("}");
        sourceWriter.println();
    }

    private String getConstructorArgs(JMethod actionMethod) {
        StringBuilder sb = new StringBuilder("");

        for (JParameter parameter : actionMethod.getParameters()) {
            sb.append(parameter.getType().getParameterizedQualifiedSourceName())
                    .append(" ")
                    .append(parameter.getName())
                    .append(", ");
        }

        if (sb.length() != 0) {
            sb.delete(sb.length() - 2, sb.length());
        }

        return sb.toString();
    }

    private void writePutPathParams(SourceWriter sourceWriter, JMethod actionMethod) {
        Boolean needNewLine = false;

        for (JParameter parameter : actionMethod.getParameters()) {
            PathParam pathParam = parameter.getAnnotation(PathParam.class);

            if (pathParam != null) {
                verifyPathParamExists(actionMethod, pathParam);

                needNewLine = true;
                sourceWriter.println(PUT_PATH_PARAM, pathParam.value(), parameter.getName());
            }
        }

        if (needNewLine) {
            sourceWriter.println();
        }
    }

    private void verifyPathParamExists(JMethod actionMethod, PathParam pathParam) {
        if (!path.contains(String.format(PATH_PARAM, pathParam.value()))) {
            String warning = String.format(PATH_PARAM_MISSING, actionMethod.getName(), pathParam.value(), path);
            getTreeLogger().log(Type.WARN, warning);
        }
    }

    private void writePutQueryParams(SourceWriter sourceWriter, JMethod actionMethod) {
        Boolean needNewLine = false;

        for (JParameter parameter : actionMethod.getParameters()) {
            QueryParam queryParam = parameter.getAnnotation(QueryParam.class);

            if (queryParam != null) {
                needNewLine = true;
                sourceWriter.println(PUT_QUERY_PARAM, queryParam.value(), parameter.getName());
            }
        }

        if (needNewLine) {
            sourceWriter.println();
        }
    }

    private void writePutFormParams(SourceWriter sourceWriter, JMethod actionMethod) {
        Boolean needNewLine = false;

        for (JParameter parameter : actionMethod.getParameters()) {
            FormParam formParam = parameter.getAnnotation(FormParam.class);

            if (formParam != null) {
                needNewLine = true;
                sourceWriter.println(PUT_FORM_PARAM, formParam.value(), parameter.getName());
            }
        }

        if (needNewLine) {
            sourceWriter.println();
        }
    }

    private void writePutHeaderParams(SourceWriter sourceWriter, JMethod actionMethod) {
        Boolean needNewLine = false;

        for (JParameter parameter : actionMethod.getParameters()) {
            HeaderParam headerParam = parameter.getAnnotation(HeaderParam.class);

            if (headerParam != null) {
                needNewLine = true;
                sourceWriter.println(PUT_HEADER_PARAM, headerParam.value(), parameter.getName());
            }
        }

        if (needNewLine) {
            sourceWriter.println();
        }
    }
}
