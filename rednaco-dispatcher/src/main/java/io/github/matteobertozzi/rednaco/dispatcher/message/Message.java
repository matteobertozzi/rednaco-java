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

import java.util.List;

public interface Message extends MessageContent {
  /**
   * @return The size of the message. metadata + data
   */
  int estimateSize();

  long timestampNs();

  MessageMetadata metadata();

  default List<String> metadataValueAsList(final String key) {
    return metadata().getList(key);
  }

  default String metadataValue(final String key) {
    return metadata().get(key);
  }

  default String metadataValue(final String key, final String defaultValue) {
    return metadata().getString(key, defaultValue);
  }
}

