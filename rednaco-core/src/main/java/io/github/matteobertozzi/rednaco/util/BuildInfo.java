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

package io.github.matteobertozzi.rednaco.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import io.github.matteobertozzi.rednaco.strings.StringUtil;

public record BuildInfo(String name, String version,
    String buildDate, String createdBy,
    String gitBranch, String gitHash)
{
  private static final DateTimeFormatter LOCAL_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
  private static final DateTimeFormatter MVN_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
  private static final String UNKNOWN = "unknown";

  public boolean isValid() {
    return !StringUtil.equals(buildDate, UNKNOWN) && !StringUtil.equals(version, UNKNOWN);
  }

  public static BuildInfo fromManifest(final String name) throws IOException {
    String version = UNKNOWN;
    String buildDate = UNKNOWN;
    String createdBy = UNKNOWN;
    String gitBranch = UNKNOWN;
    String gitHash = UNKNOWN;

    final Enumeration<URL> resources = BuildInfo.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
    while (resources.hasMoreElements()) {
      final Attributes attributes;
      try (InputStream stream = resources.nextElement().openStream()) {
        final Manifest manifest = new Manifest(stream);
        attributes = manifest.getMainAttributes();
        if (!Objects.equals(attributes.getValue("Implementation-Title"), name)) {
          continue;
        }
      }

      // parse build timestamp
      final ZonedDateTime utcBuildDate = LocalDateTime.parse(attributes.getValue("buildTimestamp"), MVN_DATE_FORMAT).atZone(ZoneOffset.UTC);
      final LocalDateTime localBuildDate = utcBuildDate.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();

      // add service info
      buildDate = localBuildDate.format(LOCAL_DATE_FORMAT);
      version = attributes.getValue("Implementation-Version");
      createdBy = attributes.getValue("Built-By");
      if (StringUtil.isEmpty(createdBy)) {
        createdBy = attributes.getValue("builtBy");
      }
      gitBranch = attributes.getValue("gitBranch");
      gitHash = attributes.getValue("gitHash");
      break;
    }

    return new BuildInfo(name, version, buildDate, createdBy, gitBranch, gitHash);
  }
}
