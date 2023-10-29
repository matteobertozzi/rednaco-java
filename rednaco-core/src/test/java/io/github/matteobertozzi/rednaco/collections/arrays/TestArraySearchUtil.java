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

package io.github.matteobertozzi.rednaco.collections.arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestArraySearchUtil {
  @Test
  public void testIntBinarySearch() {
    final int[] stripedVec = new int[] { 10, 1, 20, 2, 30, 3, 40, 4 };

    Assertions.assertEquals(0, -(ArraySearchUtil.binarySearch(stripedVec, 0, stripedVec.length / 2, 2, 5)) - 1);
    Assertions.assertEquals(-1, ArraySearchUtil.binarySearch(stripedVec, 0, stripedVec.length / 2, 2, 2));
    Assertions.assertEquals(0, ArraySearchUtil.binarySearch(stripedVec, 0, stripedVec.length / 2, 2, 10));
    Assertions.assertEquals(1, ArraySearchUtil.binarySearch(stripedVec, 0, stripedVec.length / 2, 2, 20));
    Assertions.assertEquals(2, ArraySearchUtil.binarySearch(stripedVec, 0, stripedVec.length / 2, 2, 30));
    Assertions.assertEquals(3, ArraySearchUtil.binarySearch(stripedVec, 0, stripedVec.length / 2, 2, 40));
    Assertions.assertEquals(4, -(ArraySearchUtil.binarySearch(stripedVec, 0, stripedVec.length / 2, 2, 50)) - 1);

    Assertions.assertEquals(-1, ArraySearchUtil.binarySearch(stripedVec, 2, ((stripedVec.length - 2) / 2), 2, 10));
    Assertions.assertEquals(0, ArraySearchUtil.binarySearch(stripedVec, 2, ((stripedVec.length - 2) / 2), 2, 20));
    Assertions.assertEquals(1, ArraySearchUtil.binarySearch(stripedVec, 2, ((stripedVec.length - 2) / 2), 2, 30));
    Assertions.assertEquals(2, ArraySearchUtil.binarySearch(stripedVec, 2, ((stripedVec.length - 2) / 2), 2, 40));
    Assertions.assertEquals(-4, ArraySearchUtil.binarySearch(stripedVec, 2, ((stripedVec.length - 2) / 2), 2, 50));
  }

  @Test
  public void testCustomBinarySearch() {
    final Object[] stripedVec = new Object[] { "bbb", 10, "ddd", 20, "fff", 30, "hhh", 40 };

    Assertions.assertEquals(0, -(ArraySearchUtil.binarySearch(0, stripedVec.length / 2, index -> "aaa".compareTo((String)stripedVec[index * 2]))) - 1);
    Assertions.assertEquals(0, ArraySearchUtil.binarySearch(0, stripedVec.length / 2, index -> "bbb".compareTo((String)stripedVec[index * 2])));
    Assertions.assertEquals(1, ArraySearchUtil.binarySearch(0, stripedVec.length / 2, index -> "ddd".compareTo((String)stripedVec[index * 2])));
    Assertions.assertEquals(2, ArraySearchUtil.binarySearch(0, stripedVec.length / 2, index -> "fff".compareTo((String)stripedVec[index * 2])));
    Assertions.assertEquals(3, ArraySearchUtil.binarySearch(0, stripedVec.length / 2, index -> "hhh".compareTo((String)stripedVec[index * 2])));
    Assertions.assertEquals(4, -(ArraySearchUtil.binarySearch(0, stripedVec.length / 2, index -> "iii".compareTo((String)stripedVec[index * 2]))) - 1);
  }
}
