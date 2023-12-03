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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.matteobertozzi.rednaco.time.RetryUtil.RetryLogic;

public class TestRetryUtil {
  @Test
  public void testFixedRetryNoJitter() {
    final RetryLogic logic = RetryUtil.newFixedRetry(2000, 0);
    Assertions.assertEquals(2000, logic.minWaitIntervalMillis());
    Assertions.assertEquals(2000, logic.maxWaitIntervalMillis());
    for (int i = 0; i < 1000; ++i) {
      Assertions.assertEquals(2000, logic.nextWaitIntervalMillis());
    }
  }

  @Test
  public void testFixedRetryWithJitter() {
    final RetryLogic logic = RetryUtil.newFixedRetry(2000, 500);
    Assertions.assertEquals(1500, logic.minWaitIntervalMillis());
    Assertions.assertEquals(2500, logic.maxWaitIntervalMillis());
    for (int i = 0; i < 1000; ++i) {
      final int v = logic.nextWaitIntervalMillis();
      Assertions.assertTrue(v >= 1500 && v <= 2500, "v:" + v);
    }
  }

  @Test
  public void testExponentialRetry() {
    final RetryLogic logic = RetryUtil.newExponentialRetry(1000, 15_000, 500);
    Assertions.assertEquals(1000, logic.minWaitIntervalMillis());
    Assertions.assertEquals(15_000, logic.maxWaitIntervalMillis());

    for (int i = 0; i < 10; ++i) {
      int v = logic.nextWaitIntervalMillis();
      Assertions.assertTrue(v >= 2000 && v <= 2500);

      v = logic.nextWaitIntervalMillis();
      Assertions.assertTrue(v >= 4000 && v <= 4500);

      v = logic.nextWaitIntervalMillis();
      Assertions.assertTrue(v >= 8000 && v <= 8500);

      v = logic.nextWaitIntervalMillis();
      Assertions.assertTrue(v >= 15_000 && v <= 15_500);

      v = logic.nextWaitIntervalMillis();
      Assertions.assertTrue(v >= 15_000 && v <= 15_500);

      logic.reset();
    }
  }
}
