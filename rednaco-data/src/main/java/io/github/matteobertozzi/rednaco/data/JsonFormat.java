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

import com.fasterxml.jackson.databind.json.JsonMapper;

public final class JsonFormat extends DataFormat {
  public static final JsonFormat INSTANCE = new JsonFormat();

  private final JsonFormatMapper mapper = new JsonFormatMapper();

  private JsonFormat() {
    // no-op
  }

  @Override
  public String name() {
    return "JSON";
  }

  @Override
  public String contentType() {
    return "application/json";
  }

  @Override
  public boolean isBinary() {
    return false;
  }

  @Override
  protected DataFormatMapperJackson get() {
    return mapper;
  }

  private static final class JsonFormatMapper extends DataFormatMapperJackson {
    private JsonFormatMapper() {
      super(new JsonMapper());
    }
  }
}
