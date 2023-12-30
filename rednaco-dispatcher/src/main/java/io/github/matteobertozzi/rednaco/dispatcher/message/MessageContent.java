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
import java.io.OutputStream;

import io.github.matteobertozzi.rednaco.bytes.ByteArrayWriter;
import io.github.matteobertozzi.rednaco.data.DataFormat;
import io.github.matteobertozzi.rednaco.hashes.CryptographicHash;

public interface MessageContent {
  MessageContent retain();
  MessageContent release();

  /**
   * @return the length of the message content
   */
  int contentLength();

  long writeContentToStream(OutputStream stream) throws IOException;
  long writeContentToStream(DataOutput stream) throws IOException;
  <T> T convertContent(DataFormat format, Class<T> classOfT);

  default boolean hasContent() {
    return contentLength() > 0;
  }

  default byte[] convertContentToBytes() {
    final byte[] buffer = new byte[contentLength()];
    try (ByteArrayWriter writer = new ByteArrayWriter(buffer)) {
      writeContentToStream(writer);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
    return buffer;
  }

  default CryptographicHash contentHash(final CryptographicHash hash) {
    return hash.update(convertContentToBytes());
  }
}