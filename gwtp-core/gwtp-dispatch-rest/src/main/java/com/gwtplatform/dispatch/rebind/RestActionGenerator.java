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
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.gwtplatform.dispatch.client.rest.AbstractRestAction;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.rest.HttpMethod;

public class RestActionGenerator extends AbstractGenerator {
    private static class AnnotatedMethodParameter {
        private JParameter parameter;
        private String fieldName;

        private AnnotatedMethodParameter(JParameter parameter, String fieldName) {
            this.parameter = parameter;
            this.fieldName = fieldName;
        }
    }

    private interface AnnotationValueResolver<T extends Annotation> {
        String resolve(T annotation);
    }

    @SuppressWarnings("unchecked")
    private static final List<Class<? extends Annotation>> PARAM_ANNOTATIONS =
            Arrays.asList(HeaderParam.class, QueryParam.class, PathParam.class, FormParam.class);

    private static final String SUFFIX = "Impl";
    private static final String SERIALIZATION_CONSTRUCTOR = "%s() { /* For serialization */ }";
    private static final String ADD_HEADER_PARAM = "addHeaderParam(\"%s\", %s);";
    private static final String ADD_PATH_PARAM = "addPathParam(\"%s\", %s);";
    private static final String ADD_QUERY_PARAM = "addQueryParam(\"%s\", %s);";
    private static final String ADD_FORM_PARAM = "addFormParam(\"%s\", %s);";
    private static final String SET_BODY_PARAM = "setBodyParam(%s, \"%s\");";
    private static final String SUPER_CLASS_CONSTRUCTOR = "super(HttpMethod.%s, \"%s\", \"%s\");";
    private static final String PATH_PARAM = "{%s}";
    private static final String ACTION_CONSTRUCTOR = "public %s(%s) {";
    private static final String CLOSE_BLOCK = "}";
    private static final String METHOD_PARAMETER = "%s %s";
    private static final String METHOD_PARAMETER_SEPARATOR = ", ";
    private static final String SHARED_PACKAGE = ".shared.";
    private static final String CLIENT_PACKAGE = ".client.";

    private static final String PATH_PARAM_MISSING = "@PathParam(\"%1$s\") declared, but '%1$s' not found in %2$s.";
    private static final String MANY_REST_ANNOTATIONS = "'%s' parameter's '%s' is annotated with more than one REST annotations.";
    private static final String MANY_POTENTIAL_BODY = "%s has more than one potential body parameter.";
    private static final String FORM_AND_BODY_PARAM = "%s has both @FormParam and a body parameter. You must specify one or the other.";

    private final List<AnnotatedMethodParameter> pathParams = new ArrayList<AnnotatedMethodParameter>();
    private final List<AnnotatedMethodParameter> headerParams = new ArrayList<AnnotatedMethodParameter>();
    private final List<AnnotatedMethodParameter> queryParams = new ArrayList<AnnotatedMethodParameter>();
    private final List<AnnotatedMethodParameter> formParams = new ArrayList<AnnotatedMethodParameter>();
    private final List<JParameter> potentialBodyParams = new ArrayList<JParameter>();

    private JMethod actionMethod;
    private HttpMethod httpMethod;
    private String path = "";
    private JParameter bodyParam;

    private String bodySerializerId = "";
    private String responseSerializerId = "";

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
        this.actionMethod = actionMethod;

        setGeneratorContext(generatorContext);
        setTypeOracle(generatorContext.getTypeOracle());
        setTreeLogger(treeLogger);
        setTypeClass(actionMethod.getReturnType().isClassOrInterface());
        setPackageName(actionMethod.getEnclosingType().getPackage().getName().replace(SHARED_PACKAGE, CLIENT_PACKAGE));

        PrintWriter printWriter = tryCreatePrintWriter(getActionName() + "_", SUFFIX);

        if (printWriter != null) {
            setTreeLogger(getTreeLogger().branch(Type.DEBUG, "Generating rest action " + getClassName()));

            verifyIsAction();
            JClassType resultType = getResultType();

            retrieveConfigAnnonations();
            retrieveParameterConfig();
            retrieveBodyConfig();

            verifyPathParamsExist();

            generateSerializers(resultType);

            writeClass(printWriter);
        }

