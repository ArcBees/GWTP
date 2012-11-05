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

/**
 * For testing purposes only.
 *
 * @author Florian Sauter
 */
@GenProxy(
    targetPackage = "com.gwtplatform.dispatch.annotation.proxy",
    filterSetter = { "id" },
    filterGetter = { "version" },
    locator = EmployeeLocator.class
)
public class Employee {
  private String displayName;
  private Long supervisorKey;
  private Long id;
  private Integer version;

  @UseProxyName("com.gwtplatform.dispatch.annotation.proxy.EmployeeProxy")
  private Employee supervisor;

  public Employee() {
  }

  public String getDisplayName() {
    return displayName;
  }

  public Long getSupervisorKey() {
    return supervisorKey;
  }

  public void setSupervisorKey(Long supervisorKey) {
    this.supervisorKey = supervisorKey;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public Employee getSupervisor() {
    return supervisor;
  }

  public void setSupervisor(Employee supervisor) {
    this.supervisor = supervisor;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
}
