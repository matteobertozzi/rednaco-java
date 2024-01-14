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

import java.util.ArrayList;
import java.util.List;

import io.github.matteobertozzi.easerinsights.logging.Logger;
import io.github.matteobertozzi.rednaco.collections.ImmutableCollections;
import io.github.matteobertozzi.rednaco.collections.arrays.ArraySortUtil;
import io.github.matteobertozzi.rednaco.collections.arrays.ArrayUtil;
import io.github.matteobertozzi.rednaco.dispatcher.routing.RoutesMapping.DirectRouteMapping;
import io.github.matteobertozzi.rednaco.dispatcher.routing.RoutesMapping.PatternRouteMapping;

public class RouteBuilder {
  private final ArrayList<PatternRouteMapping> patternMappings = new ArrayList<>();
  private final ArrayList<DirectRouteMapping> directMappings = new ArrayList<>();
  private final ArrayList<String> aliases = new ArrayList<>();

  public RouteBuilder() {
    // no-op
  }

  public void addAlias(final String alias, final String uriPrefix) {
    aliases.add(alias);
    aliases.add(uriPrefix);
  }

  public RouteBuilder add(final RoutesMapping mapping) {
    addToDirectMap(mapping.directRouteMappings());
    addToPatternTrie(mapping.variableRouteMappings());
    addToPatternTrie(mapping.patternRouteMappings());
    return this;
  }

  private void addToDirectMap(final DirectRouteMapping[] mappings) {
    if (ArrayUtil.isNotEmpty(mappings)) {
      directMappings.addAll(ImmutableCollections.listOf(mappings));
    }
  }

  private void addToPatternTrie(final PatternRouteMapping[] mappings) {
    if (ArrayUtil.isNotEmpty(mappings)) {
      patternMappings.addAll(ImmutableCollections.listOf(mappings));
    }
  }

  private static RouterMap[] buildRouterMap(final List<DirectRouteMapping> mappings) {
    final RouterMap[] directMappings = new RouterMap[UriMethod.METHODS.length];
    for (int m = 0; m < directMappings.length; ++m) {
      directMappings[m] = new RouterMap();
    }

    for (final DirectRouteMapping route: mappings) {
      final UriMethod[] methods = route.methods();
      for (int m = 0; m < methods.length; ++m) {
        final UriMethod method = methods[m];
        directMappings[method.ordinal()].put(route.uri(), route);
        Logger.trace("add direct mapping: {} {}", method, route.uri());
      }
    }
    return directMappings;
  }

  private static RouterTrie[] buildRouterTries(final List<PatternRouteMapping> mappings) {
    final RouterTrie[] patternMappings = new RouterTrie[UriMethod.METHODS.length];
    for (int m = 0; m < patternMappings.length; ++m) {
      patternMappings[m] = new RouterTrie();
    }

    for (final PatternRouteMapping route: mappings) {
      final UriMethod[] methods = route.methods();
      for (int m = 0; m < methods.length; ++m) {
        final UriMethod method = methods[m];
        patternMappings[method.ordinal()].put(route.path(), route);
        Logger.trace("add pattern mapping: {} {}", method, route.pattern());
      }
    }
    return patternMappings;
  }

  private static String[] buildAliases(final List<String> aliases) {
    if (aliases.isEmpty()) return new String[0];

    final String[] sortedAliases = aliases.toArray(new String[0]);
    ArraySortUtil.sort(0, sortedAliases.length / 2,
      (a, b) -> sortedAliases[b * 2].compareTo(sortedAliases[a * 2]),
      (a, b) -> {
        ArrayUtil.swap(sortedAliases, a * 2, b * 2);
        ArrayUtil.swap(sortedAliases, (a * 2) + 1, (b * 2) + 1);
      }
    );
    for (int i = 0; i < sortedAliases.length; i += 2) {
      Logger.trace("add mapping alias: {} -> {}", sortedAliases[i], sortedAliases[i + 1]);
    }

    return sortedAliases;
  }

  public Router build() {
    final RouterMap[] routerMap = buildRouterMap(directMappings);
    final RouterTrie[] routerTrie = buildRouterTries(patternMappings);
    return new Router(buildAliases(aliases), routerMap, routerTrie);
  }
}
