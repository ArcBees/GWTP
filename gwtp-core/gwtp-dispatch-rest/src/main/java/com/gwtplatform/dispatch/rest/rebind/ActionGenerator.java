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

import java.lang.annotation.Annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
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
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.inject.assistedinject.Assisted;
import com.gwtplatform.dispatch.rest.client.MetadataType;
import com.gwtplatform.dispatch.rest.rebind.type.ActionBinding;
import com.gwtplatform.dispatch.rest.rebind.type.MethodCall;
import com.gwtplatform.dispatch.rest.rebind.type.ResourceBinding;
import com.gwtplatform.dispatch.rest.rebind.util.GeneratorUtil;
import com.gwtplatform.dispatch.rest.rebind2.HttpVerb;
import com.gwtplatform.dispatch.rest.rebind2.events.RegisterMetadataEvent;
import com.gwtplatform.dispatch.rest.rebind2.events.RegisterSerializableTypeEvent;
import com.gwtplatform.dispatch.rest.rebind2.parameter.FormParamValueResolver;
import com.gwtplatform.dispatch.rest.rebind2.parameter.HeaderParamValueResolver;
import com.gwtplatform.dispatch.rest.rebind2.parameter.HttpParamValueResolver;
import com.gwtplatform.dispatch.rest.rebind2.parameter.PathParamValueResolver;
import com.gwtplatform.dispatch.rest.rebind2.parameter.QueryParamValueResolver;
import com.gwtplatform.dispatch.rest.shared.DateFormat;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.NoXsrfHeader;
import com.gwtplatform.dispatch.rest.shared.RestAction;

import static com.gwtplatform.dispatch.rest.client.MetadataType.BODY_TYPE;
import static com.gwtplatform.dispatch.rest.client.MetadataType.RESPONSE_TYPE;

public class ActionGenerator extends AbstractVelocityGenerator {
    private static class AnnotatedMethodParameter {
        private JParameter parameter;
        private String fieldName;

        private AnnotatedMethodParameter(JParameter parameter, String fieldName) {
            this.parameter = parameter;
            this.fieldName = fieldName;
        }
    }

    private static final String TEMPLATE = "com/gwtplatform/dispatch/rest/rebind/RestAction.vm";

    private static final String MANY_REST_ANNOTATIONS = "'%s' parameter's '%s' is annotated with more than one REST " +
            "annotations.";
    private static final String DATE_FORMAT_NOT_DATE = "'%s' parameter's '%s' is annotated with @DateFormat but its " +
            "type is not Date";
    private static final String MANY_POTENTIAL_BODY = "%s has more than one potential body parameter.";
    private static final String FORM_AND_BODY_PARAM = "%s has both @FormParam and a body parameter. You must specify " +
            "one or the other.";
    private static final String ADD_HEADER_PARAM = "addHeaderParam";
    private static final String ADD_PATH_PARAM = "addPathParam";
    private static final String ADD_QUERY_PARAM = "addQueryParam";
    private static final String ADD_FORM_PARAM = "addFormParam";
    private static final String SET_BODY_PARAM = "setBodyParam";
    private static final String ESCAPED_STRING = "\"%s\"";

    @SuppressWarnings("unchecked")
    private static final List<Class<? extends Annotation>> PARAM_ANNOTATIONS =
            Arrays.asList(HeaderParam.class, QueryParam.class, PathParam.class, FormParam.class);

    private final EventBus eventBus;

    private final JMethod actionMethod;
    private final ResourceBinding parent;
    private final String path;
    private final boolean secured;
    private final List<JParameter> parameters;
    private final List<AnnotatedMethodParameter> pathParams = new ArrayList<AnnotatedMethodParameter>();
    private final List<AnnotatedMethodParameter> headerParams = new ArrayList<AnnotatedMethodParameter>();
    private final List<AnnotatedMethodParameter> queryParams = new ArrayList<AnnotatedMethodParameter>();
    private final List<AnnotatedMethodParameter> formParams = new ArrayList<AnnotatedMethodParameter>();
    private final List<JParameter> potentialBodyParams = new ArrayList<JParameter>();

