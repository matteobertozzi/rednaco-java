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

package io.github.matteobertozzi.rednaco.math;

public final class Statistics {
  private Statistics() {
    // no-op
  }

  // ================================================================================
  //  sum related
  // ================================================================================
  public static long sum(final short[] buf, final int off, final int len) {
    if (buf == null || len == 0) return 0;

    long sum = 0;
    for (int i = 0; i < len; ++i) {
      sum += buf[off + i];
    }
    return sum;
  }

  public static long sum(final int[] buf, final int off, final int len) {
    if (buf == null || len == 0) return 0;

    long sum = 0;
    for (int i = 0; i < len; ++i) {
      sum += buf[off + i];
    }
    return sum;
  }

  public static long sum(final long[] buf, final int off, final int len) {
    if (buf == null || len == 0) return 0;

    long sum = 0;
    for (int i = 0; i < len; ++i) {
      sum += buf[off + i];
    }
    return sum;
  }

  public static double sum(final float[] buf, final int off, final int len) {
    if (buf == null || len == 0) return 0;

    double sum = 0;
    for (int i = 0; i < len; ++i) {
      sum += buf[off + i];
    }
    return sum;
  }

  public static double sum(final double[] buf, final int off, final int len) {
    if (buf == null || len == 0) return 0;

    double sum = 0;
    for (int i = 0; i < len; ++i) {
      sum += buf[off + i];
    }
    return sum;
  }

  // ================================================================================
  //  min related
  // ================================================================================
  public static short min(final short[] buf, final int off, final int len) {
    short minValue = buf[off];
    for (int i = 1; i < len; ++i) {
      if (minValue > buf[off + i]) {
        minValue = buf[off + i];
      }
    }
    return minValue;
  }

  public static int min(final int[] buf, final int off, final int len) {
    int minValue = buf[off];
    for (int i = 1; i < len; ++i) {
      if (minValue > buf[off + i]) {
        minValue = buf[off + i];
      }
    }
    return minValue;
  }

  public static long min(final long[] buf, final int off, final int len) {
    long minValue = buf[off];
    for (int i = 1; i < len; ++i) {
      if (minValue > buf[off + i]) {
        minValue = buf[off + i];
      }
    }
    return minValue;
  }

  public static float min(final float[] buf, final int off, final int len) {
    float minValue = buf[off];
    for (int i = 1; i < len; ++i) {
      if (minValue > buf[off + i]) {
        minValue = buf[off + i];
      }
    }
    return minValue;
  }

  public static double min(final double[] buf, final int off, final int len) {
    double minValue = buf[off];
    for (int i = 1; i < len; ++i) {
      if (minValue > buf[off + i]) {
        minValue = buf[off + i];
      }
    }
    return minValue;
  }

  // ================================================================================
  //  max related
  // ================================================================================
  public static short max(final short[] buf, final int off, final int len) {
    short maxValue = buf[off];
    for (int i = 1; i < len; ++i) {
      if (maxValue < buf[off + i]) {
        maxValue = buf[off + i];
      }
    }
    return maxValue;
  }

  public static int max(final int[] buf, final int off, final int len) {
    int maxValue = buf[off];
    for (int i = 1; i < len; ++i) {
      if (maxValue < buf[off + i]) {
        maxValue = buf[off + i];
      }
    }
    return maxValue;
  }

  public static long max(final long[] buf, final int off, final int len) {
    long maxValue = buf[off];
    for (int i = 1; i < len; ++i) {
      if (maxValue < buf[off + i]) {
        maxValue = buf[off + i];
      }
    }
    return maxValue;
  }

  public static float max(final float[] buf, final int off, final int len) {
    float maxValue = buf[off];
    for (int i = 1; i < len; ++i) {
      if (maxValue < buf[off + i]) {
        maxValue = buf[off + i];
      }
    }
    return maxValue;
  }

  public static double max(final double[] buf, final int off, final int len) {
    double maxValue = buf[off];
    for (int i = 1; i < len; ++i) {
      if (maxValue < buf[off + i]) {
        maxValue = buf[off + i];
      }
    }
    return maxValue;
  }

  // ================================================================================
  //  mean/average
  // ================================================================================
  public static double average(final long numEvents, final double sum) {
    return numEvents != 0 ? sum / numEvents : 0;
  }

  public static double average(final int[] buf, final int off, final int len) {
    if (buf == null || len == 0) return 0;
    return (double)sum(buf, off, len) / len;
  }

