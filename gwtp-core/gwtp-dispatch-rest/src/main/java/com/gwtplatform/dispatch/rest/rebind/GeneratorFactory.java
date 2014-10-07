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

import java.util.List;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.gwtplatform.dispatch.rest.rebind.type.ActionBinding;
import com.gwtplatform.dispatch.rest.rebind.type.ResourceBinding;
import com.gwtplatform.dispatch.rest.rebind.type.ServiceBinding;

public interface GeneratorFactory {
    ServiceGenerator createServiceGenerator(JClassType service);

    ResourceDelegateGenerator createResourceDelegateGenerator(ServiceBinding serviceBinding,
            List<ActionBinding> actionBindings, List<ServiceBinding> serviceBindings);

    ChildServiceGenerator createChildServiceGenerator(JMethod method, ServiceBinding parent);

    ActionGenerator createActionGenerator(JMethod actionMethod, ResourceBinding parent);
}
