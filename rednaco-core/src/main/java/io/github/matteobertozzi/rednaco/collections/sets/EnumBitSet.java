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

import io.github.matteobertozzi.rednaco.util.BitUtil;

import java.util.Arrays;

public class EnumBitSet<T extends Enum<T>> {
    private final long[] words;

    public EnumBitSet(final Class<T> elementType) {
      this(elementType.getEnumConstants());
    }

    public EnumBitSet(final T[] universe) {
      int maxOrdinal = 0;
      for (int i = 0; i < universe.length; ++i) {
        final int ordinal = universe[i].ordinal();
        if (ordinal < 0) {
          throw new IllegalArgumentException("expected ordinals from 0-N: " + universe[i]);
        }
        maxOrdinal = Math.max(maxOrdinal, ordinal);
      }

      final int length = BitUtil.align(maxOrdinal + 1, 64) >>> 6;
      this.words = new long[length];
    }

    public boolean isEmpty() {
      for (int i = 0; i < words.length; ++i) {
        if (words[i] != 0) {
          return false;
        }
      }
      return true;
    }

    public void set(final T key, final boolean value) {
      final int ordinal = key.ordinal();
      final int index =  ordinal >>> 6;
      final int offset = ordinal & 63;
      if (value) {
        words[index] |= (1L << offset);
      } else {
        words[index] &= ~(1L << offset);
      }
    }

    public void set(final T key) {
      set(key, true);
    }

    public void clear(final T key) {
      set(key, false);
    }

    public void clear() {
      Arrays.fill(words, 0);
    }

    public boolean get(final T key) {
      final int ordinal = key.ordinal();
      final int index =  ordinal >>> 6;
      final int offset = ordinal & 63;
      return (words[index] & (1L << offset)) != 0;
    }
  }
