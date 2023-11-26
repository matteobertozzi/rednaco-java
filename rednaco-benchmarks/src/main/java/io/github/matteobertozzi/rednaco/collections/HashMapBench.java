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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import io.github.matteobertozzi.rednaco.collections.items.TestItemStrLong;
import io.github.matteobertozzi.rednaco.collections.items.TestItemWithBadHashCode;
import io.github.matteobertozzi.rednaco.collections.maps.IndexedHashMap;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 4, time = 2)
@Measurement(iterations = 4, time = 2)
@Fork(value = 3)
public class HashMapBench {
  private Supplier<Map<Object, Object>> mapSupplier;
  private Object[] testItems;

  @Param("1000000")
  private int size;

  @Param
  private MapType mapType;

  @Param
  private RecType recType;

  public enum MapType {
    HASH_MAP,
    INDEXED_HASH_MAP,
  }

  public enum RecType {
    INT,
    REC,
    BAD_HASHCODE
  }

  @Setup
  public void setup() {
    switch (mapType) {
      case HASH_MAP -> mapSupplier = () -> new HashMap<>(8);
      case INDEXED_HASH_MAP -> mapSupplier = () -> new IndexedHashMap<>(8);
      default -> throw new AssertionError();
    }

    int adjustSize = size;
    if (recType == RecType.BAD_HASHCODE) {
      adjustSize = Math.min(size, 1500);
    }

    final Random rand = new Random();
    testItems = new Object[adjustSize];
    for (int i = 0; i < testItems.length; ++i) {
      testItems[i] = switch (recType) {
        case INT -> rand.nextInt();
        case REC -> new TestItemStrLong("k" + rand.nextLong(0, 1_000), rand.nextLong(0, 1_000));
        case BAD_HASHCODE -> new TestItemWithBadHashCode(rand.nextLong());
      };
    }
  }

  @Benchmark
  public int put() {
    final Map<Object, Object> map = mapSupplier.get();
    for (int i = 0; i < testItems.length; ++i) {
      final Object item = testItems[i];
      map.put(item, item);
    }
    return map.size();
  }

  @Benchmark
  public long containsGetPut() {
    Object v = null;
    final Map<Object, Object> map = mapSupplier.get();
    for (int i = 0; i < testItems.length; ++i) {
      final Object item = testItems[i];
      if (map.containsKey(item)) {
        v = map.get(item);
      } else {
        map.put(item, item);
      }
    }
    return map.size() + (v != null ? 1 : 0);
  }

  public static void main(final String[] args) throws Exception {
    final Options opt = new OptionsBuilder()
        //.addProfiler(GCProfiler.class)
        .build();
    new Runner(opt).run();
  }
}