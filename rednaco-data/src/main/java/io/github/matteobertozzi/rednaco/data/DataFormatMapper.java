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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

interface DataFormatMapper {
// ===============================================================================================
  //  JsonNode conversions
  // ===============================================================================================
  JsonNode toTreeNode(Object value);
  <T> T fromTreeNode(JsonNode node, Class<T> valueType) throws JsonProcessingException, IllegalArgumentException;

  // ===============================================================================================
  //  Object to Object conversions
  // ===============================================================================================
  <T> T convert(Object value, Class<T> valueType);
  <T> T convert(Object value, TypeReference<T> valueType);

  // ===============================================================================================
  //  From file/stream/byte[]/string/... conversions
  // ===============================================================================================
  <T> T fromStream(InputStream stream, Class<T> valueType) throws IOException;
  <T> T fromStream(InputStream stream, TypeReference<T> valueType) throws IOException;
  <T> T fromBytes(byte[] data, Class<T> valueType);
  <T> T fromBytes(byte[] data, TypeReference<T> valueType);

  <T> T fromBytes(byte[] data, int off, int len, Class<T> valueType);
  <T> T fromString(String data, Class<T> valueType);
  <T> T fromString(String data, TypeReference<T> valueType);

  // ===============================================================================================
  //  To file/stream/byte[]/string/... conversions
  // ===============================================================================================
  void addToStream(OutputStream stream, Object obj) throws IOException;
  void addToPrettyPrintStream(OutputStream stream, Object obj) throws IOException;

  String asPrettyPrintString(Object value);
  String asString(Object value);
  byte[] asBytes(Object value);
}
