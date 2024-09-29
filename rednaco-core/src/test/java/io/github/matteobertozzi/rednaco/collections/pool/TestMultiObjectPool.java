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
package io.github.matteobertozzi.rednaco.collections.pool;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.matteobertozzi.easerinsights.logging.LogProvider;
import io.github.matteobertozzi.easerinsights.logging.Logger;
import io.github.matteobertozzi.easerinsights.logging.Logger.LogLevel;
import io.github.matteobertozzi.easerinsights.tracing.Span;
import io.github.matteobertozzi.rednaco.strings.StringFormat;
import io.github.matteobertozzi.rednaco.util.function.NoOpFunction;

public class TestMultiObjectPool {
  record ItemType(int type) {}
  record Item(ItemType type, int id) implements Closeable {
    @Override public void close() throws IOException {}
  }

  @Test
  public void testAddRemove() {
    final ItemType typeA = new ItemType(1);
    final Item itemA1 = new Item(typeA, 10);
    final Item itemA2 = new Item(typeA, 20);
    final Item itemA3 = new Item(typeA, 30);
    final Item itemA4 = new Item(typeA, 40);
    final Item itemA5 = new Item(typeA, 50);

    final MultiObjectPool<ItemType, Item> pool = new MultiObjectPool<>(3);
    Assertions.assertEquals(0, pool.size());
    Assertions.assertTrue(pool.add(itemA1.type(), itemA1));
    Assertions.assertEquals(1, pool.size());
    Assertions.assertTrue(pool.add(itemA1.type(), itemA2));
    Assertions.assertEquals(2, pool.size());
    Assertions.assertTrue(pool.add(itemA1.type(), itemA3));
    Assertions.assertEquals(3, pool.size());
    Assertions.assertFalse(pool.add(itemA1.type(), itemA4));
    Assertions.assertFalse(pool.add(itemA1.type(), itemA5));
    Assertions.assertEquals(3, pool.size());

    Assertions.assertNotNull(pool.poll(typeA));
    Assertions.assertEquals(2, pool.size());
    Assertions.assertNotNull(pool.poll(typeA));
    Assertions.assertEquals(1, pool.size());

    Assertions.assertTrue(pool.add(itemA1.type(), itemA4));
    Assertions.assertEquals(2, pool.size());
    Assertions.assertTrue(pool.add(itemA1.type(), itemA5));
    Assertions.assertEquals(3, pool.size());

    Assertions.assertNotNull(pool.poll(typeA));
    Assertions.assertEquals(2, pool.size());
    Assertions.assertNotNull(pool.poll(typeA));
    Assertions.assertEquals(1, pool.size());
    Assertions.assertNotNull(pool.poll(typeA));
    Assertions.assertEquals(0, pool.size());
  }

  @Test
  public void testRandAddRemove() {
    final HashSet<Item> itemSet = HashSet.newHashSet(8);
    final MultiObjectPool<ItemType, Item> itemPool = new MultiObjectPool<>(8);

    final ItemType typeA = new ItemType(1);
    for (int i = 0; i < 100; ++i) {
      if (Math.random() > 0.5) {
        final Item item = new Item(typeA, i);
        if (itemPool.add(item.type(), item)) {
          itemSet.add(item);
        }
        Assertions.assertEquals(itemSet.size(), itemPool.size());
      } else {
        final Item item = itemPool.poll(typeA);
        if (item != null) {
          Assertions.assertTrue(itemSet.remove(item));
        }
      }
    }
  }

  @Test
  public void testMultiAddRemove() {
    final ItemType typeA = new ItemType(1);
    final ItemType typeB = new ItemType(2);
    final ItemType typeC = new ItemType(3);

    final MultiObjectPool<ItemType, Item> itemPool = new MultiObjectPool<>(8);
    Assertions.assertTrue(itemPool.add(typeA, new Item(typeA, 1)));
    Assertions.assertTrue(itemPool.add(typeA, new Item(typeA, 2)));
    Assertions.assertTrue(itemPool.add(typeB, new Item(typeB, 10)));
    Assertions.assertTrue(itemPool.add(typeA, new Item(typeA, 3)));
    Assertions.assertTrue(itemPool.add(typeB, new Item(typeB, 20)));
    Assertions.assertTrue(itemPool.add(typeB, new Item(typeB, 30)));
    Assertions.assertTrue(itemPool.add(typeA, new Item(typeA, 4)));
    Assertions.assertTrue(itemPool.add(typeB, new Item(typeB, 40)));

    Assertions.assertNull(itemPool.poll(typeC));
    Assertions.assertEquals(typeB, itemPool.poll(typeB).type());
    Assertions.assertEquals(typeB, itemPool.poll(typeB).type());
    Assertions.assertEquals(typeA, itemPool.poll(typeA).type());
    Assertions.assertEquals(typeA, itemPool.poll(typeA).type());
    Assertions.assertEquals(typeA, itemPool.poll(typeA).type());
    Assertions.assertEquals(typeB, itemPool.poll(typeB).type());
    Assertions.assertEquals(typeB, itemPool.poll(typeB).type());
    Assertions.assertNull(itemPool.poll(typeB));
    Assertions.assertEquals(typeA, itemPool.poll(typeA).type());
    Assertions.assertNull(itemPool.poll(typeA));
  }

  @Test
  public void testCleaner() {
    Logger.setLogProvider(new LogProvider() {

      @Override
      public void logMessage(final Span span, final LogLevel level, final String format, final Object[] args) {
        System.out.println(StringFormat.namedFormat(format, args));
      }

      @Override
      public void logMessage(final Span span, final LogLevel level, final Throwable exception, final String format, final Object[] args) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'logMessage'");
      }

      @Override
      public void logEntry(final LogEntry entry) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'logEntry'");
      }

    });
    final MultiObjectPool<ItemType, Item> itemPool = new MultiObjectPool<>(16);
    final ItemType typeA = new ItemType(1);
    for (int i = 0; i < 16; ++i) {
      final Item item = new Item(typeA, i);
      Assertions.assertTrue(itemPool.add(item.type(), item));
    }
    Assertions.assertEquals(16, itemPool.size());
    Assertions.assertEquals(8, itemPool.clean(item -> (item.id() & 1) == 0, NoOpFunction::consumer));
    Assertions.assertEquals(8, itemPool.size());
  }
}
