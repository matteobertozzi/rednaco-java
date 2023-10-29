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

package io.github.matteobertozzi.rednaco.threading;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class TestThreadUtil {
  @Test
  @Timeout(value = 5, unit = TimeUnit.SECONDS)
  public void testCondition() throws Exception {
    final AtomicBoolean running = new AtomicBoolean(true);
    final AtomicInteger events = new AtomicInteger();
    final ReentrantLock lock = new ReentrantLock();
    final Condition cond = lock.newCondition();

    final List<Thread> threads = ThreadUtil.runInThreadsNoWait("TestCondition", 2, () -> {
      while (running.get()) {
        lock.lock();
        try {
          ThreadUtil.conditionAwait(cond);
          events.incrementAndGet();
        } finally {
          lock.unlock();
        }
      }
    });

    try {
      ThreadUtil.sleep(250);
      ThreadUtil.conditionSignal(cond, lock);
      while (events.get() < 1) Thread.yield();
      ThreadUtil.withLock(lock, () -> Assertions.assertEquals(1, events.get()));

      ThreadUtil.conditionSignalAll(cond, lock);
      while (events.get() < 3) Thread.yield();
      ThreadUtil.withLock(lock, () -> Assertions.assertEquals(3, events.get()));

      ThreadUtil.conditionSignal(cond, lock);
      while (events.get() < 4) Thread.yield();
      ThreadUtil.withLock(lock, () -> Assertions.assertEquals(4, events.get()));

      ThreadUtil.conditionSignal(cond, lock);
      while (events.get() < 5) Thread.yield();
      ThreadUtil.withLock(lock, () -> Assertions.assertEquals(5, events.get()));

      ThreadUtil.conditionSignalAll(cond, lock);
      while (events.get() < 7) Thread.yield();
      ThreadUtil.withLock(lock, () -> Assertions.assertEquals(7, events.get()));
    } finally {
      running.set(false);
      ThreadUtil.conditionSignalAll(cond, lock);
      ThreadUtil.shutdown(threads);
    }
  }

  @Test
  public void testRunInThreads() {
    final AtomicInteger count = new AtomicInteger(0);
    final Set<String> threadNames = ConcurrentHashMap.newKeySet();
    ThreadUtil.runInThreads("TestRunT", 8, () -> {
      ThreadUtil.sleep(Math.round(Math.random() * 300));
      count.incrementAndGet();
      threadNames.add(Thread.currentThread().getName());
    });

    Assertions.assertEquals(8, count.get());
    Assertions.assertEquals(8, threadNames.size());
    verifyThreadNames(threadNames, "TestRunT");
  }

  @Test
  public void runInThreadsNoWait() {
    final AtomicInteger count = new AtomicInteger(0);
    final Set<String> threadNames = ConcurrentHashMap.newKeySet();
    final List<Thread> threads = ThreadUtil.runInThreadsNoWait("TestRunT", 8, () -> {
      ThreadUtil.sleep(200 + (Math.round(Math.random() * 250)));
      count.incrementAndGet();
      threadNames.add(Thread.currentThread().getName());
    });
    Assertions.assertNotEquals(8, count.get());

    ThreadUtil.shutdown(threads);
    Assertions.assertEquals(8, count.get());
    Assertions.assertEquals(8, threads.size());
    Assertions.assertEquals(8, threadNames.size());
    verifyThreadNames(threadNames, "TestRunT");
  }

  private static final Pattern THREAD_NAME_PATTERN = Pattern.compile("^([A-Za-z0-9]+)-([0-9]+)-([0-9]+)$");
  private static void verifyThreadNames(final Set<String> threadNames, final String namePrefix) {
    final String[] sortedThreadNames = threadNames.toArray(new String[0]);
    Arrays.sort(sortedThreadNames);
    for (int i = 0; i < sortedThreadNames.length; ++i) {
      final Matcher m = THREAD_NAME_PATTERN.matcher(sortedThreadNames[i]);
      Assertions.assertTrue(m.matches());
      Assertions.assertEquals(3, m.groupCount());
      Assertions.assertEquals(namePrefix, m.group(1));
      Assertions.assertEquals(String.valueOf(i + 1), m.group(3));
    }
  }
}
