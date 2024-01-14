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

import java.util.concurrent.TimeUnit;

import io.github.matteobertozzi.easerinsights.DatumUnit;
import io.github.matteobertozzi.easerinsights.metrics.Metrics;
import io.github.matteobertozzi.easerinsights.metrics.collectors.MaxAvgTimeRangeGauge;
import io.github.matteobertozzi.rednaco.dispatcher.routing.RoutesMapping.RouteMatcher;

public class Router {
  private final MaxAvgTimeRangeGauge lookupTime = Metrics.newCollector()
      .unit(DatumUnit.NANOSECONDS)
      .name("message.router.lookup.time")
      .label("Message Router Lookup Time")
      .register(MaxAvgTimeRangeGauge.newMultiThreaded(60, 1, TimeUnit.MINUTES));

  private final String[] aliases;
  private final RouterMap[] directMappings;
  private final RouterTrie[] patternMappings;

  public Router(final String[] aliases, final RouterMap[] directMappings, final RouterTrie[] patternMappings) {
    this.aliases = aliases;
    this.directMappings = directMappings;
    this.patternMappings = patternMappings;
  }

  public RouteMatcher get(final UriMethod method, final String rawPath) {
    final long startTime = System.nanoTime();
    try {
      final String path = RoutePathUtil.cleanPath(rawPath);
      final int methodIndex = method.ordinal();

      RouteMatcher mapping = directMappings[methodIndex].get(path);
      if (mapping != null) return mapping;

      final String aliasPath = applyAlias(path);
      if (aliasPath != null) {
        mapping = directMappings[methodIndex].get(aliasPath);
        if (mapping != null) return mapping;
      }

      mapping = patternMappings[methodIndex].get(path);
      if (mapping != null) return mapping;

      if (aliasPath != null) {
        mapping = patternMappings[methodIndex].get(aliasPath);
        return mapping;
      }
      return null;
    } finally {
      lookupTime.sample(System.nanoTime() - startTime);
    }
  }

  private String applyAlias(final String path) {
    for (int i = 0; i < aliases.length; i += 2) {
      final String alias = aliases[i];
      if (path.startsWith(alias)) {
        return aliases[i + 1] + path.substring(alias.length());
      }
    }
    return null;
  }
}
