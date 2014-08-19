/**
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

package com.gwtplatform.common.rebind;

class DependencyImpl implements Dependency {
    private final String classFromDependency;
    private final String groupId;
    private final String artifactId;

    DependencyImpl(
            String classFromDependency,
            String groupId,
            String artifactId) {
        this.classFromDependency = classFromDependency;
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public String getArtifactId() {
        return artifactId;
    }

    @Override
    public String getVersion() {
        try {
            return Class.forName(classFromDependency).getPackage().getImplementationVersion();
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public boolean isPresent() {
        try {
            Class.forName(classFromDependency);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
