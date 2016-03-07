/*
 * Copyright 2016 ArcBees Inc.
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

package com.gwtplatform.mvp.client.gin;

import com.google.gwt.inject.client.AbstractGinModule;

/**
 * Empty module used as a placeholder in form factor configuration properties. If those properties are empty (this is
 * the case when form factors are NOT used), GIN generators will throw an exception.
 */
class EmptyModule extends AbstractGinModule {
    @Override
    protected void configure() {
    }
}
