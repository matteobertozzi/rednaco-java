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

package io.github.matteobertozzi.rednaco.time;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.github.matteobertozzi.rednaco.strings.HumansUtil;
import io.github.matteobertozzi.rednaco.threading.BenchUtil;

public final class TimeUtil {
  private static ClockProvider CLOCK_INSTANCE = SystemClock.INSTANCE;

  private TimeUtil() {
    // no-op
  }

  public static long currentEpochMillis() {
    return CLOCK_INSTANCE.epochMillis();
  }

  public static long currentEpochNanos() {
    return CLOCK_INSTANCE.epochNanos();
  }

  public static long epochNanos(final Instant instant) {
    final long seconds = instant.getEpochSecond();
    final long nanosFromSecond = instant.getNano();
    return (seconds * 1_000_000_000L) + nanosFromSecond;
  }

  // =====================================================================================
  //  Clock Util
  // =====================================================================================
  public static long alignToWindow(final long timestamp, final int window) {
    return timestamp - (timestamp % window);
  }

  // =====================================================================================
  //  Clock Providers
  // =====================================================================================
  public interface ClockProvider {
    long epochMillis();
    long epochNanos();
  }

  public static ClockProvider getClockProvider() {
    return TimeUtil.CLOCK_INSTANCE;
  }

  public static void setClockProvider(final ClockProvider provider) {
    TimeUtil.CLOCK_INSTANCE = provider;
  }

  public static void setSystemClockProvider() {
    setClockProvider(SystemClock.INSTANCE);
  }

  public static final class SystemClock implements ClockProvider {
    public static final SystemClock INSTANCE = new SystemClock();

    private SystemClock() {
      // no-op
    }

    @Override
    public long epochMillis() {
      return System.currentTimeMillis();
    }

    @Override
    public long epochNanos() {
      return TimeUtil.epochNanos(Instant.now());
    }
  }

  public static final class CachedSystemClock implements ClockProvider {
    private final long refEpochNanos = System.currentTimeMillis() * 1_000_000L;
    private final long refNanos = System.nanoTime();

    @Override
    public long epochMillis() {
      return epochNanos() / 1_000_000L;
    }

    @Override
    public long epochNanos() {
      return refEpochNanos + (System.nanoTime() - refNanos);
    }
  }

  public static class ManualClockProvider implements ClockProvider {
    private long nanos;

    public ManualClockProvider(final long nanos) {
      this.nanos = nanos;
    }

    @Override
    public long epochMillis() {
      return TimeUnit.NANOSECONDS.toMillis(nanos);
    }

    @Override
    public long epochNanos() {
      return nanos;
    }

    public void reset() {
      this.nanos = 0;
    }

    public void setTime(final long value, final TimeUnit unit) {
      this.nanos = unit.toNanos(value);
    }

    public void setTime(final Instant instant) {
      setTime(instant.toEpochMilli(), TimeUnit.MILLISECONDS);
    }

    public void setTime(ZonedDateTime dateTime) {
      setTime(dateTime.toInstant());
    }

    public void incTime(final long value, final TimeUnit unit) {
      this.nanos += unit.toNanos(value);
    }

    public void incTime(final Duration duration) {
      this.nanos += duration.toNanos();
    }

    public void decTime(final long value, final TimeUnit unit) {
      this.nanos -= unit.toNanos(value);
    }

    public void decTime(final Duration duration) {
      this.nanos -= duration.toNanos();
    }
  }

  public static void main(final String[] args) throws Throwable {
    final CachedSystemClock cachedClock = new CachedSystemClock();
    final SystemClock sysClock = SystemClock.INSTANCE;

    final long NRUNS = 20_000_000L;
    BenchUtil.run("System.nanoTime()         ", NRUNS, System::nanoTime);
    BenchUtil.run("sysClock.epochNanos()     ", NRUNS, sysClock::epochNanos);
    BenchUtil.run("cachedClock.epochNanos()  ", NRUNS, cachedClock::epochNanos);
    BenchUtil.run("System.currentTimeMillis()", NRUNS, System::currentTimeMillis);
    BenchUtil.run("sysClock.epochMillis()    ", NRUNS, sysClock::epochMillis);
    BenchUtil.run("cachedClock.epochMillis() ", NRUNS, cachedClock::epochMillis);

    final Random rand = new Random();
    final long startTime = System.nanoTime();
    while ((System.nanoTime() - startTime) < TimeUnit.MINUTES.toNanos(10)) {
      final long cachedEpochNanos = cachedClock.epochNanos();
      final long sysEpochNanos = sysClock.epochNanos();
      final long deltaNanos = Math.abs(cachedEpochNanos - sysEpochNanos);
      if (deltaNanos > 200_000) {
        System.out.println(" -> delta nanos too large: " + HumansUtil.humanTimeNanos(deltaNanos) + " -> " + deltaNanos + "ns");
      }

      final long cachedEpochMillis = cachedClock.epochMillis();
      final long sysEpochMillis = sysClock.epochMillis();
      final long deltaMillis = Math.abs(cachedEpochMillis - sysEpochMillis);
      if (deltaMillis > 5) {
        System.out.println(" -> delta millis too large: " + HumansUtil.humanTimeMillis(deltaMillis));
      }
      Thread.sleep(rand.nextInt(1, 1000));
    }
  }
}

