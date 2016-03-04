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

package com.gwtplatform.mvp.client.proxy;

/**
 * A {@link Gatekeeper} that allows all requests. This is used as a default gatekeeper if no classes sport the {@link
 * com.gwtplatform.mvp.client.annotations.DefaultGatekeeper @DefaultGatekeeper} annotation.
 */
public class AlwaysTrueGatekeeper implements Gatekeeper {
    @Override
    public boolean canReveal() {
        return true;
    }
}