        return getQualifiedClassName();
    }

    private String getActionName() {
        StringBuilder nameBuilder = new StringBuilder(actionMethod.getName());
        Character firstChar = Character.toUpperCase(nameBuilder.charAt(0));
        nameBuilder.setCharAt(0, firstChar);

        StringBuilder classNameBuilder = new StringBuilder();
        classNameBuilder.append(actionMethod.getEnclosingType().getName())
                .append("_")
                .append(nameBuilder);

        for (JType type : actionMethod.getErasedParameterTypes()) {
            classNameBuilder.append("_").append(type.getSimpleSourceName());
        }

        return classNameBuilder.toString();
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

    /**
     * @return The cooncrete result type.
     */
    private JClassType getResultType() throws UnableToCompleteException {
        JClassType actionInterface = getType(Action.class.getName());
        JClassType implementedAction = getInterfaceInHierarchy(getTypeClass(), actionInterface);

        JParameterizedType parameterized = implementedAction.isParameterized();
        if (parameterized != null && parameterized.getTypeArgs().length == 1) {
            return parameterized.getTypeArgs()[0];
        } else {
            getTreeLogger().log(Type.ERROR, "The action must specify a result type argument.");
            throw new UnableToCompleteException();
        }
    }

    private JClassType getInterfaceInHierarchy(JClassType classType, JClassType actionInterface) {
        if (getTypeClass().getName().equals(actionInterface.getName())) {
            return classType;
        } else {
            for (JClassType implemented : getTypeClass().getImplementedInterfaces()) {
                if (implemented.isAssignableTo(actionInterface)) {
                    return getInterfaceInHierarchy(implemented, actionInterface);
                }
            }
        }

        return null;
    }

    private void retrieveConfigAnnonations() throws UnableToCompleteException {
        retrieveHttpMethod();

        if (actionMethod.isAnnotationPresent(Path.class)) {
            path = concatenatePath(path, actionMethod.getAnnotation(Path.class).value());
        }
    }

    private void retrieveHttpMethod() throws UnableToCompleteException {
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
            getTreeLogger().log(Type.ERROR, actionMethod.getName() + " has no http method annotations.");
            throw new UnableToCompleteException();
        } else if (moreThanOneAnnotation) {
            getTreeLogger().log(Type.WARN, actionMethod.getName() + " has more than one http method annotation.");
        }
    }

    private void retrieveParameterConfig() throws UnableToCompleteException {
        JParameter[] parameters = actionMethod.getParameters();

        buildParamList(parameters, HeaderParam.class, new AnnotationValueResolver<HeaderParam>() {
            @Override
            public String resolve(HeaderParam annotation) {
                return annotation.value();
            }
        }, headerParams);

        buildParamList(parameters, PathParam.class, new AnnotationValueResolver<PathParam>() {
            @Override
            public String resolve(PathParam annotation) {
                return annotation.value();
            }
        }, pathParams);

        buildParamList(parameters, QueryParam.class, new AnnotationValueResolver<QueryParam>() {
            @Override
            public String resolve(QueryParam annotation) {
                return annotation.value();
            }
        }, queryParams);

        buildParamList(parameters, FormParam.class, new AnnotationValueResolver<FormParam>() {
            @Override
            public String resolve(FormParam annotation) {
                return annotation.value();
            }
        }, formParams);

        buildPotentialBodyParams();
    }

    private <T extends Annotation> void buildParamList(JParameter[] parameters, Class<T> annotationClass,
            AnnotationValueResolver<T> annotationValueResolver, List<AnnotatedMethodParameter> destination)
            throws UnableToCompleteException {
        List<Class<? extends Annotation>> restrictedAnnotations = getRestrictedAnnotations(annotationClass);

        for (JParameter parameter : parameters) {
            T parameterAnnotation = parameter.getAnnotation(annotationClass);

            if (parameterAnnotation != null) {
                if (hasAnnotationFrom(parameter, restrictedAnnotations)) {
                    getTreeLogger().log(Type.ERROR, String.format(MANY_REST_ANNOTATIONS, actionMethod.getName(), parameter.getName()));
                    throw new UnableToCompleteException();
                }

                String value = annotationValueResolver.resolve(parameterAnnotation);
                destination.add(new AnnotatedMethodParameter(parameter, value));
            }
        }
    }

    private List<Class<? extends Annotation>> getRestrictedAnnotations(Class<? extends Annotation> allowedAnnotation) {
        List<Class<? extends Annotation>> restrictedAnnotations = new ArrayList<Class<? extends Annotation>>();
        restrictedAnnotations.addAll(PARAM_ANNOTATIONS);
        restrictedAnnotations.remove(allowedAnnotation);

        return restrictedAnnotations;
    }

    private Boolean hasAnnotationFrom(JParameter parameter, List<Class<? extends Annotation>> restrictedAnnotations) {
        for (Class<? extends Annotation> restrictedAnnotation : restrictedAnnotations) {
            if (parameter.isAnnotationPresent(restrictedAnnotation)) {
                return true;
            }
        }

        return false;
    }

    private void buildPotentialBodyParams() {
        Collections.addAll(potentialBodyParams, actionMethod.getParameters());

        List<AnnotatedMethodParameter> annotatedParameters = new ArrayList<AnnotatedMethodParameter>();
        annotatedParameters.addAll(headerParams);
        annotatedParameters.addAll(pathParams);
        annotatedParameters.addAll(queryParams);
        annotatedParameters.addAll(formParams);

        for (AnnotatedMethodParameter annotatedParameter : annotatedParameters) {
            potentialBodyParams.remove(annotatedParameter.parameter);
        }
    }

    private void verifyPathParamsExist() throws UnableToCompleteException {
        for (AnnotatedMethodParameter param : pathParams) {
            verifyPathParamExists(param.fieldName);
        }
    }

    private void verifyPathParamExists(String param) throws UnableToCompleteException {
        if (!path.contains(String.format(PATH_PARAM, param))) {
            String warning = String.format(PATH_PARAM_MISSING, param, path);
            getTreeLogger().log(Type.ERROR, warning);

            throw new UnableToCompleteException();
        }
    }

    private void retrieveBodyConfig() throws UnableToCompleteException {
        if (potentialBodyParams.isEmpty()) {
            return;
        }

        if (potentialBodyParams.size() > 1) {
            getTreeLogger().log(Type.ERROR, String.format(MANY_POTENTIAL_BODY, actionMethod.getName()));
            throw new UnableToCompleteException();
        }

        if (!formParams.isEmpty()) {
            getTreeLogger().log(Type.ERROR, String.format(FORM_AND_BODY_PARAM, actionMethod.getName()));
            throw new UnableToCompleteException();
        }

        bodyParam = potentialBodyParams.get(0);
    }

    private void generateSerializers(JClassType resultType) throws UnableToCompleteException {
        if (bodyParam != null) {
            bodySerializerId = generateSerializer(bodyParam.getType().isClassOrInterface());
        }

        responseSerializerId = generateSerializer(resultType);
    }

    private String generateSerializer(JClassType type) throws UnableToCompleteException {
        SerializerGenerator bodySerializerGenerator = new SerializerGenerator(type);
        bodySerializerGenerator.generate(getTreeLogger(), getGeneratorContext());

        return bodySerializerGenerator.getSerializerId();
    }

    private void writeClass(PrintWriter printWriter) {
        ClassSourceFileComposerFactory composer = initComposer();
        SourceWriter sourceWriter = composer.createSourceWriter(getGeneratorContext(), printWriter);

        writeConstructor(sourceWriter);

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

    private void writeConstructor(SourceWriter sourceWriter) {
        String arguments = composeConstructorArgs();

        if (!arguments.isEmpty()) {
            sourceWriter.println(SERIALIZATION_CONSTRUCTOR, getClassName());
            sourceWriter.println();
        }

        sourceWriter.println(ACTION_CONSTRUCTOR, getClassName(), arguments);

        sourceWriter.indent();
        {
            sourceWriter.println(SUPER_CLASS_CONSTRUCTOR, httpMethod.name(), path, responseSerializerId);
            sourceWriter.println();

            writeAddParams(sourceWriter, headerParams, ADD_HEADER_PARAM);
            writeAddParams(sourceWriter, pathParams, ADD_PATH_PARAM);
            writeAddParams(sourceWriter, queryParams, ADD_QUERY_PARAM);
            writeAddParams(sourceWriter, formParams, ADD_FORM_PARAM);

            if (bodyParam != null) {
                sourceWriter.println(SET_BODY_PARAM, bodyParam.getName(), bodySerializerId);
            }
        }
        sourceWriter.outdent();

        sourceWriter.println(CLOSE_BLOCK);
        sourceWriter.println();
    }

    private String composeConstructorArgs() {
        JParameter[] parameters = actionMethod.getParameters();
        StringBuilder sb = new StringBuilder();

        Integer i = 1;
        for (JParameter parameter : parameters) {
            sb.append(String.format(METHOD_PARAMETER, parameter.getType().getParameterizedQualifiedSourceName(),
                    parameter.getName()));

            if (i < parameters.length) {
                sb.append(METHOD_PARAMETER_SEPARATOR);
            }

            i++;
        }

        return sb.toString();
    }

    private void writeAddParams(SourceWriter sourceWriter, List<AnnotatedMethodParameter> parameters, String format) {
        Boolean needNewLine = false;

        for (AnnotatedMethodParameter parameter : parameters) {
            needNewLine = true;
            sourceWriter.println(format, parameter.fieldName, parameter.parameter.getName());
        }

        if (needNewLine) {
            sourceWriter.println();
        }
    }
}
