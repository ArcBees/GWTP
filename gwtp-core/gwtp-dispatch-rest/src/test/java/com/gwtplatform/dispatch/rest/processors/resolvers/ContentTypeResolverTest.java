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

package com.gwtplatform.dispatch.rest.processors.resolvers;

import java.util.Set;

import javax.lang.model.element.Element;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.junit.Test;

import com.google.common.collect.Sets;
import com.gwtplatform.dispatch.rest.processors.AnnotatedElementBuilder;
import com.gwtplatform.dispatch.rest.shared.ContentType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static com.gwtplatform.dispatch.rest.processors.AnnotatedElementBuilder.stubElementWithoutAnnotations;

public class ContentTypeResolverTest {
    @Test
    public void resolveConsumes_missing_defaultsToWildCard() {
        // given
        Element element = stubElementWithoutAnnotations();

        // when
        Set<ContentType> contentTypes = ContentTypeResolver.resolveConsumes(element);

        // then
        assertThat(contentTypes).containsOnly(ContentType.valueOf(MediaType.WILDCARD));
    }

    @Test
    public void resolveConsumes_missing_defaultsToSpecifiedFallback() {
        // given
        Element element = stubElementWithoutAnnotations();

        Set<ContentType> fallbacks = Sets.newHashSet(
                ContentType.valueOf("text/html"),
                ContentType.valueOf("application/json")
        );

        // when
        Set<ContentType> contentTypes = ContentTypeResolver.resolveConsumes(element, fallbacks);

        // then
        assertThat(contentTypes).containsExactlyElementsOf(fallbacks);
    }

    @Test
    public void resolveProduces_emptyValue_defaultsToWildcard() {
        // given
        Produces produces = mock(Produces.class);
        given(produces.value()).willReturn(new String[]{});

        Element element = new AnnotatedElementBuilder(produces).getElement();

        // when
        Set<ContentType> contentTypes = ContentTypeResolver.resolveProduces(element);

        // then
        assertThat(contentTypes).containsOnly(ContentType.valueOf(MediaType.WILDCARD));
    }

    @Test
    public void resolveConsumes_multipleValues() {
        // given
        String type1 = "text/*";
        String type2 = "application/*";

        Consumes consumes = mock(Consumes.class);
        given(consumes.value()).willReturn(new String[]{type1, type2});

        Element element = new AnnotatedElementBuilder(consumes).getElement();

        // when
        Set<ContentType> contentTypes = ContentTypeResolver.resolveConsumes(element);

        // then
        assertThat(contentTypes).containsOnly(ContentType.valueOf(type1), ContentType.valueOf(type2));
    }

    @Test
    public void resolveConsumes_multipleValuesWithinMultipleValues() {
        // given
        String type1 = "text/*;q=0.1";
        String type2 = "application/*;q=0.6";
        String type3 = "audio/*;q=0.8";

        Consumes consumes = mock(Consumes.class);
        given(consumes.value()).willReturn(new String[]{type1, type2 + "," + type3});

        Element element = new AnnotatedElementBuilder(consumes).getElement();

        // when
        Set<ContentType> contentTypes = ContentTypeResolver.resolveConsumes(element);

        // then
        assertThat(contentTypes).containsOnly(
                ContentType.valueOf(type1),
                ContentType.valueOf(type2),
                ContentType.valueOf(type3)
        );
    }
}
