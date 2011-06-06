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

package com.gwtplatform.samples.hplace.server;

import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.gwtplatform.samples.hplace.shared.GetProductAction;
import com.gwtplatform.samples.hplace.shared.GetProductResult;
import com.gwtplatform.samples.hplace.shared.Product;

import com.google.inject.Inject;

/**
 * @author Philippe Beaudoin
 */
public class GetProductHandler implements
    ActionHandler<GetProductAction, GetProductResult> {

  private final ProductDatabase database;

  @Inject
  public GetProductHandler(ProductDatabase database) {
    this.database = database;
  }

  @Override
  public GetProductResult execute(final GetProductAction action,
      final ExecutionContext context) throws ActionException {
    Product product = database.get(action.getId());
    if (product == null) {
      throw new ActionException("Product not found");
    }
    return new GetProductResult(product);
  }

  @Override
  public Class<GetProductAction> getActionType() {
    return GetProductAction.class;
  }

  @Override
  public void undo(final GetProductAction action, final GetProductResult result,
      final ExecutionContext context) throws ActionException {
    // No undo
  }
}
