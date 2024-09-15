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

package io.github.matteobertozzi.rednaco.collections.maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class MapUtil {
  private MapUtil() {
    // no-op
  }

  public static <K, V> K extractRandKey(final Map<K, V> input) {
    if (input == null || input.isEmpty()) return null;
    return input.keySet().iterator().next();
  }

  // ================================================================================
  //  Length related
  // ================================================================================
  public static <K, V> int size(final Map<K, V> input) {
    return input != null ? input.size() : 0;
  }

  public static <K, V> boolean isEmpty(final Map<K, V> input) {
    return input == null || input.isEmpty();
  }

  public static <K, V> boolean isNotEmpty(final Map<K, V> input) {
    return input != null && !input.isEmpty();
  }

  // ================================================================================
  //  Instance related
  // ================================================================================
  public static <K, V> Map<K, V> emptyIfNull(final Map<K, V> input) {
    return input == null ? Map.of() : input;
  }

  public static <K, V> Map<K, V> nullIfEmpty(final Map<K, V> input) {
    return isEmpty(input) ? null : input;
  }

  // ================================================================================
  //  New Map related
  // ================================================================================
  public static <K, V> Map<K, V> newHashMapFrom(final V[] values, final Function<V, K> keyMapper) {
    final HashMap<K, V> map = HashMap.newHashMap(values.length);
    for (final V value: values) {
      map.put(keyMapper.apply(value), value);
    }
    return map;
  }

  public static <K, V> Map<K, V> newHashMapFrom(final Collection<V> values, final Function<V, K> keyMapper) {
    final HashMap<K, V> map = HashMap.newHashMap(values.size());
    for (final V value: values) {
      map.put(keyMapper.apply(value), value);
    }
    return map;
  }

  public static <T, K, V> Map<K, V> newHashMapFrom(final T[] input, final Function<T, K> keyMapper, final Function<T, V> valueMapper) {
    final HashMap<K, V> map = HashMap.newHashMap(input.length);
    for (final T value: input) {
      map.put(keyMapper.apply(value), valueMapper.apply(value));
    }
    return map;
  }

  public static <T, K, V> Map<K, V> newHashMapFrom(final Collection<T> input, final Function<T, K> keyMapper, final Function<T, V> valueMapper) {
    final HashMap<K, V> map = HashMap.newHashMap(input.size());
    for (final T value: input) {
      map.put(keyMapper.apply(value), valueMapper.apply(value));
    }
    return map;
  }

  public static <K, IV, OV> Map<K, OV> newHashMapFrom(final Map<K, IV> input, final Function<IV, OV> valueMapper) {
    final HashMap<K, OV> map = HashMap.newHashMap(input.size());
    for (final Map.Entry<K, IV> entry: input.entrySet()) {
      map.put(entry.getKey(), valueMapper.apply(entry.getValue()));
    }
    return map;
  }

  public static <IK, IV, OK, OV> Map<OK, OV> newHashMapFrom(final Map<IK, IV> input, final Function<IK, OK> keyMapper, final Function<IV, OV> valueMapper) {
    final HashMap<OK, OV> map = HashMap.newHashMap(input.size());
    for (final Map.Entry<IK, IV> entry: input.entrySet()) {
      map.put(keyMapper.apply(entry.getKey()), valueMapper.apply(entry.getValue()));
    }
    return map;
  }

  public static <IK, IV, OK, OV> Map<OK, OV> newHashMapFrom(final Map<IK, IV> input, final BiFunction<IK, IV, OK> keyMapper, final Function<IV, OV> valueMapper) {
    final HashMap<OK, OV> map = HashMap.newHashMap(input.size());
    for (final Map.Entry<IK, IV> entry: input.entrySet()) {
      map.put(keyMapper.apply(entry.getKey(), entry.getValue()), valueMapper.apply(entry.getValue()));
    }
    return map;
  }

  // ================================================================================
  //  New Multi Map related
  // ================================================================================
  public static <K, V> Map<K, List<V>> newMultiHashMapFrom(final Collection<V> values, final Function<V, K> keyMapper) {
    final HashMap<K, List<V>> map = HashMap.newHashMap(values.size());
    for (final V entry: values) {
      map.computeIfAbsent(keyMapper.apply(entry), k -> new ArrayList<>()).add(entry);
    }
    return map;
  }
}
