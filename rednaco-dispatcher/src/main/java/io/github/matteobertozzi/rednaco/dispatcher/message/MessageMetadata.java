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

package io.github.matteobertozzi.rednaco.dispatcher.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import io.github.matteobertozzi.rednaco.collections.arrays.ArrayUtil;
import io.github.matteobertozzi.rednaco.collections.lists.ListUtil;
import io.github.matteobertozzi.rednaco.strings.StringConverter;

public interface MessageMetadata {
  int size();
  boolean isEmpty();

  String get(String key);
  List<String> getList(String key);

  List<Map.Entry<String, String>> entries();

  void forEach(final BiConsumer<? super String, ? super String> action);

  // --------------------------------------------------------------------------------
  // Property helpers
  // --------------------------------------------------------------------------------
  default String getString(final String key, final String defaultValue) {
    final String value = get(key);
    return value != null ? value : defaultValue;
  }

  default int getInt(final String key, final int defaultValue) {
    return StringConverter.toInt(key, get(key), defaultValue);
  }

  default long getLong(final String key, final long defaultValue) {
    return StringConverter.toLong(key, get(key), defaultValue);
  }

  default float getFloat(final String key, final float defaultValue) {
    return StringConverter.toFloat(key, get(key), defaultValue);
  }

  default double getDouble(final String key, final double defaultValue) {
    return StringConverter.toDouble(key, get(key), defaultValue);
  }

  default boolean getBoolean(final String key, final boolean defaultValue) {
    return StringConverter.toBoolean(key, get(key), defaultValue);
  }

  default <T extends Enum<T>> T getEnumValue(final Class<T> enumType, final String key, final T defaultValue) {
    return StringConverter.toEnumValue(enumType, key, get(key), defaultValue);
  }

  // --------------------------------------------------------------------------------
  // List lookup helpers
  // --------------------------------------------------------------------------------
  default String[] getStringArray(final String key) {
    final List<String> items = getList(key);
    return ListUtil.isEmpty(items) ? null : items.toArray(new String[0]);
  }

  default Set<String> getStringSet(final String key) {
    final String[] items = getStringArray(key);
    return ArrayUtil.isEmpty(items) ? Set.of() : Set.of(items);
  }

  default boolean[] getBooleanArray(final String key) {
    // TODO
    return null;
  }

  default short[] getShortArray(final String key) {
    // TODO
    return null;
  }

  default int[] getIntArray(final String key) {
    // TODO
    return null;
  }

  default long[] getLongArray(final String key) {
    // TODO
    return null;
  }

  default float[] getFloatArray(final String key) {
    // TODO
    return null;
  }

  default double[] getDoubleArray(final String key) {
    // TODO
    return null;
  }

  default String[] toStringArray() {
    final ArrayList<String> kvs = new ArrayList<>(size());
    forEach((k, v) -> {
      kvs.add(k);
      kvs.add(v);
    });
    return kvs.toArray(new String[0]);
  }
}
