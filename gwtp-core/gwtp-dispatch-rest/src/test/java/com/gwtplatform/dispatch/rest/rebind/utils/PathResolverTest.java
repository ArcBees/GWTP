/*
 * Copyright 2015 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.rebind.utils;

import java.util.Map;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PathResolverTest {

    @Test
    public void regexInformationShouldNotBePartOfPath() {
        // When
        String resolvedPath = PathResolver.resolvePath("{id: [0-9]*}/subpath/{sid:[0-9]{1,9}}");

        // Then
        assertThat(resolvedPath).isEqualTo("{id}/subpath/{sid}");
    }

    @Test
    public void pathParameterWithoutRegexShouldNotBeChanged() {
        // When
        String resolvedPath = PathResolver.resolvePath("{id: [0-9]*}/subpath/{sid}");

        // Then
        assertThat(resolvedPath).isEqualTo("{id}/subpath/{sid}");
    }

    @Test
    public void regexInformationCanBeRetrievedForParameter() {
        // When
        Map<String, String> regex = PathResolver.extractPathParameterRegex("{id: [0-9]*}/subpath");

        // Then
        assertThat(regex.get("id")).isEqualTo("[0-9]*");
    }

    @Test
    public void multipleRegexInformationCanBeRetrievedForParameter() {
        // When
        String idRegex = "[0-9]*";
        String sidRegex = "[0-9]{1,9}";
        String imeiRegex = "[0-9\\:\\/]{15}";
        StringBuilder paramBuilder = new StringBuilder();
        paramBuilder.append("{id: ").append(idRegex).append("}");
        paramBuilder.append("/subpath");
        paramBuilder.append("/{sid: ").append(sidRegex).append("}");
        paramBuilder.append("/mobile");
        paramBuilder.append("/{imei: ").append(imeiRegex).append("}");
        Map<String, String> regex = PathResolver.extractPathParameterRegex(paramBuilder.toString());

        // Then
        assertThat(regex.get("id")).isEqualTo(idRegex);
        assertThat(regex.get("sid")).isEqualTo(sidRegex);
        assertThat(regex.get("imei")).isEqualTo(imeiRegex);
        assertThat(regex.get("subpath")).isNull();
        assertThat(regex.get("mobile")).isNull();
    }

    @Test
    public void regexWithEscapedAngleBracketsCanBeResolved() {
        // When
        String regex = "[a-zA-Z0-9\\{](-[a-zA-Z0-9])-[a-zA-Z0-9]{12}";
        Map<String, String> regexMap = PathResolver.extractPathParameterRegex(
                "/{id:[a-zA-Z0-9\\{](-[a-zA-Z0-9])-[a-zA-Z0-9]{12}}");

        // Then
        assertThat(regexMap.get("id")).isEqualTo(regex);
    }

    @Test
    public void multipleAndMoreComplexRegexInformationCanBeRetrievedForParameter() {
        // When
        String nameRegex = "[0-9]*";
        String idRegex = "[a-zA-Z0-9\\{](-[a-zA-Z0-9])-[a-zA-Z0-9]{12}";
        String id2Regex = "[a-zA-Z0-9\\{](-[a-zA-Z0-9\\}])-[a-zA-Z0-9]{15}";
        String emailRegex =
                "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\""
                        + "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\["
                        + "\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+"
                        + "[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}"
                        + "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]"
                        + ":(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]"
                        + "|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

        StringBuilder paramBuilder = new StringBuilder();
        paramBuilder.append("/{name: ").append(nameRegex).append("}");
        paramBuilder.append("/x");
        paramBuilder.append("/{id: ").append(idRegex).append("}");
        paramBuilder.append("/{id2: ").append(id2Regex).append("}");
        paramBuilder.append("/{email:").append(emailRegex).append("}");

        Map<String, String> regex = PathResolver.extractPathParameterRegex(paramBuilder.toString());

        // Then
        assertThat(regex.get("name")).isEqualTo(nameRegex);
        assertThat(regex.get("id")).isEqualTo(idRegex);
        assertThat(regex.get("id2")).isEqualTo(id2Regex);
        assertThat(regex.get("email")).isEqualTo(emailRegex);
    }

    @Test
    public void nullShouldBeReturnedIfNoRegexIsDefined() {
        // When
        Map<String, String> regex = PathResolver.extractPathParameterRegex("{id}/subpath/{sid}");

        // Then
        assertThat(regex.get("id")).isNull();
        assertThat(regex.get("sid")).isNull();
    }

    @Test
    public void nullShouldBeReturnedIfParameterIsNotDefined() {
        // When
        Map<String, String> regex = PathResolver.extractPathParameterRegex("{id}/subpath/{sid}");

        // Then
        assertThat(regex.get("subpath")).isNull();
    }
}
