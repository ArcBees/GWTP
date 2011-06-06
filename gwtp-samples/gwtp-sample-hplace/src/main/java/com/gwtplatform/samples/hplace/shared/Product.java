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

package com.gwtplatform.samples.hplace.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Philippe Beaudoin
 */
public class Product implements IsSerializable {

  public static int FLAG_FAVORITE = 0x1;
  public static int FLAG_SPECIAL = 0x2;

  private static int nextId = 1;

  private int flags;
  private int id;
  private String name;
  private String price;
  private int quantity;

  public Product(String name, int flags, String price, int quantity) {
    this.id = nextId++;
    this.name = name;
    this.flags = flags;
    this.price = price;
    this.quantity = quantity;
  }

  Product() {
    // For serialization only
  }

  /**
   * Checks if all the passed flags are set. Passing 0 always returns
   * {@code true}.
   *
   * @param flags A bitwise combination of the flags
   * @return {@code true} if all the passed flags are set, {@code false}
   *         otherwise.
   */
  public boolean flagsSet(int flags) {
    return (this.flags & flags) == flags;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getPrice() {
    return price;
  }

  public int getQuantity() {
    return quantity;
  }

  public boolean isFavorite() {
    return (flags & FLAG_FAVORITE) != 0;
  }

  public boolean isSpecial() {
    return (flags & FLAG_SPECIAL) != 0;
  }
}
