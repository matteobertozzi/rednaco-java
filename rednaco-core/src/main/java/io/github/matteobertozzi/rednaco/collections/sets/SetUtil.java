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

package io.github.matteobertozzi.rednaco.collections.sets;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public final class SetUtil {
  private SetUtil() {
    // no-op
  }

  // ================================================================================
  //  Length related
  // ================================================================================
  public static <K> int size(final Set<K> input) {
    return input != null ? input.size() : 0;
  }

  public static <K> boolean isEmpty(final Set<K> input) {
    return input == null || input.isEmpty();
  }

  public static <K> boolean isNotEmpty(final Set<K> input) {
    return input != null && !input.isEmpty();
  }

  // ================================================================================
  //  Contains
  // ================================================================================
  public static <K> boolean contains(final Set<K> input, final K key) {
    return input != null && input.contains(key);
  }

  // ================================================================================
  //  Instance related
  // ================================================================================
  public static <K> Set<K> emptyIfNull(final Set<K> input) {
    return input == null ? Set.of() : input;
  }

  public static <K> Set<K> nullIfEmpty(final Set<K> input) {
    return isEmpty(input) ? null : input;
  }

  // ================================================================================
  //  New Map related
  // ================================================================================
  public static <K, V> Set<K> newHashSetFrom(final Collection<V> values, final Function<V, K> keyMapper) {
    final HashSet<K> set = HashSet.newHashSet(values.size());
    for (final V value: values) {
      set.add(keyMapper.apply(value));
    }
    return set;
  }
}
