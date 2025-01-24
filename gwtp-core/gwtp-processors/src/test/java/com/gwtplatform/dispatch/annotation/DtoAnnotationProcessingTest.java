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

package com.gwtplatform.dispatch.annotation;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * This test is being run by ant, but is not run in eclipse.
 * <p>
 * TODO: Make a test suite with a couple of permutations (with/without Order, Optional, both...).
 */
public class DtoAnnotationProcessingTest {

    @Test
    public void shouldGenerateDto() {
        PersonNameDto dto = new PersonNameDto("bob", "smith");
        assertEquals("bob", dto.getFirstName());
        assertEquals("smith", dto.getLastName());

        PersonNameDto dto2 = new PersonNameDto("bob", "smith");
        assertEquals(dto, dto2);

        PersonNameDto dto3 = new PersonNameDto("bobby", "smith");
        assertFalse(dto.equals(dto3));
    }

    @Test
    public void shouldGenerateDtoWithOptionalFieldsAndBuilder() {
        PersonNameDto dto = new PersonNameDto.Builder("bob", "andrews").secondName("peter").build();
        assertEquals("bob", dto.getFirstName());
        assertEquals("andrews", dto.getLastName());
        assertEquals("peter", dto.getSecondName());
    }
}
