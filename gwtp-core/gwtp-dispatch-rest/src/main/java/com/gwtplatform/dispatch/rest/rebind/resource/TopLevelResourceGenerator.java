/*
 * Copyright 2014 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.rebind.resource;

import java.io.PrintWriter;
import java.util.Set;

import javax.ws.rs.Path;

import org.apache.velocity.app.VelocityEngine;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.gwtplatform.dispatch.rest.rebind.events.RegisterGinBindingEvent;
import com.gwtplatform.dispatch.rest.rebind.utils.ClassDefinition;
import com.gwtplatform.dispatch.rest.rebind.utils.ContentTypeResolver;
import com.gwtplatform.dispatch.rest.rebind.utils.EventBus;
import com.gwtplatform.dispatch.rest.rebind.utils.Logger;
import com.gwtplatform.dispatch.rest.rebind.utils.PathResolver;
import com.gwtplatform.dispatch.rest.shared.ContentType;
import com.gwtplatform.dispatch.rest.shared.NoXsrfHeader;

import jakarta.inject.Inject;

public class TopLevelResourceGenerator extends AbstractResourceGenerator {
    private static final String TEMPLATE = "com/gwtplatform/dispatch/rest/rebind/resource/Resource.vm";

    private final EventBus eventBus;

    private ResourceDefinition resourceDefinition;

    @Inject
    TopLevelResourceGenerator(
            Logger logger,
            GeneratorContext context,
            EventBus eventBus,
            VelocityEngine velocityEngine,
            Set<MethodGenerator> methodGenerators) {
        super(logger, context, velocityEngine, methodGenerators);

        this.eventBus = eventBus;
    }

    @Override
    public boolean canGenerate(ResourceContext context) {
        setContext(context);

        return getResourceType().isInterface() != null
                && getResourceType().isAnnotationPresent(Path.class)
                && super.canGenerate(context);
    }

    @Override
    public ResourceDefinition generate(ResourceContext context) throws UnableToCompleteException {
        resourceDefinition = null;

        return super.generate(context);
    }

    @Override
    protected String getTemplate() {
        return TEMPLATE;
    }

    @Override
    protected String getImplName() {
        return getResourceType().getSimpleSourceName() + IMPL;
    }

    @Override
    protected ResourceDefinition getResourceDefinition() {
        if (resourceDefinition == null) {
            String path = resolvePath();
            boolean secured = resolveSecured();
            Set<ContentType> consumes = resolveConsumes();
            Set<ContentType> produces = resolveProduces();

            resourceDefinition = new ResourceDefinition(getResourceType(), getPackageName(), getImplName(), path,
                    secured, consumes, produces);
        }

        return resourceDefinition;
    }

    @Override
    protected void commit(PrintWriter printWriter) {
        super.commit(printWriter);

        registerResourceBinding();
    }

    private String resolvePath() {
        return PathResolver.resolve(getResourceType());
    }

    private boolean resolveSecured() {
        return !getResourceType().isAnnotationPresent(NoXsrfHeader.class);
    }

    protected Set<ContentType> resolveConsumes() {
        return ContentTypeResolver.resolveConsumes(getResourceType());
    }

    protected Set<ContentType> resolveProduces() {
        return ContentTypeResolver.resolveProduces(getResourceType());
    }

    private void registerResourceBinding() {
        RegisterGinBindingEvent.postSingleton(eventBus, new ClassDefinition(getResourceType()), getClassDefinition());
    }
}
