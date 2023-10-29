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
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import io.github.matteobertozzi.rednaco.time.TimeUtil.ClockProvider;
import io.github.matteobertozzi.rednaco.time.TimeUtil.ManualClockProvider;

@Execution(ExecutionMode.SAME_THREAD)
public class TestTimeUtil {
  @Test
  public void testAlignToWindow() {
    Assertions.assertEquals(1348563447000L, TimeUtil.alignToWindow(1348563447123L, 1_000));
    Assertions.assertEquals(1348563445000L, TimeUtil.alignToWindow(1348563447123L, 5_000));
    Assertions.assertEquals(1348563440000L, TimeUtil.alignToWindow(1348563447123L, 10_000));
    Assertions.assertEquals(1348563420000L, TimeUtil.alignToWindow(1348563447123L, 30_000));
    Assertions.assertEquals(1348563420000L, TimeUtil.alignToWindow(1348563447123L, 60_000));
    Assertions.assertEquals(1348563300000L, TimeUtil.alignToWindow(1348563447123L, 5 * 60_000));
    Assertions.assertEquals(1348562700000L, TimeUtil.alignToWindow(1348563447123L, 15 * 60_000));
    Assertions.assertEquals(1348561800000L, TimeUtil.alignToWindow(1348563447123L, 30 * 60_000));
    Assertions.assertEquals(1348560000000L, TimeUtil.alignToWindow(1348563447123L, 60 * 60_000));
    Assertions.assertEquals(1348531200000L, TimeUtil.alignToWindow(1348563447123L, 24 * 60 * 60_000));
  }

  @Test
  public void testManualClockProvider() {
    final ClockProvider clockProvider = TimeUtil.getClockProvider();
    try {
      final ManualClockProvider manualClock = new ManualClockProvider(0);
      TimeUtil.setClockProvider(manualClock);
      Assertions.assertEquals(0L, manualClock.epochMillis());
      Assertions.assertEquals(0L, manualClock.epochNanos());

      manualClock.incTime(15, TimeUnit.SECONDS);
      Assertions.assertEquals(15000L, manualClock.epochMillis());
      Assertions.assertEquals(15000000000L, manualClock.epochNanos());

      manualClock.incTime(Duration.ofMinutes(5));
      Assertions.assertEquals(315000L, manualClock.epochMillis());
      Assertions.assertEquals(315000000000L, manualClock.epochNanos());

      manualClock.decTime(Duration.ofSeconds(17));
      Assertions.assertEquals(298000L, manualClock.epochMillis());
      Assertions.assertEquals(298000000000L, manualClock.epochNanos());

      manualClock.decTime(2, TimeUnit.MINUTES);
      Assertions.assertEquals(178000L, manualClock.epochMillis());
      Assertions.assertEquals(178000000000L, manualClock.epochNanos());

      manualClock.decTime(123, TimeUnit.NANOSECONDS);
      Assertions.assertEquals(177999L, manualClock.epochMillis());
      Assertions.assertEquals(177999999877L, manualClock.epochNanos());

      manualClock.setTime(1234, TimeUnit.NANOSECONDS);
      Assertions.assertEquals(0L, manualClock.epochMillis());
      Assertions.assertEquals(1234L, manualClock.epochNanos());

      manualClock.reset();
      Assertions.assertEquals(0L, manualClock.epochMillis());
      Assertions.assertEquals(0L, manualClock.epochNanos());
    } finally {
      TimeUtil.setClockProvider(clockProvider);
    }
  }
}
