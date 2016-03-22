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

package com.gwtplatform.dispatch.rest.shared;

import java.util.HashMap;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class ContentTypeTest {
    @Test
    public void valueOf_wildcard() {
        // when
        ContentType contentType = ContentType.valueOf("*");

        // then
        assertThat(contentType.getType()).isEqualTo("*");
        assertThat(contentType.getSubType()).isEqualTo("*");
        assertThat(contentType.getParameters()).isEmpty();
    }

    @Test
    public void valueOf_type_wildcard() {
        // when
        ContentType contentType = ContentType.valueOf("text/*");

        // then
        assertThat(contentType.getType()).isEqualTo("text");
        assertThat(contentType.getSubType()).isEqualTo("*");
        assertThat(contentType.getParameters()).isEmpty();
    }

    @Test
    public void valueOf_type_type() {
        // when
        ContentType contentType = ContentType.valueOf("text/html");

        // then
        assertThat(contentType.getType()).isEqualTo("text");
        assertThat(contentType.getSubType()).isEqualTo("html");
        assertThat(contentType.getParameters()).isEmpty();
    }

    @Test
    public void valueOf_parameters() {
        // when
        ContentType contentType = ContentType.valueOf("*; encoding=UTF-8; q=0.5; secure");

        // then
        assertThat(contentType.getType()).isEqualTo("*");
        assertThat(contentType.getSubType()).isEqualTo("*");
        assertThat(contentType.getParameters()).containsOnly(
                entry("encoding", "UTF-8"),
                entry("q", "0.5")
        );
    }

    @Test
    public void toString_wildcard() {
        // given
        ContentType contentType = new ContentType("*", "*", new HashMap<>());

        // when
        String string = contentType.toString();

        // then
        assertThat(string).isEqualTo("*/*");
    }

    @Test
    public void toString_type_wildcard() {
        // given
        ContentType contentType = new ContentType("text", "*", new HashMap<>());

        // when
        String string = contentType.toString();

        // then
        assertThat(string).isEqualTo("text/*");
    }

    @Test
    public void toString_type_type() {
        // given
        ContentType contentType = new ContentType("text", "html", new HashMap<>());

        // when
        String string = contentType.toString();

        // then
        assertThat(string).isEqualTo("text/html");
    }

    @Test
    public void toString_parameters() {
        // given
        ContentType contentType = new ContentType("text", "html", ImmutableMap.<String, String>builder()
                .put("q", "0.8")
                .put("encoding", "UTF-8")
                .build());

        // when
        String string = contentType.toString();

        // then
        assertThat(string).isEqualTo("text/html; q=0.8; encoding=UTF-8");
    }

    @Test
    public void isCompatible_same() {
        // given
        ContentType contentType1 = new ContentType("text", "html", new HashMap<>());
        ContentType contentType2 = new ContentType("text", "html", new HashMap<>());

        // when
        boolean compatible = contentType1.isCompatible(contentType2);

        // then
        assertThat(compatible).isTrue();
    }

    @Test
    public void isCompatible_differentType() {
        // given
        ContentType contentType1 = new ContentType("text", "*", new HashMap<>());
        ContentType contentType2 = new ContentType("application", "*", new HashMap<>());

        // when
        boolean compatible = contentType1.isCompatible(contentType2);

        // then
        assertThat(compatible).isFalse();
    }

    @Test
    public void isCompatible_sameType_differentSubtype() {
        // given
        ContentType contentType1 = new ContentType("text", "css", new HashMap<>());
        ContentType contentType2 = new ContentType("text", "html", new HashMap<>());

        // when
        boolean compatible = contentType1.isCompatible(contentType2);

        // then
        assertThat(compatible).isFalse();
    }

    @Test
    public void isCompatible_wildCardType1() {
        // given
        ContentType contentType1 = new ContentType("*", "*", new HashMap<>());
        ContentType contentType2 = new ContentType("application", "*", new HashMap<>());

        // when
        boolean compatible = contentType1.isCompatible(contentType2);

        // then
        assertThat(compatible).isTrue();
    }

    @Test
    public void isCompatible_wildCardType2() {
        // given
        ContentType contentType1 = new ContentType("text", "*", new HashMap<>());
        ContentType contentType2 = new ContentType("*", "*", new HashMap<>());

        // when
        boolean compatible = contentType1.isCompatible(contentType2);

        // then
        assertThat(compatible).isTrue();
    }

    @Test
    public void isCompatible_sameType_wildcardSubType1() {
        // given
        ContentType contentType1 = new ContentType("text", "*", new HashMap<>());
        ContentType contentType2 = new ContentType("text", "html", new HashMap<>());

        // when
        boolean compatible = contentType1.isCompatible(contentType2);

        // then
        assertThat(compatible).isTrue();
    }

    @Test
    public void isCompatible_sameType_wildcardSubType2() {
        // given
        ContentType contentType1 = new ContentType("text", "html", new HashMap<>());
        ContentType contentType2 = new ContentType("text", "*", new HashMap<>());

        // when
        boolean compatible = contentType1.isCompatible(contentType2);

        // then
        assertThat(compatible).isTrue();
    }

    @Test
    public void isCompatible_null() {
        // given
        ContentType contentType1 = new ContentType("text", "html", new HashMap<>());
        ContentType contentType2 = null;

        // when
        boolean compatible = contentType1.isCompatible(contentType2);

        // then
        assertThat(compatible).isFalse();
    }
}
