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

package io.github.matteobertozzi.rednaco.hashes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import io.github.matteobertozzi.rednaco.bytes.encoding.IntEncoder;

public abstract class AbstractHash<T extends AbstractHash<T>> {
  public abstract T update(byte[] buf, int off, int len);

  public T update(final byte[] buf) {
    return update(buf, 0, buf.length);
  }

  public T update(final String buf) {
    return update(buf.getBytes());
  }

  public T updateUtf8(final String buf) {
    return update(buf.getBytes(StandardCharsets.UTF_8));
  }

  public T update(final byte b) {
    return update(new byte[] { b });
  }

  public T update(final IntEncoder encoder, final int v) {
    final byte[] buf = new byte[4];
    encoder.writeFixed32(buf, 0, v);
    return update(buf, 0, 4);
  }

  public T update(final IntEncoder encoder, final long v) {
    final byte[] buf = new byte[8];
    encoder.writeFixed64(buf, 0, v);
    return update(buf, 0, 8);
  }

  public T update(final UUID uuid) {
    update(IntEncoder.BIG_ENDIAN, uuid.getMostSignificantBits());
    update(IntEncoder.BIG_ENDIAN, uuid.getLeastSignificantBits());
    return self();
  }

  public T update(final Path file) throws IOException {
    try (InputStream stream = Files.newInputStream(file)) {
      update(stream);
    }
    return self();
  }

  public T update(final File file) throws IOException {
    try (FileInputStream stream = new FileInputStream(file)) {
      update(stream);
    }
    return self();
  }

  private T update(final InputStream stream) throws IOException {
    final byte[] buffer = new byte[16384];
    int read;
    while ((read = stream.read(buffer, 0, 16384)) >= 0) {
      update(buffer, 0, read);
    }
    return self();
  }

  @SuppressWarnings("unchecked")
  protected T self() {
    return (T)this;
  }
}
