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

import com.google.inject.Injector;

import org.junit.internal.runners.model.MultipleFailureException;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Statement} invoking a list of methods with parameters by filling-in 
 * these parameters with injected instances. The methods are called after the
 * provided {@code prev} {@link Statement}.
 * 
 * @author Philippe Beaudoin
 */
public class InjectedAfterStatements extends Statement {
  
  private final Statement prev;

  private final List<Statement> afters;

  public InjectedAfterStatements(Statement prev, List<FrameworkMethod> afters, 
      Object target, Injector injector) {
    this.prev = prev;
    this.afters = new ArrayList<Statement>(afters.size());
    for (FrameworkMethod method : afters) {
      this.afters.add(new InjectedStatement(method, target, injector));
    }
  }
  
  @Override
  public void evaluate() throws Throwable {
    List<Throwable> errors = new ArrayList<Throwable>();
    errors.clear();
    try {
      prev.evaluate();
    } catch (Throwable e) {
      errors.add(e);
    } finally {
      for (Statement after : afters) {
        try {
          after.evaluate();
        } catch (Throwable e) {
          errors.add(e);
        }
      }
    }
    if (!errors.isEmpty()) {
      throw new MultipleFailureException(errors);
    }
  }
}
