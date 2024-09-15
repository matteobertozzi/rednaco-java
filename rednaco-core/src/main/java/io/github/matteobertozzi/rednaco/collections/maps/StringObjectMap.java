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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.github.matteobertozzi.rednaco.strings.StringConverter;

public final class StringObjectMap extends HashMap<String, Object> {
  public StringObjectMap() {
    super(0);
  }

  public StringObjectMap(final int initialSize) {
    super(initialSize);
  }

  public StringObjectMap(final Map<String, Object> properties) {
    this(properties.entrySet());
  }

  public StringObjectMap(final Collection<Entry<String, Object>> entries) {
    super(entries.size());
    for (final Entry<String, Object> entry: entries) {
      put(entry.getKey(), entry.getValue());
    }
  }

  public static StringObjectMap singletonMap(final String key, final Object value) {
    return new StringObjectMap(Map.of(key, value));
  }

  public static StringObjectMap fromKeyValues(final String[] key, final Object[] value) {
    final StringObjectMap map = new StringObjectMap(key.length);
    for (int i = 0; i < key.length; ++i) {
      map.put(key[i], value[i]);
    }
    return map;
  }

  public boolean isNotEmpty() {
    return !isEmpty();
  }

  // ================================================================================
  // Property helpers
  // ================================================================================
  public String getString(final String key, final String defaultValue) {
    return getString(this, key, defaultValue);
  }

  public int getShort(final String key, final short defaultValue) {
    return getShort(this, key, defaultValue);
  }

  public int getInt(final String key, final int defaultValue) {
    return getInt(this, key, defaultValue);
  }

  public long getLong(final String key, final long defaultValue) {
    return getLong(this, key, defaultValue);
  }

  public float getFloat(final String key, final float defaultValue) {
    return getFloat(this, key, defaultValue);
  }

  public double getDouble(final String key, final double defaultValue) {
    return getDouble(this, key, defaultValue);
  }

  public boolean getBoolean(final String key, final boolean defaultValue) {
    return getBoolean(this, key, defaultValue);
  }

  public <T extends Enum<T>> T getEnumValue(final String key, final T defaultValue) {
    return getEnumValue(this, key, defaultValue);
  }

  // ================================================================================
  // List lookup helpers
  // ================================================================================
  public String[] getStringList(final String key, final String[] defaultValue) {
    return getStringList(this, key, defaultValue);
  }

  public int[] getIntArray(final String key, final int[] defaultValue) {
    return getIntArray(this, key, defaultValue);
  }

  public long[] getLongArray(final String key, final long[] defaultValue) {
    return getLongArray(this, key, defaultValue);
  }

  // ================================================================================
  //  Primitives lookup helpers
  // ================================================================================
  public static String getString(final Map<String, Object> map, final String key, final String defaultValue) {
    final String value = (String) map.get(key);
    return value != null ? value : defaultValue;
  }

  public static int getShort(final Map<String, Object> map, final String key, final short defaultValue) {
    final Object oValue = map.get(key);
    return switch (oValue) {
      case null -> defaultValue;
      case final Number value -> value.shortValue();
      case final String sValue -> StringConverter.toShort(sValue, defaultValue);
      default -> throw new IllegalArgumentException("expected a int got: " + oValue.getClass() + " " + oValue);
    };

  }

  public static int getInt(final Map<String, Object> map, final String key, final int defaultValue) {
    final Object oValue = map.get(key);
    return switch (oValue) {
      case null -> defaultValue;
      case final Number value -> value.intValue();
      case final String sValue -> StringConverter.toInt(sValue, defaultValue);
      default -> throw new IllegalArgumentException("expected a int got: " + oValue.getClass() + " " + oValue);
    };

  }

  public static long getLong(final Map<String, Object> map, final String key, final long defaultValue) {
    final Object oValue = map.get(key);
    return switch (oValue) {
      case null -> defaultValue;
      case final Number value -> value.longValue();
      case final String sValue -> StringConverter.toLong(sValue, defaultValue);
      default -> throw new IllegalArgumentException("expected a long got: " + oValue.getClass() + " " + oValue);
    };

  }

  public static float getFloat(final Map<String, Object> map, final String key, final float defaultValue) {
    final Object oValue = map.get(key);
    return switch (oValue) {
      case null -> defaultValue;
      case final Number value -> value.floatValue();
      case final String sValue -> StringConverter.toFloat(sValue, defaultValue);
      default -> throw new IllegalArgumentException("expected a float got: " + oValue.getClass() + " " + oValue);
    };

  }

  public static double getDouble(final Map<String, Object> map, final String key, final double defaultValue) {
    final Object oValue = map.get(key);
    return switch (oValue) {
      case null -> defaultValue;
      case final Number value -> value.floatValue();
      case final String sValue -> StringConverter.toDouble(sValue, defaultValue);
      default -> throw new IllegalArgumentException("expected a double got: " + oValue.getClass() + " " + oValue);
    };

  }

  public static boolean getBoolean(final Map<String, Object> map, final String key, final boolean defaultValue) {
    final Object oValue = map.get(key);
    return switch (oValue) {
      case null -> defaultValue;
      case final Boolean value -> value;
      case final String sValue -> StringConverter.toBoolean(sValue, defaultValue);
      default -> throw new IllegalArgumentException("expected a boolean got: " + oValue.getClass() + " " + oValue);
    };

  }

  @SuppressWarnings("unchecked")
  public static <T extends Enum<T>> T getEnumValue(final Map<String, Object> map, final String key, final T defaultValue) {
    final T value = (T) map.get(key);
    return value != null ? value : defaultValue;
  }

  // ================================================================================
  // List lookup helpers
  // ================================================================================
  public static String[] getStringList(final Map<String, Object> map, final String key, final String[] defaultValue) {
    final String[] value = (String[]) map.get(key);
    return value != null ? value : defaultValue;
  }

  public static int[] getIntArray(final Map<String, Object> map, final String key, final int[] defaultValue) {
    final int[] value = (int[]) map.get(key);
    return value != null ? value : defaultValue;
  }

  public static long[] getLongArray(final Map<String, Object> map, final String key, final long[] defaultValue) {
    final long[] value = (long[]) map.get(key);
    return value != null ? value : defaultValue;
  }
}
