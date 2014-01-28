/**
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.rebind;

import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
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
import javax.ws.rs.core.HttpHeaders;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.inject.assistedinject.Assisted;
import com.gwtplatform.dispatch.rest.rebind.event.RegisterMetadataEvent;
import com.gwtplatform.dispatch.rest.rebind.event.RegisterSerializableTypeEvent;
import com.gwtplatform.dispatch.rest.rebind.type.ActionBinding;
import com.gwtplatform.dispatch.rest.rebind.type.ClassBinding;
import com.gwtplatform.dispatch.rest.rebind.type.FromParamMethodCall;
import com.gwtplatform.dispatch.rest.rebind.type.MethodCall;
import com.gwtplatform.dispatch.rest.rebind.type.NoParamMethodCall;
import com.gwtplatform.dispatch.rest.rebind.util.AnnotationValueResolver;
import com.gwtplatform.dispatch.rest.rebind.util.FormParamValueResolver;
import com.gwtplatform.dispatch.rest.rebind.util.GeneratorUtil;
import com.gwtplatform.dispatch.rest.rebind.util.HeaderParamValueResolver;
import com.gwtplatform.dispatch.rest.rebind.util.PathParamValueResolver;
import com.gwtplatform.dispatch.rest.rebind.util.QueryParamValueResolver;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.MetadataType;
import com.gwtplatform.dispatch.rest.shared.RestAction;

import static com.gwtplatform.dispatch.rest.shared.MetadataType.BODY_TYPE;
import static com.gwtplatform.dispatch.rest.shared.MetadataType.RESPONSE_TYPE;

public class ActionGenerator extends AbstractVelocityGenerator {
    private static class AnnotatedMethodParameter {
        private JParameter parameter;
        private String fieldName;

        private AnnotatedMethodParameter(JParameter parameter, String fieldName) {
            this.parameter = parameter;
            this.fieldName = fieldName;
        }
    }

    @SuppressWarnings("unchecked")
    private static final List<Class<? extends Annotation>> PARAM_ANNOTATIONS =
            Arrays.asList(HeaderParam.class, QueryParam.class, PathParam.class, FormParam.class);

    private static final String TEMPLATE = "com/gwtplatform/dispatch/rest/rebind/RestAction.vm";
    private static final String MANY_REST_ANNOTATIONS = "'%s' parameter's '%s' is annotated with more than one REST " +
                                                        "annotations.";
    private static final String MANY_POTENTIAL_BODY = "%s has more than one potential body parameter.";
    private static final String FORM_AND_BODY_PARAM = "%s has both @FormParam and a body parameter. You must specify " +
                                                      "one or the other.";
    private static final String ADD_HEADER_PARAM = "addHeaderParam";
    private static final String ADD_PATH_PARAM = "addPathParam";
    private static final String ADD_QUERY_PARAM = "addQueryParam";
    private static final String ADD_FORM_PARAM = "addFormParam";
    private static final String SET_BODY_PARAM = "setBodyParam";

    private final EventBus eventBus;

    private final JMethod actionMethod;
    private final ClassBinding parent;
    private final List<JParameter> parameters;
    private final JType returnType;
    private final List<AnnotatedMethodParameter> pathParams = new ArrayList<AnnotatedMethodParameter>();
    private final List<AnnotatedMethodParameter> headerParams = new ArrayList<AnnotatedMethodParameter>();
    private final List<AnnotatedMethodParameter> queryParams = new ArrayList<AnnotatedMethodParameter>();
    private final List<AnnotatedMethodParameter> formParams = new ArrayList<AnnotatedMethodParameter>();
    private final List<JParameter> potentialBodyParams = new ArrayList<JParameter>();

    private HttpMethod httpMethod;
    private String path = "";
    private JParameter bodyParam;

    @Inject
    ActionGenerator(
            EventBus eventBus,
            TypeOracle typeOracle,
            Logger logger,
            Provider<VelocityContext> velocityContextProvider,
            VelocityEngine velocityEngine,
            GeneratorUtil generatorUtil,
            @Assisted JMethod actionMethod,
            @Assisted ClassBinding parent) {
        super(typeOracle, logger, velocityContextProvider, velocityEngine, generatorUtil);

        this.eventBus = eventBus;
        this.actionMethod = actionMethod;
        this.parent = parent;
        this.parameters = Lists.newArrayList(parent.getCtorParameters());

        returnType = actionMethod.getReturnType();
    }

    public ActionBinding generate(String basePath) throws UnableToCompleteException {
        String implName = getSuperTypeName() + SUFFIX;
        PrintWriter printWriter = getGeneratorUtil().tryCreatePrintWriter(getPackage(), implName);

        if (printWriter != null) {
            doGenerate(basePath, implName, printWriter);

            registerMetadata();
        } else {
            getLogger().debug("Action already generated. Returning.");
        }

        ActionBinding actionBinding = new ActionBinding(implName, actionMethod.getName(),
                getResultType().getParameterizedQualifiedSourceName());
        actionBinding.setSuperTypeName(getSuperTypeName());
        actionBinding.setImplPackage(getPackage());
        actionBinding.setCtorParameters(parameters);
        return actionBinding;
    }

    @Override
    protected void populateVelocityContext(VelocityContext velocityContext) throws UnableToCompleteException {
        velocityContext.put("resultClass", getResultType());
        velocityContext.put("httpMethod", httpMethod);
        velocityContext.put("methodCalls", getMethodCallsToAdd());
        velocityContext.put("restPath", path);
        velocityContext.put("ctorParams", parameters);
    }

    @Override
    protected String getPackage() {
        return parent.getImplPackage().replace(SHARED_PACKAGE, CLIENT_PACKAGE);
    }

    private String getQualifiedImplName() {
        return getPackage() + "." + getSuperTypeName() + SUFFIX;
    }

    private String getSuperTypeName() {
        // The service may define overloads, in which case the method name is not unique.
        // The method index will ensure the generated class uniqueness.
        int methodIndex = Arrays.asList(actionMethod.getEnclosingType().getMethods()).indexOf(actionMethod);

        return parent.getSuperTypeName() + "_" + methodIndex + "_" + actionMethod.getName();
    }

    private List<MethodCall> getMethodCallsToAdd() {
        List<MethodCall> methodCalls = new ArrayList<MethodCall>();

        methodCalls.addAll(getMethodCallsToAdd(headerParams, ADD_HEADER_PARAM));
        methodCalls.addAll(getMethodCallsToAdd(pathParams, ADD_PATH_PARAM));
        methodCalls.addAll(getMethodCallsToAdd(queryParams, ADD_QUERY_PARAM));
        methodCalls.addAll(getMethodCallsToAdd(formParams, ADD_FORM_PARAM));

        addContentTypeHeaderMethodCall(methodCalls);

        if (bodyParam != null) {
            methodCalls.add(new FromParamMethodCall(SET_BODY_PARAM, null, bodyParam));
        }

        return methodCalls;
    }

    private void doGenerate(String basePath, String implName, PrintWriter printWriter)
            throws UnableToCompleteException {
        verifyIsAction();

        retrieveConfigAnnotations(basePath);
        retrieveParameterConfig();
        retrieveBodyConfig();

        mergeTemplate(printWriter, TEMPLATE, implName);
    }

    private void addContentTypeHeaderMethodCall(List<MethodCall> methodCalls) {
        Consumes consumes = actionMethod.getAnnotation(Consumes.class);

        if (consumes != null && consumes.value().length > 0) {
            methodCalls.add(new NoParamMethodCall(ADD_HEADER_PARAM, HttpHeaders.CONTENT_TYPE, consumes.value()[0]));
        }
    }

    private List<FromParamMethodCall> getMethodCallsToAdd(List<AnnotatedMethodParameter> methodParameters,
                                                          String methodName) {
        List<FromParamMethodCall> methodCalls = new ArrayList<FromParamMethodCall>();
        for (AnnotatedMethodParameter methodParameter : methodParameters) {
            methodCalls.add(new FromParamMethodCall(methodName, methodParameter.fieldName, methodParameter.parameter));
        }
        return methodCalls;
    }

    private void registerMetadata() throws UnableToCompleteException {
        if (bodyParam != null) {
            registerMetadatum(BODY_TYPE, bodyParam.getType());
        }

        registerMetadatum(RESPONSE_TYPE, getResultType());
    }

    private void registerMetadatum(MetadataType metadataType, JType type) {
        String typeLiteral = "\"" + type.getParameterizedQualifiedSourceName() + "\"";

        eventBus.post(new RegisterMetadataEvent(getQualifiedImplName(), metadataType, typeLiteral));

        if (!Void.class.getCanonicalName().equals(type.getQualifiedSourceName()) && type.isPrimitive() == null) {
            eventBus.post(new RegisterSerializableTypeEvent(type));
        }
    }

    private void verifyIsAction() throws UnableToCompleteException {
        JClassType actionClass = null;

        try {
            actionClass = getTypeOracle().getType(RestAction.class.getName());
        } catch (NotFoundException e) {
            getLogger().die("Unable to find interface Action.");
        }

        JClassType returnClass = returnType.isClassOrInterface();
        if (!returnClass.isAssignableTo(actionClass)) {
            String typeName = returnClass.getQualifiedSourceName();
            getLogger().die(typeName + " must implement RestAction.");
        }
    }

    private void retrieveConfigAnnotations(String basePath) throws UnableToCompleteException {
        retrieveHttpMethod();

        if (actionMethod.isAnnotationPresent(Path.class)) {
            path = concatenatePath(basePath, actionMethod.getAnnotation(Path.class).value());
        } else {
            path = basePath;
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
            getLogger().die(actionMethod.getName() + " has no http method annotations.");
        } else if (moreThanOneAnnotation) {
            getLogger().warn(actionMethod.getName() + " has more than one http method annotation.");
        }
    }

    private void retrieveParameterConfig() throws UnableToCompleteException {
        Collections.addAll(parameters, actionMethod.getParameters());

        buildParamList(parameters, HeaderParam.class, new HeaderParamValueResolver(), headerParams);
        buildParamList(parameters, PathParam.class, new PathParamValueResolver(), pathParams);
        buildParamList(parameters, QueryParam.class, new QueryParamValueResolver(), queryParams);
        buildParamList(parameters, FormParam.class, new FormParamValueResolver(), formParams);

        buildPotentialBodyParams();
    }

    private <T extends Annotation> void buildParamList(List<JParameter> parameters, Class<T> annotationClass,
                                                       AnnotationValueResolver<T> annotationValueResolver,
                                                       List<AnnotatedMethodParameter> destination)
            throws UnableToCompleteException {
        List<Class<? extends Annotation>> restrictedAnnotations = getRestrictedAnnotations(annotationClass);

        for (JParameter parameter : parameters) {
            T parameterAnnotation = parameter.getAnnotation(annotationClass);

            if (parameterAnnotation != null) {
                if (hasAnnotationFrom(parameter, restrictedAnnotations)) {
                    getLogger().die(String.format(MANY_REST_ANNOTATIONS, actionMethod.getName(), parameter.getName()));
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
        potentialBodyParams.addAll(parameters);

        List<AnnotatedMethodParameter> annotatedParameters = new ArrayList<AnnotatedMethodParameter>();
        annotatedParameters.addAll(headerParams);
        annotatedParameters.addAll(pathParams);
        annotatedParameters.addAll(queryParams);
        annotatedParameters.addAll(formParams);

        for (AnnotatedMethodParameter annotatedParameter : annotatedParameters) {
            potentialBodyParams.remove(annotatedParameter.parameter);
        }
    }

    private void retrieveBodyConfig() throws UnableToCompleteException {
        if (potentialBodyParams.isEmpty()) {
            return;
        }

        if (potentialBodyParams.size() > 1) {
            getLogger().die(String.format(MANY_POTENTIAL_BODY, actionMethod.getName()));
        }

        if (!formParams.isEmpty()) {
            getLogger().die(String.format(FORM_AND_BODY_PARAM, actionMethod.getName()));
        }

        bodyParam = potentialBodyParams.get(0);
    }

    @SuppressWarnings("ConstantConditions")
    private JClassType getResultType() throws UnableToCompleteException {
        JParameterizedType parameterized = returnType.isParameterized();
        if (parameterized == null || parameterized.getTypeArgs().length != 1) {
            getLogger().die("The action must specify a result type argument.");
        }
        return parameterized.getTypeArgs()[0];
    }
}
