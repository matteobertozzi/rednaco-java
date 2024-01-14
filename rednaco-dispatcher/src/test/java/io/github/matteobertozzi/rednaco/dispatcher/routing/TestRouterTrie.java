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

import java.util.Map;
import java.util.regex.Matcher;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.matteobertozzi.rednaco.dispatcher.MessageExecutor.ExecutionType;
import io.github.matteobertozzi.rednaco.dispatcher.routing.RoutePathUtil.RouterPathSpec;
import io.github.matteobertozzi.rednaco.dispatcher.routing.RoutesMapping.DirectRouteMapping;
import io.github.matteobertozzi.rednaco.dispatcher.routing.RoutesMapping.PatternRouteMapping;
import io.github.matteobertozzi.rednaco.dispatcher.routing.RoutesMapping.RouteMatcher;

public class TestRouterTrie {
  @Test
  public void testSplitInternal() {
    final RouterTrie router = new RouterTrie();
    routerPut(router, RoutePathUtil.parsePath("/api/v1/namespaces"));
    routerPut(router, RoutePathUtil.parsePathWithVariables("/api/v1/namespaces/{namespace}/bbb"));
    routerPut(router, RoutePathUtil.parsePathWithVariables("/api/v1/namespaces/{namespace}/ccc"));
    routerPut(router, RoutePathUtil.parsePathWithVariables("/api/v1/namespaces/{namespace}/ddd"));

    Assertions.assertNotNull(router.get(RoutePathUtil.cleanPath("/api/v1/namespaces")));
    assertPattern(Map.of("namespace", "foo"), router.get(RoutePathUtil.cleanPath("/api/v1/namespaces/foo/bbb")));
    assertPattern(Map.of("namespace", "foo"), router.get(RoutePathUtil.cleanPath("/api/v1/namespaces/foo/bbb")));
    assertPattern(Map.of("namespace", "foo"), router.get(RoutePathUtil.cleanPath("/api/v1/namespaces/foo/ccc")));
    assertPattern(Map.of("namespace", "foo"), router.get(RoutePathUtil.cleanPath("/api/v1/namespaces/foo/ddd")));
    Assertions.assertNull(router.get(RoutePathUtil.cleanPath("/api/v1/namespaces/foo/eee")));
  }

  @Test
  public void testNewLeaf() {
    final RouterTrie router = new RouterTrie();
    routerPut(router, RoutePathUtil.parsePath("/apis/apps/v1/ccc"));
    routerPut(router, RoutePathUtil.parsePath("/apis/apps/v1/ddd"));
    routerPut(router, RoutePathUtil.parsePathWithVariables("/apis/apps/v1/namespaces/{namespace}/ccc"));

    Assertions.assertNotNull(router.get(RoutePathUtil.cleanPath("/apis/apps/v1/ccc")));
    Assertions.assertNotNull(router.get(RoutePathUtil.cleanPath("/apis/apps/v1/ddd")));
    assertPattern(Map.of("namespace", "foo"), router.get(RoutePathUtil.cleanPath("/apis/apps/v1/namespaces/foo/ccc")));

    routerPut(router, RoutePathUtil.parsePathWithVariables("/apis/apps/v1/namespaces/{namespace}/ccc/{name}"));
    Assertions.assertNotNull(router.get(RoutePathUtil.cleanPath("/apis/apps/v1/ccc")));
    Assertions.assertNotNull(router.get(RoutePathUtil.cleanPath("/apis/apps/v1/ddd")));
    assertPattern(Map.of("namespace", "foo"), router.get(RoutePathUtil.cleanPath("/apis/apps/v1/namespaces/foo/ccc")));
    assertPattern(Map.of("namespace", "foo", "name", "thz"), router.get(RoutePathUtil.cleanPath("/apis/apps/v1/namespaces/foo/ccc/thz")));
  }

  @Test
  public void testB3() {
    final RouterTrie router = new RouterTrie();
    routerPut(router, RoutePathUtil.parsePath("/apis/aaaa/v1"));
    routerPut(router, RoutePathUtil.parsePathWithVariables("/apis/aaaa/v1/namespaces/{namespace}/lll"));
    routerPut(router, RoutePathUtil.parsePath("/apis/aaaa/v1/ssss"));

    Assertions.assertNotNull(router.get(RoutePathUtil.cleanPath("/apis/aaaa/v1")));
    assertPattern(Map.of("namespace", "foo"), router.get(RoutePathUtil.cleanPath("/apis/aaaa/v1/namespaces/foo/lll")));
    Assertions.assertNotNull(router.get(RoutePathUtil.cleanPath("/apis/aaaa/v1/ssss")));
  }

  @Test
  public void test4() {
    final RouterTrie router = new RouterTrie();
    routerPut(router, RoutePathUtil.parsePathWithVariables("/v1/aaa/{var}"));
    Assertions.assertNotNull(router.get(RoutePathUtil.cleanPath("/v1/aaa/TEST")));
    Assertions.assertNull(router.get(RoutePathUtil.cleanPath("/v1/bbb/TEST")));
    routerPut(router, RoutePathUtil.parsePathWithVariables("/v1/bbb/{bar}"));
    Assertions.assertNotNull(router.get(RoutePathUtil.cleanPath("/v1/aaa/TEST")));
    Assertions.assertNotNull(router.get(RoutePathUtil.cleanPath("/v1/bbb/TEST")));
  }

  private static void routerPut(final RouterTrie router, final RouterPathSpec spec) {
    if (spec.pattern() != null) {
      router.put(spec.path(), new PatternRouteMapping(UriMethod.METHODS_POST, spec.pattern(), ExecutionType.DEFAULT, null, spec.path()));
    } else {
      final String uri = new String(spec.path(), 0, spec.path().length - 1);
      router.put(spec.path(), new DirectRouteMapping(UriMethod.METHODS_POST, uri, ExecutionType.DEFAULT, null));
    }
  }

  private static void assertPattern(final Map<String, String> variables, final RouteMatcher matcher) {
    Assertions.assertNotNull(matcher);
    final Matcher m = matcher.matcher();
    Assertions.assertTrue(m.matches());
    for (final Map.Entry<String, String> entry: variables.entrySet()) {
      Assertions.assertEquals(entry.getValue(), m.group(entry.getKey()));
    }
  }
}
