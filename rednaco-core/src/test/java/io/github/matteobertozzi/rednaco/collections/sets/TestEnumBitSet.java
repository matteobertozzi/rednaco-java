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

package io.github.matteobertozzi.rednaco.collections.sets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestEnumBitSet {
  private enum TestEnum {
    TEST_0, TEST_1, TEST_2, TEST_3, TEST_4, TEST_5, TEST_6, TEST_7, TEST_8,
    TEST_9, TEST_10, TEST_11, TEST_12, TEST_13, TEST_14, TEST_15, TEST_16,
    TEST_17, TEST_18, TEST_19, TEST_20, TEST_21, TEST_22, TEST_23, TEST_24,
    TEST_25, TEST_26, TEST_27, TEST_28, TEST_29, TEST_30, TEST_31, TEST_32,
    TEST_33, TEST_34, TEST_35, TEST_36, TEST_37, TEST_38, TEST_39, TEST_40,
    TEST_41, TEST_42, TEST_43, TEST_44, TEST_45, TEST_46, TEST_47, TEST_48,
    TEST_49, TEST_50, TEST_51, TEST_52, TEST_53, TEST_54, TEST_55, TEST_56,
    TEST_57, TEST_58, TEST_59, TEST_60, TEST_61, TEST_62, TEST_63, TEST_64,
    TEST_65, TEST_66, TEST_67, TEST_68, TEST_69, TEST_70, TEST_71, TEST_72,
    TEST_73, TEST_74, TEST_75, TEST_76, TEST_77, TEST_78, TEST_79, TEST_80,
    TEST_81, TEST_82, TEST_83, TEST_84, TEST_85, TEST_86, TEST_87, TEST_88,
    TEST_89, TEST_90, TEST_91, TEST_92, TEST_93, TEST_94, TEST_95, TEST_96,
    TEST_97, TEST_98, TEST_99, TEST_100, TEST_101, TEST_102, TEST_103, TEST_104,
    TEST_105, TEST_106, TEST_107, TEST_108, TEST_109, TEST_110, TEST_111, TEST_112,
    TEST_113, TEST_114, TEST_115, TEST_116, TEST_117, TEST_118, TEST_119, TEST_120,
    TEST_121, TEST_122, TEST_123, TEST_124, TEST_125, TEST_126, TEST_127, TEST_128,
    TEST_129, TEST_130, TEST_131, TEST_132, TEST_133, TEST_134, TEST_135, TEST_136,
    TEST_137, TEST_138, TEST_139, TEST_140, TEST_141, TEST_142, TEST_143, TEST_144,
    TEST_145, TEST_146, TEST_147, TEST_148, TEST_149, TEST_150, TEST_151, TEST_152,
    TEST_153, TEST_154, TEST_155, TEST_156, TEST_157, TEST_158, TEST_159, TEST_160,
    TEST_161, TEST_162, TEST_163, TEST_164, TEST_165, TEST_166, TEST_167, TEST_168,
  }

  @Test
  public void testBitSet() {
    final EnumBitSet<TestEnum> enumBitSet = new EnumBitSet<>(TestEnum.class);
    final TestEnum[] universe = TestEnum.class.getEnumConstants();
    verifySetGet(enumBitSet, universe);
  }

  @Test
  public void testBitSetSubUniverse() {
    final TestEnum[] universe = new TestEnum[] {
      TestEnum.TEST_33,
      TestEnum.TEST_20,
      TestEnum.TEST_47,
      TestEnum.TEST_12,
      TestEnum.TEST_54,
    };
    final EnumBitSet<TestEnum> enumBitSet = new EnumBitSet<>(universe);
    verifySetGet(enumBitSet, universe);
  }

  private static <T extends Enum<T>> void verifySetGet(final EnumBitSet<T> enumBitSet, final T[] universe) {
    assertIsEmpty(enumBitSet, universe);

    for (int i = 0; i < universe.length; i += 2) {
      enumBitSet.set(universe[i]);
    }

    for (int i = 0; i < universe.length - 1;) {
      Assertions.assertTrue(enumBitSet.get(universe[i++]));
      Assertions.assertFalse(enumBitSet.get(universe[i++]));
    }
    Assertions.assertFalse(enumBitSet.isEmpty());

    enumBitSet.clear();
    assertIsEmpty(enumBitSet, universe);

    for (int i = 1; i < universe.length; i += 2) {
      enumBitSet.set(universe[i]);
    }

    for (int i = 0; i < universe.length - 1;) {
      Assertions.assertFalse(enumBitSet.get(universe[i++]));
      Assertions.assertTrue(enumBitSet.get(universe[i++]));
    }
    Assertions.assertFalse(enumBitSet.isEmpty());
  }

  private static <T extends Enum<T>> void assertIsEmpty(final EnumBitSet<T> enumBitSet, final T[] universe) {
    Assertions.assertTrue(enumBitSet.isEmpty());
    for (int i = 0; i < universe.length; ++i) {
      Assertions.assertFalse(enumBitSet.get(universe[i]));
    }
  }
}
