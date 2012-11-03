/**
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

import com.google.web.bindery.requestfactory.shared.Locator;

/**
 * For testing purposes only.
 *
 * @author Florian Sauter
 */
public class EmployeeLocator extends Locator<Employee, Long> {

  @Override
  public Employee create(Class<? extends Employee> clazz) {
    return new Employee();
  }

  @Override
  public Employee find(Class<? extends Employee> clazz, Long id) {
    return null;
  }

  @Override
  public Class<Employee> getDomainType() {
    return null;
  }

  @Override
  public Long getId(Employee domainObject) {
    return null;
  }

  @Override
  public Class<Long> getIdType() {
    return null;
  }

  @Override
  public Object getVersion(Employee domainObject) {
    return null;
  }

}