    private boolean returnsAction;
    private JClassType resultClass;
    private JPrimitiveType resultPrimitive;
    private HttpMethod httpMethod;
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
            @Assisted ResourceBinding parent) {
        super(typeOracle, logger, velocityContextProvider, velocityEngine, generatorUtil);

        this.eventBus = eventBus;
        this.actionMethod = actionMethod;
        this.parent = parent;

        path = concatenatePath(parent.getResourcePath(), extractPath(actionMethod));
        secured = parent.isSecured() && !actionMethod.isAnnotationPresent(NoXsrfHeader.class);
        parameters = Lists.newArrayList(parent.getCtorParameters());
    }

    public ActionBinding generate() throws UnableToCompleteException {
        String implName = getSuperTypeName() + SUFFIX;
        if (doGenerate(implName)) {
            registerMetadata();
        }
        return createActionBinding(implName);
    }

    @Override
    protected void populateVelocityContext(VelocityContext velocityContext) throws UnableToCompleteException {
        velocityContext.put("resultClass", resultClass);
        velocityContext.put("httpMethod", httpMethod);
        velocityContext.put("methodCalls", getMethodCallsToAdd());
        velocityContext.put("restPath", path);
        velocityContext.put("ctorParams", parameters);
        velocityContext.put("secured", secured);
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
            methodCalls.add(new MethodCall(SET_BODY_PARAM, bodyParam.getName()));
        }

        return methodCalls;
    }

    private List<MethodCall> getMethodCallsToAdd(List<AnnotatedMethodParameter> methodParameters, String methodName) {
        List<MethodCall> methodCalls = new ArrayList<MethodCall>();
        for (AnnotatedMethodParameter methodParameter : methodParameters) {
            List<String> arguments = Lists.newArrayList(String.format(ESCAPED_STRING, methodParameter.fieldName),
                    methodParameter.parameter.getName());

            maybeAddDateFormat(arguments, methodParameter);

            methodCalls.add(new MethodCall(methodName, arguments.toArray(new String[arguments.size()])));
        }

        return methodCalls;
    }

    private void maybeAddDateFormat(List<String> args, AnnotatedMethodParameter methodParameter) {
        if (methodParameter.parameter.isAnnotationPresent(DateFormat.class)) {
            String dateFormat = methodParameter.parameter.getAnnotation(DateFormat.class).value();
            args.add(String.format(ESCAPED_STRING, dateFormat));
        }
    }

    private void addContentTypeHeaderMethodCall(List<MethodCall> methodCalls) {
        Consumes consumes = actionMethod.getAnnotation(Consumes.class);

        if (consumes != null && consumes.value().length > 0) {
            MethodCall methodCall = new MethodCall(
                    ADD_HEADER_PARAM,
                    String.format(ESCAPED_STRING, HttpHeaders.CONTENT_TYPE),
                    String.format(ESCAPED_STRING, consumes.value()[0]));
            methodCalls.add(methodCall);
        }
    }

    private void registerMetadata() throws UnableToCompleteException {
        if (bodyParam != null) {
            registerMetadatum(BODY_TYPE, bodyParam.getType());
        }

        registerMetadatum(RESPONSE_TYPE, resultClass);
    }

    private void registerMetadatum(MetadataType metadataType, JType type) {
        String typeLiteral = "\"" + type.getParameterizedQualifiedSourceName() + "\"";

        RegisterMetadataEvent.post(eventBus, getQualifiedImplName(), metadataType, typeLiteral);

        if (!Void.class.getCanonicalName().equals(type.getQualifiedSourceName()) && type.isPrimitive() == null) {
            RegisterSerializableTypeEvent.post(eventBus, type);
        }
    }

    private boolean doGenerate(String implName) throws UnableToCompleteException {
        verifyReturnType();

        retrieveHttpMethod();
        retrieveParameterConfig();
        retrieveBodyConfig();

        return mergeTemplate(TEMPLATE, implName);
    }

    private void verifyReturnType() throws UnableToCompleteException {
        JClassType actionClass = getGeneratorUtil().getType(RestAction.class.getName());
        JClassType returnInterface = getReturnType().isInterface();

        returnsAction = returnInterface != null && returnInterface.isAssignableTo(actionClass);
        resultPrimitive = getReturnType().isPrimitive();
        resultClass = parseResultType();
    }

    private JClassType parseResultType() throws UnableToCompleteException {
        JClassType resultType;

        if (returnsAction) {
            resultType = parseActionResultType();
        } else {
            resultType = parseRawResultType();
        }

        return resultType;
    }

    private JClassType parseActionResultType() throws UnableToCompleteException {
        JClassType resultType = null;
        JParameterizedType parameterized = getReturnType().isParameterized();

        if (parameterized == null || parameterized.getTypeArgs().length != 1) {
            getLogger().die("RestAction must specify a result type argument.");
        } else {
            resultType = parameterized.getTypeArgs()[0];
        }

        return resultType;
    }

    private JClassType parseRawResultType() throws UnableToCompleteException {
        JClassType resultType;

        if (resultPrimitive != null) {
            resultType = getGeneratorUtil().convertPrimitiveToBoxed(resultPrimitive);
        } else {
            resultType = getReturnType().isClassOrInterface();
        }

        return resultType;
    }

    private void retrieveHttpMethod() throws UnableToCompleteException {
        Boolean moreThanOneAnnotation = false;

        for (HttpVerb verb : HttpVerb.values()) {
            if (actionMethod.isAnnotationPresent(verb.getAnnotationClass())) {
                moreThanOneAnnotation = moreThanOneAnnotation || httpMethod != null;
                httpMethod = verb.getVerb();
            }
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
            HttpParamValueResolver<T> annotationValueResolver, List<AnnotatedMethodParameter> destination)
            throws UnableToCompleteException {
        List<Class<? extends Annotation>> restrictedAnnotations = getRestrictedAnnotations(annotationClass);

        for (JParameter parameter : parameters) {
            T parameterAnnotation = parameter.getAnnotation(annotationClass);

            if (parameterAnnotation != null) {
                if (hasAnnotationFrom(parameter, restrictedAnnotations)) {
                    getLogger().die(String.format(MANY_REST_ANNOTATIONS, actionMethod.getName(), parameter.getName()));
                }
                if (parameter.isAnnotationPresent(DateFormat.class) && !isDate(parameter)) {
                    getLogger().die(String.format(DATE_FORMAT_NOT_DATE, actionMethod.getName(), parameter.getName()));
                }

                String value = annotationValueResolver.resolve(parameterAnnotation);
                destination.add(new AnnotatedMethodParameter(parameter, value));
            }
        }
    }

    private boolean isDate(JParameter parameter) {
        return Date.class.getCanonicalName().equals(parameter.getType().getQualifiedSourceName());
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
            getLogger().die(String.format(MANY_POTENTIAL_BODY, getMethodName()));
        }

        if (!formParams.isEmpty()) {
            getLogger().die(String.format(FORM_AND_BODY_PARAM, getMethodName()));
        }

        bodyParam = potentialBodyParams.get(0);
    }

    private ActionBinding createActionBinding(String implName) throws UnableToCompleteException {
        String resultClassName = resultClass.getParameterizedQualifiedSourceName();

        ActionBinding binding =
                new ActionBinding(path, getPackage(), implName, getMethodName(), resultClassName, parameters);
        binding.setSecured(secured);
        binding.setRestAction(returnsAction);
        binding.setResultPrimitive(resultPrimitive);

        return binding;
    }

    private JType getReturnType() {
        return actionMethod.getReturnType();
    }

    private String getMethodName() {
        return actionMethod.getName();
    }
}
