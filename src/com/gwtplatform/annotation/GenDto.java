/**
 * Copyright 2010 ArcBees Inc.
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

package com.gwtplatform.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to generate simple Data Transfer Object (DTO) classes.
 * <p/>
 * If you type:
 * 
 * <pre>
 * <code> 
 * {@literal}@GenDto
 * public class PersonName {
 *   String firstName;
 *   String lastName; 
 * }
 * </code>
 * </pre>
 * 
 * gwt-platform will generate a class call PersonNameDto.
 * <p/>
 * PersonNameDto will have fields, getters, and a constructor that 
 * takes firstName and lastName plus equals, hashCode, toString etc, 
 * <p/>
 * Notes:
 * <p/>
 * There is no naming requirement for your class name. 
 * It will be appended with Dto
 * 
 * <p/>
 * 
 * @author Brendan Doherty 
 * @author Stephen Haberman (concept)
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface GenDto {
}
