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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import io.github.matteobertozzi.rednaco.data.json.JsonObject;

public abstract class DataFormatMapperString implements DataFormatMapper {
  @Override
  public JsonNode toTreeNode(final Object value) {
    return JsonFormat.INSTANCE.toTreeNode(value);
  }

  @Override
  public <T> T fromTreeNode(final JsonNode node, final Class<T> valueType)
      throws JsonProcessingException, IllegalArgumentException {
    return JsonFormat.INSTANCE.fromTreeNode(node, valueType);
  }

  @Override
  public <T> T convert(final Object value, final Class<T> valueType) {
    return JsonFormat.INSTANCE.convert(value, valueType);
  }

  @Override
  public <T> T convert(final Object value, final TypeReference<T> valueType) {
    return JsonFormat.INSTANCE.convert(value, valueType);
  }

  @Override
  public <T> T fromStream(final InputStream stream, final Class<T> valueType) throws IOException {
    return fromBytes(stream.readAllBytes(), valueType);
  }

  @Override
  public <T> T fromStream(final InputStream stream, final TypeReference<T> valueType) throws IOException {
    return fromBytes(stream.readAllBytes(), valueType);
  }

  @Override
  public <T> T fromBytes(final byte[] data, final Class<T> valueType) {
    return fromString(new String(data), valueType);
  }

  @Override
  public <T> T fromBytes(final byte[] data, final TypeReference<T> valueType) {
    return fromString(new String(data), valueType);
  }

  @Override
  public <T> T fromBytes(final byte[] data, final int off, final int len, final Class<T> valueType) {
    return fromString(new String(data, off, len), valueType);
  }

  @Override
  public <T> T fromString(final String data, final Class<T> valueType) {
    return JsonFormat.INSTANCE.convert(parseFormatString(data), valueType);
  }

  @Override
  public <T> T fromString(final String data, final TypeReference<T> valueType) {
    return JsonFormat.INSTANCE.convert(parseFormatString(data), valueType);
  }

  @Override
  public void addToStream(final OutputStream stream, final Object obj) throws IOException {
    stream.write(asBytes(obj));
  }

  @Override
  public void addToPrettyPrintStream(final OutputStream stream, final Object obj) throws IOException {
    addToStream(stream, obj);
  }

  @Override
  public String asPrettyPrintString(final Object value) {
    return asString(value);
  }

  @Override
  public String asString(final Object value) {
    return toFormatString(JsonFormat.INSTANCE.convert(value, JsonObject.class));
  }

  @Override
  public byte[] asBytes(final Object value) {
    return asString(value).getBytes(StandardCharsets.UTF_8);
  }

  protected abstract JsonObject parseFormatString(String data);
  protected abstract String toFormatString(JsonObject object);
}
