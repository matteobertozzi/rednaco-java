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

package io.github.matteobertozzi.rednaco.strings;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

public final class HumansUtil {
  private HumansUtil() {
    // no-op
  }

  private static final ThreadLocal<NumberFormat> humanDecimalFormatter = ThreadLocal.withInitial(() -> new DecimalFormat("0.00"));
  private static String decimalFormat(final String suffix, final double value) {
    return humanDecimalFormatter.get().format(value) + suffix;
  }

  // ================================================================================
  //  Bit/Bytes related
  // ================================================================================
  public static String humanBits(final long bits) {
    if (bits >= 1000_000_000_000_000_000L) return decimalFormat("Ebit", (double) bits / 1000_000_000_000_000_000L);
    if (bits >= 1000_000_000_000_000L) return decimalFormat("Pbit", (double) bits / 1000_000_000_000_000L);
    if (bits >= 1000_000_000_000L) return decimalFormat("Tbit", (double) bits / 1000_000_000_000L);
    if (bits >= 1000_000_000L) return decimalFormat("Gbit", (double) bits / 1000_000_000L);
    if (bits >= 1000_000L) return decimalFormat("Mbit", (double) bits / 1000_000L);
    if (bits >= 1000L) return decimalFormat("Kbit", (double) bits / 1000L);
    return bits + "bit";
  }

  public static String humanBytes(final long bytes) {
    if (bytes >= (1L << 60)) return decimalFormat("EiB", (double) bytes / (1L << 60));
    if (bytes >= (1L << 50)) return decimalFormat("PiB", (double) bytes / (1L << 50));
    if (bytes >= (1L << 40)) return decimalFormat("TiB", (double) bytes / (1L << 40));
    if (bytes >= (1L << 30)) return decimalFormat("GiB", (double) bytes / (1L << 30));
    if (bytes >= (1L << 20)) return decimalFormat("MiB", (double) bytes / (1L << 20));
    if (bytes >= (1L << 10)) return decimalFormat("KiB", (double) bytes / (1L << 10));
    return bytes > 1 ? bytes + "bytes" : bytes + "byte";
  }

  // ================================================================================
  //  Count related
  // ================================================================================
  public static String humanCount(final long count) {
    if (count >= 1000000000L) return decimalFormat("B", (double) count / 1000000000L);
    if (count >= 1000000) return decimalFormat("M", (double) count / 1000000);
    if (count >= 1000) return decimalFormat("K", (double) count / 1000);
    return Long.toString(count);
  }

  // ================================================================================
  //  Rate related
  // ================================================================================
  public static String humanRate(final double rate) {
    if (rate >= 1000000000000.0) return decimalFormat("T/sec", rate / 1000000000000.0);
    if (rate >= 1000000000.0) return decimalFormat("G/sec", rate / 1000000000.0);
    if (rate >= 1000000.0) return decimalFormat("M/sec", rate / 1000000.0);
    if (rate >= 1000.0) return decimalFormat("K/sec", rate / 1000.0f);
    return decimalFormat("/sec", rate);
  }

  public static String humanRate(final long count, final long duration, final TimeUnit unit) {
    final double sec = unit.toNanos(duration) / 1000000000.0;
    return humanRate(count / sec);
  }

  // ================================================================================
  //  Percentage related
  // ================================================================================
  public static String humanPercent(final long value) {
    return value + "%";
  }

  // ================================================================================
  //  Time related
  // ================================================================================
  public static String humanTimeSince(final long timeNano) {
    return humanTime(System.nanoTime() - timeNano, TimeUnit.NANOSECONDS);
  }

  public static String humanTimeNanos(final long timeNs) {
    if (timeNs < 1000) return (timeNs < 0) ? "unkown" : timeNs + "ns";
    return humanTimeMicros(timeNs / 1000);
  }

  public static String humanTimeMicros(final long timeUs) {
    if (timeUs < 1000) return (timeUs < 0) ? "unkown" : timeUs + "us";
    return humanTimeMillis(timeUs / 1000);
  }

  public static String humanTimeMillis(final long timeDiff) {
    return humanTime(timeDiff, TimeUnit.MILLISECONDS);
  }

  public static String humanTimeSeconds(final long timeDiff) {
    return humanTime(timeDiff, TimeUnit.SECONDS);
  }

  public static String humanTime(final long timeDiff, final TimeUnit unit) {
    final long msec = unit.toMillis(timeDiff);
    if (msec == 0) {
      final long micros = unit.toMicros(timeDiff);
      if (micros > 0) return micros + "us";
      return unit.toNanos(timeDiff) + "ns";
    }

    if (msec < 1000) {
      return msec + "ms";
    }

    final long hours = msec / (60 * 60 * 1000);
    long rem = (msec % (60 * 60 * 1000));
    final long minutes = rem / (60 * 1000);
    rem = rem % (60 * 1000);
    final float seconds = rem / 1000.0f;

    if ((hours > 0) || (minutes > 0)) {
      final StringBuilder buf = new StringBuilder(32);
      if (hours > 0) {
        buf.append(hours);
        buf.append("hrs, ");
      }
      if (minutes > 0) {
        buf.append(minutes);
        buf.append("min, ");
      }

      final String humanTime;
      if (seconds > 0) {
        buf.append(decimalFormat("sec", seconds));
        humanTime = buf.toString();
      } else {
        humanTime = buf.substring(0, buf.length() - 2);
      }

      if (hours > 24) {
        return String.format("%s (%.1f days)", humanTime, (hours / 24.0));
      }
      return humanTime;
    }

    return String.format((seconds % 1) != 0 ? "%.4fsec" : "%.0fsec", seconds);
  }

  // ================================================================================
  //  Date related
  // ================================================================================
  public static String humanDateFromEpochMillis(final long timestamp) {
    return Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.UTC).toString();
  }
}
