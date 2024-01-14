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

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public class YamlFormat extends DataFormat {
  public static final YamlFormat INSTANCE = new YamlFormat();

  //private static final ThreadLocal<YamlFormatMapper> mapper = ThreadLocal.withInitial(YamlFormatMapper::new);
  private final YamlFormatMapper mapper = new YamlFormatMapper();

  private YamlFormat() {
    // no-op
  }

  @Override
  public String name() {
    return "YAML";
  }

  @Override
  public String contentType() {
    return "application/yaml";
  }

  @Override
  protected DataFormatMapper get() {
    return mapper;
  }

  private static final class YamlFormatMapper extends DataFormatMapper {
    private YamlFormatMapper() {
      super(new YAMLMapper());
    }
  }
}