  public static double average(final long[] buf, final int off, final int len) {
    if (buf == null || len == 0) return 0;
    return (double)sum(buf, off, len) / len;
  }

  public static double average(final float[] buf, final int off, final int len) {
    if (buf == null || len == 0) return 0;
    return sum(buf, off, len) / len;
  }

  public static double average(final double[] buf, final int off, final int len) {
    if (buf == null || len == 0) return 0;
    return sum(buf, off, len) / len;
  }

  // ================================================================================
  //  variance related
  // ================================================================================
  public static double variance(final long numEvents, final double sum, final double sumSquares) {
    if (numEvents == 0) return 0;
    return (sumSquares * numEvents - sum * sum) / (numEvents * numEvents);
  }

  public static double variance(final int[] buf, final int off, final int len) {
    if (buf == null || len == 0) return 0;

    double sumSquare = 0;
    final double avg = average(buf, off, len);
    for (int i = 0; i < len; ++i) {
      sumSquare += Math.pow(avg - buf[off + i], 2);
    }
    return sumSquare / len;
  }

  public static double variance(final long[] buf, final int off, final int len) {
    if (buf == null || len == 0) return 0;

    double sumSquare = 0;
    final double avg = average(buf, off, len);
    for (int i = 0; i < len; ++i) {
      sumSquare += Math.pow(avg - buf[off + i], 2);
    }
    return sumSquare / len;
  }

  public static double variance(final float[] buf, final int off, final int len) {
    if (buf == null || len == 0) return 0;

    double sumSquare = 0;
    final double avg = average(buf, off, len);
    for (int i = 0; i < len; ++i) {
      sumSquare += Math.pow(avg - buf[off + i], 2);
    }
    return sumSquare / len;
  }

  public static double variance(final double[] buf, final int off, final int len) {
    if (buf == null || len == 0) return 0;

    double sumSquare = 0;
    final double avg = average(buf, off, len);
    for (int i = 0; i < len; ++i) {
      sumSquare += Math.pow(avg - buf[off + i], 2);
    }
    return sumSquare / len;
  }

  // ================================================================================
  //  standard deviation related
  // ================================================================================
  public static double standardDeviation(final long numEvents, final double sum, final double sumSquares) {
    if (numEvents == 0) return 0;
    return Math.sqrt(Math.max(variance(numEvents, sum, sumSquares), 0.0));
  }

  public static double standardDeviation(final int[] buf, final int off, final int len) {
    if (buf == null || len == 0) return 0;
    return Math.sqrt(Math.max(variance(buf, off, len), 0.0));
  }

  public static double standardDeviation(final long[] buf, final int off, final int len) {
    if (buf == null || len == 0) return 0;
    return Math.sqrt(Math.max(variance(buf, off, len), 0.0));
  }

  public static double standardDeviation(final float[] buf, final int off, final int len) {
    if (buf == null || len == 0) return 0;
    return Math.sqrt(Math.max(variance(buf, off, len), 0.0));
  }

  public static double standardDeviation(final double[] buf, final int off, final int len) {
    if (buf == null || len == 0) return 0;
    return Math.sqrt(Math.max(variance(buf, off, len), 0.0));
  }

  // ================================================================================
  //  percentile related
  // ================================================================================
  public static double percentile(final double p, final long[] bounds, final long minValue, final long maxValue,
      final long numEvents, final long[] events, final int eventOff) {
    if (numEvents == 0) return 0;

    final double threshold = numEvents * (p / 100.0);
    long cumulativeSum = 0;
    for (int b = 0; b < bounds.length; b++) {
      final long bucketValue = events[eventOff + b];
      cumulativeSum += bucketValue;
      if (cumulativeSum >= threshold) {
        // Scale linearly within this bucket
        final long leftPoint = (b == 0) ? minValue : bounds[b - 1];
        final long rightPoint = bounds[b];
        final long leftSum = cumulativeSum - bucketValue;
        double pos = 0;
        final long rightLeftDiff = cumulativeSum - leftSum;
        if (rightLeftDiff != 0) {
          pos = (threshold - leftSum) / rightLeftDiff;
        }
        double r = leftPoint + (rightPoint - leftPoint) * pos;
        if (r < minValue) r = minValue;
        if (r > maxValue) r = maxValue;
        return r;
      }
    }
    return maxValue;
  }
}
