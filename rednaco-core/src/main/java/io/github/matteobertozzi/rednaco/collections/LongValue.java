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

public class LongValue {
  private long value;

  public LongValue() {
    this(0);
  }

  public LongValue(final int initValue) {
    this.value = initValue;
  }

  public long set(final long newValue) {
    final long oldValue = this.value;
    this.value = newValue;
    return oldValue;
  }

  public long get() {
    return value;
  }

  public int intValue() {
    return Math.toIntExact(value);
  }

  public long incrementAndGet() {
    return ++value;
  }

  public long getAndIncrement() {
    return value++;
  }

  public long add(final long amount) {
    value += amount;
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
