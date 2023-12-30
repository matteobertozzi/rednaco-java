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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.matteobertozzi.rednaco.dispatcher.MessageExecutor;

public interface RoutesMapping {
  DirectRouteMapping[] directRouteMappings();
  PatternRouteMapping[] variableRouteMappings();
  PatternRouteMapping[] patternRouteMappings();

  interface RouteMatcher {
    MessageExecutor executor();
    Matcher matcher();
  }

  interface RouteMapping extends RouteMatcher {
    UriMethod[] methods();
    String uri();

    RouteMatcher match(String path);
  }

  record DirectRouteMapping(UriMethod[] methods, String uri, MessageExecutor executor) implements RouteMapping {
    @Override
    public Matcher matcher() {
      return null;
    }

    @Override
    public RouteMatcher match(final String path) {
      return this;
    }
  }

  record PatternRouteMatcher(Matcher matcher, MessageExecutor executor) implements RouteMatcher {}
  record PatternRouteMapping(UriMethod[] methods, byte[] path, Pattern pattern, MessageExecutor executor) implements RouteMapping {
    @Override
    public Matcher matcher() {
      throw new UnsupportedOperationException();
    }

    @Override
    public String uri() {
      return new String(path, 0, path.length - 1);
    }

    @Override
    public RouteMatcher match(final String path) {
      // TODO: carrier pool
      final Matcher m = pattern.matcher(path);
      return m.matches() ? new PatternRouteMatcher(m, executor()) : null;
    }
  }
}
