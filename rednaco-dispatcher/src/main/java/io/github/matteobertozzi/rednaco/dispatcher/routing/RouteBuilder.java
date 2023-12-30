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

import io.github.matteobertozzi.easerinsights.logging.Logger;
import io.github.matteobertozzi.rednaco.dispatcher.routing.RoutesMapping.DirectRouteMapping;
import io.github.matteobertozzi.rednaco.dispatcher.routing.RoutesMapping.PatternRouteMapping;

public class RouteBuilder {
  private final RouterMap[] directMappings;
  private final RouterTrie[] patternMappings;

  public RouteBuilder() {
    directMappings = new RouterMap[UriMethod.METHODS.length];
    for (int m = 0; m < directMappings.length; ++m) {
      directMappings[m] = new RouterMap();
    }

    patternMappings = new RouterTrie[UriMethod.METHODS.length];
    for (int m = 0; m < patternMappings.length; ++m) {
      patternMappings[m] = new RouterTrie();
    }
  }

  public RouteBuilder add(final RoutesMapping mapping) {
    addToDirectMap(mapping.directRouteMappings());
    addToPatternTrie(mapping.variableRouteMappings());
    addToPatternTrie(mapping.patternRouteMappings());
    return this;
  }

  private void addToDirectMap(final DirectRouteMapping[] mappings) {
    for (int i = 0; i < mappings.length; ++i) {
      final DirectRouteMapping route = mappings[i];
      final UriMethod[] methods = route.methods();
      for (int m = 0; m < methods.length; ++m) {
        final UriMethod method = methods[m];
        directMappings[method.ordinal()].put(route.uri(), route);
        Logger.trace("add direct mapping: {} {}", method, route.uri());
      }
    }
  }

  private void addToPatternTrie(final PatternRouteMapping[] mappings) {
    for (int i = 0; i < mappings.length; ++i) {
      final PatternRouteMapping route = mappings[i];
      final UriMethod[] methods = route.methods();
      for (int m = 0; m < methods.length; ++m) {
        final UriMethod method = methods[m];
        patternMappings[method.ordinal()].put(route.path(), route);
        Logger.trace("add pattern mapping: {} {}", method, route.pattern());
      }
    }
  }

  public Router build() {
    return new Router(directMappings, patternMappings);
  }
}
