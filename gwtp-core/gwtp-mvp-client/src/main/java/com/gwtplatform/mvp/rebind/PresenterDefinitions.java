/*
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

package com.gwtplatform.mvp.rebind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.ext.typeinfo.JClassType;

/**
 * This class will hold a reference to all type of presenters that can be found in an application to be able to
 * retrieve them by type afterward.
 */
public class PresenterDefinitions {
    private final List<JClassType> standardPresenters;
    private final List<JClassType> codeSplitPresenters;
    private final Map<String, List<JClassType>> codeSplitBundlePresenters;
    private final Set<JClassType> gatekeepers;

    public PresenterDefinitions() {
        this.standardPresenters = new ArrayList<JClassType>();
        this.codeSplitPresenters = new ArrayList<JClassType>();
        this.codeSplitBundlePresenters = new HashMap<String, List<JClassType>>();
        this.gatekeepers = new HashSet<JClassType>();
    }

    public List<JClassType> getStandardPresenters() {
        return standardPresenters;
    }

    public List<JClassType> getCodeSplitPresenters() {
        return codeSplitPresenters;
    }

    public Map<String, List<JClassType>> getCodeSplitBundlePresenters() {
        return codeSplitBundlePresenters;
    }

    public Set<JClassType> getGatekeepers() {
        return gatekeepers;
    }

    public void addStandardPresenter(JClassType presenter) {
        standardPresenters.add(presenter);
    }

    public void addCodeSplitPresenter(JClassType presenter) {
        codeSplitPresenters.add(presenter);
    }

    public void addCodeSplitBundlePresenter(String bundle, JClassType presenter) {
        List<JClassType> presenters = codeSplitBundlePresenters.get(bundle);
        if (presenters == null) {
            presenters = new ArrayList<JClassType>();
            codeSplitBundlePresenters.put(bundle, presenters);
        }
        presenters.add(presenter);
    }

    public void addGatekeeper(JClassType presenter) {
        gatekeepers.add(presenter);
    }
}
