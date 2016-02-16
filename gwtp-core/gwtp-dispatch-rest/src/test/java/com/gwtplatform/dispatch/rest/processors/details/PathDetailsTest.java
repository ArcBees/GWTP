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

package com.gwtplatform.dispatch.rest.processors.details;

import javax.lang.model.element.Element;
import javax.ws.rs.Path;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class PathDetailsTest {
    @Test
    public void regexInformationShouldNotBePartOfPath() {
        Element element = buildElement("{id: [0-9]*}/subpath/{sid:[0-9]{1,9}}");

        PathDetails pathDetails = new PathDetails(element);

        assertThat(pathDetails.getValue()).isEqualTo("/{id}/subpath/{sid}");
    }

    private Element buildElement(String value) {
        Path path = mock(Path.class);
        Element element = mock(Element.class);

        given(element.getAnnotation(Path.class)).willReturn(path);
        given(path.value()).willReturn(value);
        return element;
    }

    @Test
    public void pathParameterWithoutRegexShouldNotBeChanged() {
        Element element = buildElement("{id: [0-9]*}/subpath/{sid}");

        PathDetails pathDetails = new PathDetails(element);

        assertThat(pathDetails.getValue()).isEqualTo("/{id}/subpath/{sid}");
    }

    @Test
    public void regexInformationCanBeRetrievedForParameter() {
        Element element = buildElement("{id: [0-9]*}/subpath");

        PathDetails pathDetails = new PathDetails(element);

        assertThat(pathDetails.getRegex("id")).isEqualTo("[0-9]*");
    }

    @Test
    public void multipleRegexInformationCanBeRetrievedForParameter() {
        String idRegex = "[0-9]*";
        String sidRegex = "[0-9]{1,9}";
        String imeiRegex = "[0-9\\:\\/]{15}";

        String pathBuilder = "{id: " + idRegex + "}" +
                "/subpath" +
                "/{sid: " + sidRegex + "}" +
                "/mobile" +
                "/{imei: " + imeiRegex + "}";

        Element element = buildElement(pathBuilder);

        PathDetails pathDetails = new PathDetails(element);

        assertThat(pathDetails.getRegex("id")).isEqualTo(idRegex);
        assertThat(pathDetails.getRegex("sid")).isEqualTo(sidRegex);
        assertThat(pathDetails.getRegex("imei")).isEqualTo(imeiRegex);
        assertThat(pathDetails.getRegex("subpath")).isNull();
        assertThat(pathDetails.getRegex("mobile")).isNull();
    }

    @Test
    public void regexWithEscapedAngleBracketsCanBeResolved() {
        String regex = "[a-zA-Z0-9\\\\{](-[a-zA-Z0-9])-[a-zA-Z0-9]{12}";
        Element element = buildElement("/{id:" + regex + "}");

        PathDetails pathDetails = new PathDetails(element);

        assertThat(pathDetails.getRegex("id")).isEqualTo(regex);
    }

    @Test
    public void multipleAndMoreComplexRegexInformationCanBeRetrievedForParameter() {
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

        String path = "/{name: " + nameRegex + "}" +
                "/x" +
                "/{id: " + idRegex + "}" +
                "/{id2: " + id2Regex + "}" +
                "/{email:" + emailRegex + "}";

        Element element = buildElement(path);

        PathDetails pathDetails = new PathDetails(element);

        assertThat(pathDetails.getRegex("name")).isEqualTo(nameRegex);
        assertThat(pathDetails.getRegex("id")).isEqualTo(idRegex);
        assertThat(pathDetails.getRegex("id2")).isEqualTo(id2Regex);
        assertThat(pathDetails.getRegex("email")).isEqualTo(emailRegex);
    }

    @Test
    public void nullShouldBeReturnedIfNoRegexIsDefined() {
        Element element = buildElement("{id}/subpath/{sid}");

        PathDetails pathDetails = new PathDetails(element);

        assertThat(pathDetails.getRegex("id")).isNull();
        assertThat(pathDetails.getRegex("sid")).isNull();
    }

    @Test
    public void nullShouldBeReturnedIfParameterIsNotDefined() {
        Element element = buildElement("{id}/subpath/{sid}");

        PathDetails pathDetails = new PathDetails(element);

        assertThat(pathDetails.getRegex("subpath")).isNull();
    }
}
