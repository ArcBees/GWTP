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

package com.gwtplatform.tester.mockito;

import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test various general behaviors.
 * 
 * @author Philippe Beaudoin
 */
@RunWith(GuiceMockitoJUnitRunner.class)
public class GeneralTest {

  /**
   * Guice test module.
   */
  public static class Module extends AutomockingModule {
    @Override
    protected void configureTest() {
      bindConstant().annotatedWith(OneHundred.class).to(100);
      bindConstant().annotatedWith(Names.named("200")).to(200);
      bindConstant().annotatedWith(Names.named("VALUE1")).to(MyEnum.VALUE1);
      bindConstant().annotatedWith(Names.named("VALUE2")).to(MyEnum.VALUE2);
      bind(Key.get(TestClass.class, Value3.class)).toInstance(new TestClass(MyEnum.VALUE3));
      bind(Key.get(TestClass.class, Names.named("VALUE2"))).to(TestClass.class).in(TestScope.SINGLETON);
    }
  }
  
  static enum MyEnum {
    VALUE1,
    VALUE2,
    VALUE3
  }

  static class TestClass {
    private final MyEnum value;
    @Inject
    public TestClass(@Named("VALUE2") MyEnum value) {
      this.value = value;
    }
  }
  
  @Test
  public void testConstantInjection(
      @OneHundred Integer oneHundred,
      @Named("200") Integer twoHundred,
      @Named("VALUE1") MyEnum value1) {
    assertEquals(100, (int) oneHundred);
    assertEquals(200, (int) twoHundred);
    assertEquals(MyEnum.VALUE1, value1);
  }

  @Test
  public void testInjectBoundWithKeys(
      @Value3 TestClass testClassValue3,
      @Named("VALUE2") TestClass testClassValue2) {
    assertEquals(MyEnum.VALUE3, testClassValue3.value);
    assertEquals(MyEnum.VALUE2, testClassValue2.value);
  }
}
