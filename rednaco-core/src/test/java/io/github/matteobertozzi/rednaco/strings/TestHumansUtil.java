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

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestHumansUtil {
  @Test
  public void testHumansBits() {
    Assertions.assertEquals("3.00Ebit", HumansUtil.humanBits(3000_000_000_000_000_000L));
    Assertions.assertEquals("3.04Ebit", HumansUtil.humanBits(3040_000_000_000_000_000L));
    Assertions.assertEquals("3.00Pbit", HumansUtil.humanBits(3000_000_000_000_000L));
    Assertions.assertEquals("3.00Tbit", HumansUtil.humanBits(3000_000_000_000L));
    Assertions.assertEquals("3.00Gbit", HumansUtil.humanBits(3000_000_000L));
    Assertions.assertEquals("3.00Mbit", HumansUtil.humanBits(3000_000L));
    Assertions.assertEquals("3.00Kbit", HumansUtil.humanBits(3000L));
    Assertions.assertEquals("3.42Kbit", HumansUtil.humanBits(3420L));
    Assertions.assertEquals("5bit", HumansUtil.humanBits(5));
    Assertions.assertEquals("0bit", HumansUtil.humanBits(0));
  }

  @Test
  public void testHumansBytes() {
    Assertions.assertEquals("3.00EiB", HumansUtil.humanBytes(3L << 60));
    Assertions.assertEquals("3.00PiB", HumansUtil.humanBytes(3L << 50));
    Assertions.assertEquals("3.00TiB", HumansUtil.humanBytes(3L << 40));
    Assertions.assertEquals("3.00GiB", HumansUtil.humanBytes(3L << 30));
    Assertions.assertEquals("3.00MiB", HumansUtil.humanBytes(3L << 20));
    Assertions.assertEquals("3.00KiB", HumansUtil.humanBytes(3L << 10));
    Assertions.assertEquals("3.34KiB", HumansUtil.humanBytes(3420L));
    Assertions.assertEquals("5bytes", HumansUtil.humanBytes(5));
    Assertions.assertEquals("1byte", HumansUtil.humanBytes(1));
    Assertions.assertEquals("0byte", HumansUtil.humanBytes(0));
  }

  @Test
  public void testHumansRate() {
    Assertions.assertEquals("73.54T/sec", HumansUtil.humanRate(73_540_000_000_000L, 1, TimeUnit.SECONDS));
    Assertions.assertEquals("53.54G/sec", HumansUtil.humanRate(53_540_000_000L, 1, TimeUnit.SECONDS));
    Assertions.assertEquals("42.54M/sec", HumansUtil.humanRate(42_540_000L, 1, TimeUnit.SECONDS));
    Assertions.assertEquals("12.00K/sec", HumansUtil.humanRate(12_000, 1, TimeUnit.SECONDS));
    Assertions.assertEquals("10.00/sec", HumansUtil.humanRate(10, 1, TimeUnit.SECONDS));
    Assertions.assertEquals("0.00/sec", HumansUtil.humanRate(0, 1, TimeUnit.SECONDS));
  }

  @Test
  public void testHumansPercentage() {
    Assertions.assertEquals("10%", HumansUtil.humanPercent(10));
  }

  @Test
  public void testHumansCount() {
    Assertions.assertEquals("21.30B", HumansUtil.humanCount(21_300_000_000L));
    Assertions.assertEquals("1.00B", HumansUtil.humanCount(1_000_000_000L));
    Assertions.assertEquals("15.23M", HumansUtil.humanCount(15_230_000));
    Assertions.assertEquals("1.00M", HumansUtil.humanCount(1_000_000));
    Assertions.assertEquals("10.38K", HumansUtil.humanCount(10_380));
    Assertions.assertEquals("1.08K", HumansUtil.humanCount(1080));
    Assertions.assertEquals("1.00K", HumansUtil.humanCount(1000));
    Assertions.assertEquals("5", HumansUtil.humanCount(5));
    Assertions.assertEquals("0", HumansUtil.humanCount(0));
  }

  @Test
  public void testHumansTime() {
    Assertions.assertEquals("5sec", HumansUtil.humanTimeSeconds(5));
    Assertions.assertEquals("52ms", HumansUtil.humanTimeMillis(52));
    Assertions.assertEquals("352ms", HumansUtil.humanTimeMillis(352));
    Assertions.assertEquals("4.3520sec", HumansUtil.humanTimeMillis(4352));
    Assertions.assertEquals("7min, 4.35sec", HumansUtil.humanTimeMillis(7 * 60_000 + 4352));
    Assertions.assertEquals("3hrs, 4.35sec", HumansUtil.humanTimeMillis((3 * 60 * 60_000) + 4352));
    Assertions.assertEquals("3hrs, 7min, 4.35sec", HumansUtil.humanTimeMillis((3 * 60 * 60_000) + (7 * 60_000) + 4352));
    Assertions.assertEquals("24hrs, 7min, 4.35sec", HumansUtil.humanTimeMillis((24 * 60 * 60_000) + (7 * 60_000) + 4352));
    Assertions.assertEquals("48hrs, 7min, 4.35sec (2.0 days)", HumansUtil.humanTimeMillis((48 * 60 * 60_000) + (7 * 60_000) + 4352));
  }
}
