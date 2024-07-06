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

package io.github.matteobertozzi.rednaco.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import io.github.matteobertozzi.rednaco.bytes.ByteArrayAppender;
import io.github.matteobertozzi.rednaco.bytes.ByteArrayReader;
import io.github.matteobertozzi.rednaco.bytes.ByteArraySlice;
import io.github.matteobertozzi.rednaco.bytes.BytesAppenderOutputStream;
import io.github.matteobertozzi.rednaco.bytes.BytesUtil;
import io.github.matteobertozzi.rednaco.bytes.PagedByteArray;
import io.github.matteobertozzi.rednaco.bytes.PagedByteArrayWriter;
import io.github.matteobertozzi.rednaco.io.LimitedInputStream;
import io.github.matteobertozzi.rednaco.strings.StringUtil;

public abstract class DataFormat {
  protected DataFormat() {
    // no-op
  }

  public abstract String name();
  public abstract String contentType();

  protected abstract DataFormatMapper get();

  // ===============================================================================================
  //  JsonNode conversions
  // ===============================================================================================
  public JsonNode toTreeNode(final Object value) {
    return get().toTreeNode(value);
  }

  public <T> T fromTreeNode(final JsonNode node, final Class<T> valueType) throws JsonProcessingException, IllegalArgumentException {
    return get().fromTreeNode(node, valueType);
  }

  // ===============================================================================================
  //  Object to Object conversions
  // ===============================================================================================
  public <T> T convert(final Object value, final Class<T> valueType) {
    return get().convert(value, valueType);
  }

  public <T> T convert(final Object value, final TypeReference<T> valueType) {
    return get().convert(value, valueType);
  }

  // ===============================================================================================
  //  From file/stream/byte[]/string/... conversions
  // ===============================================================================================
  public <T> T fromFile(final File file, final Class<T> valueType) throws IOException {
    try (FileInputStream stream = new FileInputStream(file)) {
      return fromStream(stream, valueType);
    }
  }

  public <T> T fromFile(final File file, final TypeReference<T> valueType) throws IOException {
    try (FileInputStream stream = new FileInputStream(file)) {
      return fromStream(stream, valueType);
    }
  }

  public <T> T fromFile(final Path path, final Class<T> valueType) throws IOException {
    try (InputStream stream = Files.newInputStream(path)) {
      return fromStream(stream, valueType);
    }
  }

  public <T> T fromFile(final Path path, final TypeReference<T> valueType) throws IOException {
    try (InputStream stream = Files.newInputStream(path)) {
      return fromStream(stream, valueType);
    }
  }

  public <T> T fromStream(final InputStream stream, final Class<T> valueType) throws IOException {
    return get().fromStream(stream, valueType);
  }

  public <T> T fromStream(final InputStream stream, final TypeReference<T> valueType) throws IOException {
    return get().fromStream(stream, valueType);
  }

  public <T> T fromStream(final InputStream stream, final int length, final Class<T> valueType) throws IOException {
    try (LimitedInputStream limitedStream = new LimitedInputStream(stream, length, false)) {
      return get().fromStream(limitedStream, valueType);
    }
  }

  public <T> T fromStream(final InputStream stream, final int length, final TypeReference<T> valueType) throws IOException {
    try (LimitedInputStream limitedStream = new LimitedInputStream(stream, length, false)) {
      return get().fromStream(limitedStream, valueType);
    }
  }

  public <T> T fromBytes(final byte[] data, final Class<T> valueType) {
    return BytesUtil.isEmpty(data) ? null : get().fromBytes(data, valueType);
  }

  public <T> T fromBytes(final byte[] data, final TypeReference<T> valueType) {
    return BytesUtil.isEmpty(data) ? null : get().fromBytes(data, valueType);
  }

  public <T> T fromBytes(final byte[] data, final int off, final int len, final Class<T> valueType) {
    return (len == 0) ? null : get().fromBytes(data, off, len, valueType);
  }

  public <T> T fromBytes(final ByteArraySlice data, final Class<T> valueType) {
    if (data.isEmpty()) return null;
    try (ByteArrayReader reader = new ByteArrayReader(data)) {
      return fromStream(reader, valueType);
    } catch (final Exception e) {
      throw new DataFormatException(e);
    }
  }

  public <T> T fromString(final String data, final Class<T> valueType) {
    return StringUtil.isEmpty(data) ? null : get().fromString(data, valueType);
  }

  public <T> T fromString(final String data, final TypeReference<T> valueType) {
    return StringUtil.isEmpty(data) ? null : get().fromString(data, valueType);
  }

  // ===============================================================================================
  //  To file/stream/byte[]/string/... conversions
  // ===============================================================================================
  public void addToStream(final OutputStream stream, final Object obj) throws IOException {
    get().addToStream(stream, obj);
  }

  public void addToPrettyPrintStream(final OutputStream stream, final Object obj) throws IOException {
    get().addToPrettyPrintStream(stream, obj);
  }

  public void addToByteArray(final ByteArrayAppender buffer, final Object obj) {
    try (BytesAppenderOutputStream stream = new BytesAppenderOutputStream(buffer)) {
      addToStream(stream, obj);
      stream.flush();
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void addToByteArray(final PagedByteArray buffer, final Object obj) {
    try (PagedByteArrayWriter writer = new PagedByteArrayWriter(buffer)) {
      addToStream(writer, obj);
      writer.flush();
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String asPrettyPrintString(final Object value) {
    return get().asPrettyPrintString(value);
  }

  public String asString(final Object value) {
    return get().asString(value);
  }

  public byte[] asBytes(final Object value) {
    return get().asBytes(value);
  }
}
