/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.matteobertozzi.rednaco.collections;

public class IntegerValue {
  private int value;

  public IntegerValue() {
    this(0);
  }

  public IntegerValue(final int initValue) {
    this.value = initValue;
  }

  public int set(final int newValue) {
    final int oldValue = this.value;
    this.value = newValue;
    return oldValue;
  }

  public int get() {
    return value;
  }

  public int incrementAndGet() {
    return ++value;
  }

  public int getAndIncrement() {
    return value++;
  }

  public int add(final int amount) {
    value += amount;
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
