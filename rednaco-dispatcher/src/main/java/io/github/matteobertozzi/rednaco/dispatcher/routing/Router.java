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

import io.github.matteobertozzi.rednaco.dispatcher.routing.RoutesMapping.RouteMapping;
import io.github.matteobertozzi.rednaco.dispatcher.routing.RoutesMapping.RouteMatcher;

public class Router {
  private final RouterMap[] directMappings;
  private final RouterTrie[] patternMappings;

  public Router(final RouterMap[] directMappings, final RouterTrie[] patternMappings) {
    this.directMappings = directMappings;
    this.patternMappings = patternMappings;
  }

  public RouteMatcher get(final UriMethod method, final String rawPath) {
    final String path = RoutePathUtil.cleanPath(rawPath);
    final int methodIndex = method.ordinal();

    final RouteMapping mapping = directMappings[methodIndex].get(path);
    if (mapping != null) return mapping;

    return patternMappings[methodIndex].get(path);
  }
}
