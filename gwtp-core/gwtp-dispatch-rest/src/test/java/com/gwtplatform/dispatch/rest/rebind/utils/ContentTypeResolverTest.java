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

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.junit.Test;

import com.google.common.collect.Sets;
import com.google.gwt.core.ext.typeinfo.HasAnnotations;
import com.gwtplatform.dispatch.rest.shared.ContentType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class ContentTypeResolverTest {
    @Test
    public void resolveConsumes_missing_defaultsToWildCard() {
        // given
        HasAnnotations hasAnnotations = mock(HasAnnotations.class);

        // when
        Set<ContentType> contentTypes = ContentTypeResolver.resolveConsumes(hasAnnotations);

        // then
        assertThat(contentTypes).containsOnly(ContentType.valueOf(MediaType.WILDCARD));
    }

    @Test
    public void resolveConsumes_missing_defaultsToSpecifiedFallback() {
        // given
        HasAnnotations hasAnnotations = mock(HasAnnotations.class);
        Set<ContentType> fallbacks = Sets.newHashSet(
                ContentType.valueOf("text/html"),
                ContentType.valueOf("application/json")
        );

        // when
        Set<ContentType> contentTypes = ContentTypeResolver.resolveConsumes(hasAnnotations, fallbacks);

        // then
        assertThat(contentTypes).containsExactlyElementsOf(fallbacks);
    }

    @Test
    public void resolveProduces_emptyValue_defaultsToWildcard() {
        // given
        HasAnnotations hasAnnotations = mock(HasAnnotations.class);
        Produces produces = mock(Produces.class);

        given(hasAnnotations.getAnnotation(Produces.class)).willReturn(produces);
        given(produces.value()).willReturn(new String[]{});

        // when
        Set<ContentType> contentTypes = ContentTypeResolver.resolveProduces(hasAnnotations);

        // then
        assertThat(contentTypes).containsOnly(ContentType.valueOf(MediaType.WILDCARD));
    }

    @Test
    public void resolveConsumes_multipleValues() {
        // given
        HasAnnotations hasAnnotations = mock(HasAnnotations.class);
        Consumes consumes = mock(Consumes.class);
        String type1 = "text/*";
        String type2 = "application/*";

        given(hasAnnotations.getAnnotation(Consumes.class)).willReturn(consumes);
        given(consumes.value()).willReturn(new String[]{type1, type2});

        // when
        Set<ContentType> contentTypes = ContentTypeResolver.resolveConsumes(hasAnnotations);

        // then
        assertThat(contentTypes).containsOnly(ContentType.valueOf(type1), ContentType.valueOf(type2));
    }

    @Test
    public void resolveConsumes_multipleValuesWithinMultipleValues() {
        // given
        HasAnnotations hasAnnotations = mock(HasAnnotations.class);
        Consumes consumes = mock(Consumes.class);
        String type1 = "text/*;q=0.1";
        String type2 = "application/*;q=0.6";
        String type3 = "audio/*;q=0.8";

        given(hasAnnotations.getAnnotation(Consumes.class)).willReturn(consumes);
        given(consumes.value()).willReturn(new String[]{type1, type2 + "," + type3});

        // when
        Set<ContentType> contentTypes = ContentTypeResolver.resolveConsumes(hasAnnotations);

        // then
        assertThat(contentTypes).containsOnly(
                ContentType.valueOf(type1),
                ContentType.valueOf(type2),
                ContentType.valueOf(type3)
        );
    }
}
