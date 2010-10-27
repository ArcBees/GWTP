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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test that the {@link AutomockingModule#forceMock} method works as expected.
 * 
 * @author Philippe Beaudoin
 */
@RunWith(GuiceMockitoJUnitRunner.class)
public class ForceMockTest {

  /**
   * Guice test module.
   */
  public static class Module extends AutomockingModule {
    @Override
    protected void configureTest() {
      forceMock(Base1.class);
      forceMock(Child21.class);
      forceMock(Child311.class);
    }
  }
  
  interface Base1 { int t1(); }
  static class Child11  implements Base1 { public int t1() { return 11; } }
  static class Child111 extends Child11  { public int t1() { return 111; } }
  static class Child12  implements Base1 { public int t1() { return 12; } }

  interface Base2 { int t2(); }
  static class Child21  implements Base2 { public int t2() { return 21; } }
  static class Child211 extends Child21 { public int t2() { return 211; } }
  static class Child22  implements Base2 { public int t2() { return 22; } }
  
  interface Base3 { int t3(); }
  static class Child31  implements Base3 { public int t3() { return 31; } }
  static class Child311 extends Child31 { public int t3() { return 311; } }
  
  @Test
  public void injectForceMock(
      Base1 base1,
      Child11 child11,
      Child111 child111,
      Child12 child12,
      Base2 base2,
      Child21 child21,
      Child211 child211,
      Child22 child22,
      Base3 base3,
      Child31 child31,
      Child311 child311) {
    verify(base1, never()).t1();
    verify(child11, never()).t1();
    verify(child111, never()).t1();
    verify(child12, never()).t1();

    verify(base2, never()).t2();
    verify(child21, never()).t2();
    verify(child211, never()).t2();
    assertEquals(22, child22.t2());
    
    verify(base3, never()).t3();
    assertEquals(31, child31.t3());
    verify(child311, never()).t3();
  }

}
