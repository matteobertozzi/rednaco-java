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

package io.github.matteobertozzi.rednaco.dispatcher.routing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestRoutePathUtil {
  @Test
  public void testCleanPath() {
    final String[] testVec = {
      // Already clean
      "/abc/def", "/abc/def",
      "/a/b/c", "/a/b/c",
      "/abc", "/abc",
      "/", "/",

      // Remove trailing slash
      "/abc/", "/abc",
      "/abc/def/", "/abc/def",
      "/a/b/c/", "/a/b/c",
      "/./", "/",
      "/abc/", "/abc",

      // Remove doubled slash
      "/abc//def//ghi", "/abc/def/ghi",
      "//abc", "/abc",
      "///abc", "/abc",
      "//abc//", "/abc",
      "/abc//", "/abc",

      // Remove . elements
      "/abc/./def", "/abc/def",
      "/./abc/def", "/abc/def",
      "/abc/.", "/abc",

      // Remove .. elements
      "/abc/def/ghi/../jkl", "/abc/def/jkl",
      "/abc/def/../ghi/../jkl", "/abc/jkl",
      "/abc/def/..", "/abc",
      "/abc/def/../..", "/",
      "/abc/def/../..", "/",
      "/abc/def/../ghi/jkl/../../../mno", "/mno",

      // Combinations
      "/abc/./../def", "/def",
      "/abc//./../def", "/def",
    };
    for (int i = 0, n = testVec.length - 1; i < n; i += 2) {
      Assertions.assertEquals(testVec[i + 1], RoutePathUtil.cleanPath(testVec[i]));
    }
  }
}
