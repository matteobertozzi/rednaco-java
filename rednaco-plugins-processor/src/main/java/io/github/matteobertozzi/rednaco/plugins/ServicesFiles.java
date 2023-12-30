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

package io.github.matteobertozzi.rednaco.plugins;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A helper class for reading and writing Services files.
 */
final class ServicesFiles {
  private static final String SERVICES_PATH = "META-INF/services";

  private ServicesFiles() {
    // no-op
  }

  public static String getPath(final Class<?> serviceClass) {
    return SERVICES_PATH + "/" + serviceClass.getName();
  }

  public static Set<String> readServiceFile(final InputStream input) throws IOException {
    try (BufferedReader r = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
      final HashSet<String> serviceClasses = new HashSet<>();
      String line;
      while ((line = r.readLine()) != null) {
        final int commentStart = line.indexOf('#');
        if (commentStart >= 0) {
          line = line.substring(0, commentStart);
        }
        line = line.trim();
        if (!line.isEmpty()) {
          serviceClasses.add(line);
        }
      }
      return serviceClasses;
    }
  }

  public static void writeServiceFile(final Collection<String> services, final OutputStream output) throws IOException {
    try (final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8))) {
      final String[] sortedServices = services.toArray(new String[0]);
      Arrays.sort(sortedServices);
      for (int i = 0; i < sortedServices.length; ++i) {
        writer.write(sortedServices[i]);
        writer.newLine();
      }
      writer.flush();
    }
  }
}
