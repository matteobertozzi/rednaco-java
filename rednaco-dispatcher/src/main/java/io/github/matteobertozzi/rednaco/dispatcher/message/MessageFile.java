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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import io.github.matteobertozzi.rednaco.data.DataFormat;
import io.github.matteobertozzi.rednaco.dispatcher.message.MessageUtil.EmptyMetadata;
import io.github.matteobertozzi.rednaco.io.IOUtil;

public class MessageFile implements Message {
  private final MessageMetadata metadata;
  private final File file;
  private final long timestamp;

  public MessageFile(final File file) {
    this(EmptyMetadata.INSTANCE, file);
  }

  public MessageFile(final MessageMetadata metadata, final File file) {
    this.metadata = metadata;
    this.file = file;
    this.timestamp = System.nanoTime();
  }

  public MessageMetadata metadata() {
    return metadata;
  }

  public File file() {
    return file;
  }

  @Override
  public int contentLength() {
    return Math.toIntExact(file.length());
  }

  @Override
  public long writeContentToStream(final OutputStream stream) throws IOException {
    try (FileInputStream inputStream = new FileInputStream(file)) {
      return inputStream.transferTo(stream);
    }
  }

  @Override
  public long writeContentToStream(final DataOutput stream) throws IOException {
    try (FileInputStream inputStream = new FileInputStream(file)) {
      return IOUtil.copy(inputStream, stream);
    }
  }

  @Override
  public <T> T convertContent(final DataFormat format, final Class<T> classOfT) {
    try {
      return format.fromFile(file, classOfT);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int estimateSize() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Message retain() {
    return this;
  }

  @Override
  public Message release() {
    return this;
  }

  @Override
  public long timestampNs() {
    return timestamp;
  }
}
