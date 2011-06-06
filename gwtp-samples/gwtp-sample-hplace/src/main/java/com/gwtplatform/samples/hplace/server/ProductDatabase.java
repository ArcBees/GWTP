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

import com.gwtplatform.samples.hplace.shared.Product;

import com.google.inject.Singleton;

import java.util.ArrayList;

/**
 * @author Philippe Beaudoin
 */
@Singleton
public class ProductDatabase {

  private Product[] products = {
      new Product("jPad Wifi 16GB", Product.FLAG_SPECIAL, "$529.00", 13),
      new Product("jPad Wifi 32GB", 0, "$649.00", 77),
      new Product("jPad Wifi 64GB", Product.FLAG_FAVORITE
          | Product.FLAG_SPECIAL, "$719.00", 5),
      new Product("jPad 3G 16GB", 0, "$679.00", 85),
      new Product("jPad 3G 32GB", Product.FLAG_FAVORITE, "$779.00", 123),
      new Product("jPad 3G 64GB", Product.FLAG_FAVORITE | Product.FLAG_SPECIAL,
          "$859.00", 0),
      new Product("jPhone 3G", Product.FLAG_SPECIAL, "$79.95", 823),
      new Product("jPhone 3GS", Product.FLAG_FAVORITE, "$199.95", 1),
      new Product("Nexus Two", 0, "$529.95", 221)};

  public Product get(int id) {
    for (Product product : products) {
      if (product.getId() == id) {
        return product;
      }
    }
    return null;
  }

  public ArrayList<Product> getMatching(int flags) {
    ArrayList<Product> result = new ArrayList<Product>();
    for (Product product : products) {
      if (product.flagsSet(flags)) {
        result.add(product);
      }
    }
    return result;
  }
}
