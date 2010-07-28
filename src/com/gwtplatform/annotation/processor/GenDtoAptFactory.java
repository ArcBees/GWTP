/**
 * Copyright 2010 Gwt-Platform
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gwtplatform.annotation.processor;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import com.gwtplatform.annotation.GenDto;
import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;

public class GenDtoAptFactory implements AnnotationProcessorFactory {

	public GenDtoAptFactory() {

	}

	@Override
	public AnnotationProcessor getProcessorFor(
			Set<AnnotationTypeDeclaration> atds,
			AnnotationProcessorEnvironment env) {

		return new GenDtoAptProcessor(env);
	}

	@Override
	public Collection<String> supportedAnnotationTypes() {
		Collection<String> rslt = new ArrayList<String>();
		rslt.add(GenDto.class.getName());
		return rslt;
	}

	@Override
	public Collection<String> supportedOptions() {
		return new ArrayList<String>();
	}

}
