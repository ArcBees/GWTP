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
import com.gwtplatform.samples.hplace.shared.GetProductListAction;
import com.gwtplatform.samples.hplace.shared.GetProductListResult;
import com.gwtplatform.samples.hplace.shared.Product;

import com.google.inject.Inject;

import java.util.ArrayList;

/**
 * @author Philippe Beaudoin
 */
public class GetProductListHandler implements
    ActionHandler<GetProductListAction, GetProductListResult> {

  private final ProductDatabase database;

  @Inject
  public GetProductListHandler(ProductDatabase database) {
    this.database = database;
  }

  @Override
  public GetProductListResult execute(final GetProductListAction action,
      final ExecutionContext context) throws ActionException {
    ArrayList<Product> products = database.getMatching(action.getFlags());
    return new GetProductListResult(products);
  }

  @Override
  public Class<GetProductListAction> getActionType() {
    return GetProductListAction.class;
  }

  @Override
  public void undo(final GetProductListAction action,
      final GetProductListResult result, final ExecutionContext context)
      throws ActionException {
    // No undo
  }
}
