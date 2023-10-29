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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestStatistics {
  @Test
  public void testIntSum() {
    Assertions.assertEquals(0, Statistics.sum(new short[0], 0, 0));
    Assertions.assertEquals(0, Statistics.sum(new int[0], 0, 0));
    Assertions.assertEquals(0, Statistics.sum(new long[0], 0, 0));
    Assertions.assertEquals(0, Statistics.sum(new short[] { 1, 2, 3 }, 0, 0));
    Assertions.assertEquals(0, Statistics.sum(new short[] { 1, 2, 3 }, 2, 0));
    Assertions.assertEquals(0, Statistics.sum(new int[] { 1, 2, 3 }, 0, 0));
    Assertions.assertEquals(0, Statistics.sum(new int[] { 1, 2, 3 }, 2, 0));
    Assertions.assertEquals(0, Statistics.sum(new long[] { 1, 2, 3 }, 0, 0));
    Assertions.assertEquals(0, Statistics.sum(new long[] { 1, 2, 3 }, 2, 0));

    // positive numbers
    Assertions.assertEquals(15, Statistics.sum(new short[] { 1, 2, 3, 4, 5 }, 0, 5));
    Assertions.assertEquals(15, Statistics.sum(new int[]   { 1, 2, 3, 4, 5 }, 0, 5));
    Assertions.assertEquals(15, Statistics.sum(new long[]  { 1, 2, 3, 4, 5 }, 0, 5));
    Assertions.assertEquals(7, Statistics.sum(new short[] { 1, 2, 3, 4, 5 }, 2, 2));
    Assertions.assertEquals(7, Statistics.sum(new int[]   { 1, 2, 3, 4, 5 }, 2, 2));
    Assertions.assertEquals(7, Statistics.sum(new long[]  { 1, 2, 3, 4, 5 }, 2, 2));

    // negative numbers
    Assertions.assertEquals(-15, Statistics.sum(new short[] { -1, -2, -3, -4, -5 }, 0, 5));
    Assertions.assertEquals(-15, Statistics.sum(new int[]   { -1, -2, -3, -4, -5 }, 0, 5));
    Assertions.assertEquals(-15, Statistics.sum(new long[]  { -1, -2, -3, -4, -5 }, 0, 5));
    Assertions.assertEquals(-7, Statistics.sum(new short[] { -1, -2, -3, -4, -5 }, 2, 2));
    Assertions.assertEquals(-7, Statistics.sum(new int[]   { -1, -2, -3, -4, -5 }, 2, 2));
    Assertions.assertEquals(-7, Statistics.sum(new long[]  { -1, -2, -3, -4, -5 }, 2, 2));
  }

  @Test
  public void testFloatSum() {
    Assertions.assertEquals(0.0f, Statistics.sum(new float[0], 0, 0), 0.0001);
    Assertions.assertEquals(0.0, Statistics.sum(new double[0], 0, 0), 0.0001);
    Assertions.assertEquals(0.0f, Statistics.sum(new float[] { 0.1f, 0.2f, 0.3f }, 0, 0), 0.0001);
    Assertions.assertEquals(0.0, Statistics.sum(new double[] { 0.1,  0.2,  0.3  }, 0, 0), 0.0001);
    Assertions.assertEquals(0.0f, Statistics.sum(new float[] { 0.1f, 0.2f, 0.3f }, 2, 0), 0.0001);
    Assertions.assertEquals(0.0, Statistics.sum(new double[] { 0.1,  0.2,  0.3  }, 2, 0), 0.0001);

    Assertions.assertEquals(1.5f, Statistics.sum(new float[]  { 0.1f, 0.2f, 0.3f, 0.4f, 0.5f }, 0, 5), 0.0001);
    Assertions.assertEquals(1.5, Statistics.sum(new double[]  { 0.1,  0.2,  0.3,  0.4,  0.5  }, 0, 5), 0.0001);

    Assertions.assertEquals(0.7f, Statistics.sum(new float[]  { 0.1f, 0.2f, 0.3f, 0.4f, 0.5f }, 2, 2), 0.0001);
    Assertions.assertEquals(0.7, Statistics.sum(new double[]  { 0.1,  0.2,  0.3,  0.4,  0.5  }, 2, 2), 0.0001);
  }

  @Test
  public void testAverage() {
    Assertions.assertEquals(86.6, Statistics.average(5, 433), 0.0001);

    Assertions.assertEquals(0, Statistics.average(new int[] { 90, 85, 88, 92, 78 }, 0, 0));

    Assertions.assertEquals(86.6, Statistics.average(new int[] { 90, 85, 88, 92, 78 }, 0, 5), 0.0001);
    Assertions.assertEquals(86.6, Statistics.average(new long[] { 90, 85, 88, 92, 78 }, 0, 5), 0.0001);
    Assertions.assertEquals(86.6, Statistics.average(new float[] { 90, 85, 88, 92, 78 }, 0, 5), 0.0001);
    Assertions.assertEquals(86.6, Statistics.average(new double[] { 90, 85, 88, 92, 78 }, 0, 5), 0.0001);
  }

  @Test
  public void testVariance() {
    Assertions.assertEquals(23.84, Statistics.variance(5, 433, 37617), 0.0001);

    Assertions.assertEquals(0, Statistics.variance(new int[] { 90, 85, 88, 92, 78 }, 0, 0));

    Assertions.assertEquals(23.84, Statistics.variance(new int[] { 90, 85, 88, 92, 78 }, 0, 5), 0.0001);
    Assertions.assertEquals(23.84, Statistics.variance(new long[] { 90, 85, 88, 92, 78 }, 0, 5), 0.0001);
    Assertions.assertEquals(23.84, Statistics.variance(new float[] { 90, 85, 88, 92, 78 }, 0, 5), 0.0001);
    Assertions.assertEquals(23.84, Statistics.variance(new double[] { 90, 85, 88, 92, 78 }, 0, 5), 0.0001);
  }

  @Test
  public void testStdDev() {
    Assertions.assertEquals(4.8826, Statistics.standardDeviation(5, 433, 37617), 0.0001);

    Assertions.assertEquals(4.8826, Statistics.standardDeviation(new int[] { 90, 85, 88, 92, 78 }, 0, 5), 0.0001);
    Assertions.assertEquals(4.8826, Statistics.standardDeviation(new long[] { 90, 85, 88, 92, 78 }, 0, 5), 0.0001);
    Assertions.assertEquals(4.8826, Statistics.standardDeviation(new float[] { 90, 85, 88, 92, 78 }, 0, 5), 0.0001);
    Assertions.assertEquals(4.8826, Statistics.standardDeviation(new double[] { 90, 85, 88, 92, 78 }, 0, 5), 0.0001);
  }
}
