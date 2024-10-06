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

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import io.github.matteobertozzi.rednaco.data.json.JsonElement;
import io.github.matteobertozzi.rednaco.data.json.JsonObject;
import io.github.matteobertozzi.rednaco.data.json.JsonPrimitive;

public final class FormUrlEncodedFormat extends DataFormat {
  public static final FormUrlEncodedFormat INSTANCE = new FormUrlEncodedFormat();

  @Override
  public String name() {
    return "form-urlencoded";
  }

  @Override
  public String contentType() {
    return "application/x-www-form-urlencoded";
  }

  @Override
  public boolean isBinary() {
    return false;
  }

  @Override
  protected DataFormatMapper get() {
    return FormUrlEncodedMapper.INSTANCE;
  }

  private static final class FormUrlEncodedMapper extends DataFormatMapperString {
    private static final FormUrlEncodedMapper INSTANCE = new FormUrlEncodedMapper();

    private FormUrlEncodedMapper() {
      // no-op
    }

    @Override
    protected JsonObject parseFormatString(final String data) {
      final StringBuilder key = new StringBuilder();
      final StringBuilder value = new StringBuilder();
      boolean isKey = true;

      final JsonObject json = new JsonObject();
      for (int i = 0; i < data.length(); i++) {
        final char c = data.charAt(i);
        switch (c) {
          case '=':
            if (isKey) {
              isKey = false;
            } else {
              value.append(c);
            }
            break;
          case '&':
            json.add(
              URLDecoder.decode(key.toString(), StandardCharsets.UTF_8),
              URLDecoder.decode(value.toString(), StandardCharsets.UTF_8)
            );
            key.setLength(0);
            value.setLength(0);
            isKey = true;
            break;
          default:
            if (isKey) {
              key.append(c);
            } else {
              value.append(c);
            }
            break;
        }
      }

      if (!key.isEmpty()) {
        json.add(
          URLDecoder.decode(key.toString(), StandardCharsets.UTF_8),
          URLDecoder.decode(value.toString(), StandardCharsets.UTF_8)
        );
      }
      return json;
    }

    @Override
    protected String toFormatString(final JsonObject object) {
      final StringBuilder result = new StringBuilder();
      for (final Map.Entry<String, JsonElement> entry: object.entrySet()) {
        if (!result.isEmpty()) result.append('&');
        result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
        result.append('=');
        if (entry.getValue().isJsonPrimitive()) {
          final JsonPrimitive primitive = entry.getValue().getAsJsonPrimitive();
          if (primitive.isString()) {
            result.append(URLEncoder.encode(primitive.getAsString(), StandardCharsets.UTF_8));
          } else if (primitive.isBoolean()) {
            result.append(primitive.getAsBoolean());
          } else if (primitive.isNumber()) {
            result.append(primitive.getAsNumber());
          } else {
            throw new UnsupportedOperationException();
          }
        } else {
          result.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
      }
      return result.toString();
    }
  }
}
