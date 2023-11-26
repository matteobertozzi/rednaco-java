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

package io.github.matteobertozzi.rednaco.collections.lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;

import io.github.matteobertozzi.rednaco.collections.arrays.IntArray;
import io.github.matteobertozzi.rednaco.util.function.FailableConsumer;

public final class ListUtil {
  private ListUtil() {
    // no-op
  }

  // ================================================================================
  //  length related
  // ================================================================================
  public static <T> int size(final Collection<T> input) {
    return input != null ? input.size() : 0;
  }

  public static <T> boolean isEmpty(final Collection<T> input) {
    return input == null || input.isEmpty();
  }

  public static <T> boolean isNotEmpty(final Collection<T> input) {
    return input != null && !input.isEmpty();
  }

  // ================================================================================
  //  Instance related
  // ================================================================================
  public static <T> List<T> emptyIfNull(final List<T> input) {
    return input == null ? List.of() : input;
  }

  public static <T> List<T> nullIfEmpty(final List<T> input) {
    return isEmpty(input) ? null : input;
  }

  // ================================================================================
  //  New list related
  // ================================================================================
  public static <T> List<T> newArrayListIfNull(final List<T> input) {
    return input != null ? input : new ArrayList<>();
  }

  public static List<String> newArrayList(final Collection<String> input) {
    return input != null ? new ArrayList<>(input) : new ArrayList<>();
  }

  public static <T, TT> List<TT> newArrayListFrom(final Collection<T> input, final Function<T, TT> transformer) {
    final ArrayList<TT> output = new ArrayList<>(input.size());
    for (final T value: input) {
      output.add(transformer.apply(value));
    }
    return output;
  }

  // ================================================================================
  //  GroupBy related
  // ================================================================================
  public static <T> int[] groupBy(final List<T> list, final BiPredicate<T, T> keyEquals) {
    final IntArray groupsIndex = new IntArray(8);
    int index = 0;
    T lastKey = null;
    for (final T item: list) {
      if (lastKey == null || !keyEquals.test(lastKey, item)) {
        groupsIndex.add(index);
        lastKey = item;
      }
      index++;
    }
    return groupsIndex.size() > 1 ? groupsIndex.drain() : null;
  }

  public static <T> void forEachGroup(final List<T> list, final BiPredicate<T, T> keyEquals, final FailableConsumer<List<T>> groupConsumer) throws Exception {
    final int[] groupsIndex = groupBy(list, keyEquals);
    forEachGroup(list, groupsIndex, groupConsumer);
  }

  public static <T> void forEachGroup(final List<T> list, final int[] groupsIndex, final FailableConsumer<List<T>> groupConsumer) throws Exception {
    if (groupsIndex == null) {
      groupConsumer.accept(list);
      return;
    }

    final int lastIndex = groupsIndex.length - 1;
    for (int i = 0; i < lastIndex; ++i) {
      final int startIndex = groupsIndex[i];
      final int endIndex = groupsIndex[i + 1];
      groupConsumer.accept(list.subList(startIndex, endIndex));
    }

    groupConsumer.accept(list.subList(groupsIndex[lastIndex], list.size()));
  }
}
