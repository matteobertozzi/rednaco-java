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

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import io.github.matteobertozzi.easerinsights.logging.Logger;
import io.github.matteobertozzi.rednaco.strings.HumansUtil;
import io.github.matteobertozzi.rednaco.threading.ThreadUtil.ExecutableFunction;

public final class BenchUtil {
  private BenchUtil() {
    // no-op
  }

  public static void run(final String name, final long count, final ExecutableFunction runnable) throws Throwable {
    final long startTime = System.nanoTime();
    for (long i = 0; i < count; ++i) {
      runnable.run();
    }
    final long elapsed = System.nanoTime() - startTime;
    System.err.println("[BENCH] " + name
      + " - " + HumansUtil.humanCount(count) + " runs took " + HumansUtil.humanTimeNanos(elapsed)
      + " " + HumansUtil.humanRate(count, elapsed, TimeUnit.NANOSECONDS));
  }

  public static void runInThreads(final String name, final int nThreads, final long count, final ExecutableFunction runnable) throws Throwable {
    final long startTime = System.nanoTime();
    ThreadUtil.runInThreads("bench-" + name, nThreads, () -> {
      try {
        run(name, count, runnable);
      } catch (final Throwable e) {
        Logger.error(e, "failed while running bench");
      }
    });
    final long elapsed = System.nanoTime() - startTime;
    System.err.println("[BENCH-TOTAL] " + name
      + " - " + HumansUtil.humanCount(count * nThreads) + " runs took " + HumansUtil.humanTimeNanos(elapsed)
      + " " + HumansUtil.humanRate(count * nThreads, elapsed, TimeUnit.NANOSECONDS));
  }

  public static void run(final String name, final Duration duration, final ExecutableFunction runnable) throws Throwable {
    final long startTime = System.nanoTime();
    final long expectedTime = startTime + duration.toNanos();
    long count = 0;
    while (System.nanoTime() < expectedTime) {
      runnable.run();
      count++;
    }
    final long elapsed = System.nanoTime() - startTime;
    System.err.println("[BENCH] " + name
      + " - " + HumansUtil.humanCount(count) + " runs took " + HumansUtil.humanTimeNanos(elapsed)
      + " " + HumansUtil.humanRate(count, elapsed, TimeUnit.NANOSECONDS));
  }
}
