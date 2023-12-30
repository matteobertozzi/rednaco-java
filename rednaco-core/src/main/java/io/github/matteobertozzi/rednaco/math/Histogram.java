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

import java.util.Arrays;

public class Histogram {
  private final long[] bounds;
  private final long[] events;
  private long minValue;
  private long maxValue;
  private long sumSquares;
  private long sum;

  public Histogram(final long[] bounds) {
    this.bounds = bounds;
    this.events = new long[bounds.length + 1];
    this.minValue = Long.MAX_VALUE;
    this.maxValue = Long.MIN_VALUE;
    this.sumSquares = 0;
    this.sum = 0;
  }

  public void reset() {
    Arrays.fill(events, 0);
    this.minValue = Long.MAX_VALUE;
    this.maxValue = Long.MIN_VALUE;
    this.sumSquares = 0;
    this.sum = 0;
  }

  public void add(final long value) {
    final int boundIndex = findBoundIndex(value);
    events[boundIndex]++;
    minValue = Math.min(minValue, value);
    maxValue = Math.max(maxValue, value);
    sumSquares += (value * value);
    sum += value;
  }

  private int findBoundIndex(final long value) {
    for (int i = 0; i < bounds.length; ++i) {
      if (value < bounds[i]) {
        return i;
      }
    }
    return bounds.length;
  }

  public long numEvents() {
    return Statistics.sum(events, 0, events.length);
  }

  public long bounds(final int index) {
    return bounds[index];
  }

  public long events(final int index) {
    return events[index];
  }

  public long sum() {
    return sum;
  }

  public long minValue() {
    return minValue;
  }

  public long maxValue() {
    return maxValue;
  }

  public double average() {
    return Statistics.average(numEvents(), sum);
  }

  public double standardDeviation() {
    return Statistics.standardDeviation(numEvents(), sum, sumSquares);
  }

  public double median() { return percentile(50.0); }

  public double percentile(final double p) {
    return Statistics.percentile(p, bounds, minValue, maxValue, numEvents(), events, 0);
  }
}
