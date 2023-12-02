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

package io.github.matteobertozzi.rednaco.io;

import java.io.Closeable;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;

import io.github.matteobertozzi.easerinsights.logging.Logger;

public final class IOUtil {
  private IOUtil() {
    // no-op
  }

  // ===============================================================================================
  //  Close related
  // ===============================================================================================
  public static void closeQuietly(final Closeable closeable) {
    if (closeable == null) return;
    try {
      closeable.close();
    } catch (final IOException e) {
      Logger.trace("unable to close {}: {}", closeable, e.getMessage());
    }
  }

  public static void closeQuietly(final AutoCloseable closeable) {
    if (closeable == null) return;
    try {
      closeable.close();
    } catch (final Exception e) {
      Logger.trace("unable to close {}: {}", closeable, e.getMessage());
    }
  }

  // ===============================================================================================
  //  Copy related
  // ===============================================================================================
  public static long copy(final InputStream in, final DataOutput out) throws IOException {
    final byte[] buffer = new byte[16384];
    long transferred = 0;
    int read;
    while ((read = in.read(buffer, 0, 16384)) >= 0) {
      out.write(buffer, 0, read);
      transferred += read;
    }
    return transferred;
  }
}
