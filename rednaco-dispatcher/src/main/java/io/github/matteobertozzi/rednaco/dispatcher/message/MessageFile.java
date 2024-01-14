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

package io.github.matteobertozzi.rednaco.dispatcher.message;

import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import io.github.matteobertozzi.rednaco.data.DataFormat;
import io.github.matteobertozzi.rednaco.io.IOUtil;
import io.github.matteobertozzi.rednaco.io.RuntimeIOException;

public record MessageFile(MessageMetadata metadata, Path path, long rangeOffset, long rangeLength, long length) implements Message {
  public boolean isPartialRange() {
    return length != rangeLength;
  }

  @Override
  public int contentLength() {
    throw new UnsupportedOperationException();
  }

  @Override
  public long writeContentToStream(final OutputStream stream) throws IOException {
    try (InputStream inputStream = Files.newInputStream(path)) {
      return inputStream.transferTo(stream);
    }
  }

  @Override
  public long writeContentToStream(final DataOutput stream) throws IOException {
    try (InputStream inputStream = Files.newInputStream(path)) {
      return IOUtil.copy(inputStream, stream);
    }
  }

  @Override
  public <T> T convertContent(final DataFormat format, final Class<T> classOfT) {
    try {
      return format.fromFile(path, classOfT);
    } catch (final IOException e) {
      throw new RuntimeIOException(e);
    }
  }

  @Override
  public Message retain() {
    return this;
  }

  @Override
  public Message release() {
    return this;
  }
}
