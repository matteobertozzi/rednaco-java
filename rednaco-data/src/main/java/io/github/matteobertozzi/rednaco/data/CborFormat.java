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

import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper;

public final class CborFormat extends DataFormat {
  public static final CborFormat INSTANCE = new CborFormat();

  //private static final ThreadLocal<CborFormatMapper> mapper = ThreadLocal.withInitial(CborFormatMapper::new);
  private final CborFormatMapper mapper = new CborFormatMapper();

  private CborFormat() {
    // no-op
  }

  @Override
  public String name() {
    return "CBOR";
  }

  @Override
  public String contentType() {
    return "application/cbor";
  }

  @Override
  protected DataFormatMapper get() {
    return mapper;
  }

  private static final class CborFormatMapper extends DataFormatMapper {
    private CborFormatMapper() {
      super(new CBORMapper());
    }
  }
}
