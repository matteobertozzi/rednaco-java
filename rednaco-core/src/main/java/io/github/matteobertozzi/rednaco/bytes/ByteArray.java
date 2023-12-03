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

package io.github.matteobertozzi.rednaco.bytes;

import java.util.Arrays;

import io.github.matteobertozzi.rednaco.bytes.BytesUtil.ByteArrayConsumer;
import io.github.matteobertozzi.rednaco.collections.arrays.ArrayUtil;

public class ByteArray implements ByteArrayAppender {
  private byte[] buffer;
  private int length;

  public ByteArray(final int initialCapacity) {
    this.buffer = new byte[initialCapacity];
    this.length = 0;
  }

  public void reset() {
    this.length = 0;
  }

  public boolean isEmpty() {
    return length == 0;
  }

  public boolean isNotEmpty() {
    return length != 0;
  }

  public int size() {
    return length;
  }

  public byte[] rawBuffer() {
    return buffer;
  }

  public byte[] buffer() {
    return Arrays.copyOf(buffer, length);
  }

  public ByteArraySlice slice() {
    return isEmpty() ? ByteArraySlice.EMPTY_SLICE : new ByteArraySlice(buffer, 0, length);
  }

  public byte[] drain() {
    final byte[] result;
    if (buffer.length == length) {
      result = buffer;
      this.buffer = BytesUtil.EMPTY_BYTES;
    } else if (length == 0) {
      result = BytesUtil.EMPTY_BYTES;
    } else {
      result = Arrays.copyOf(buffer, length);
    }
    this.length = 0;
    return result;
  }

  public int get(final int index) {
    return buffer[index] & 0xff;
  }

  public void set(final int index, final int value) {
    buffer[index] = (byte)(value & 0xff);
  }

  @Override
  public void add(final int value) {
    if (length == buffer.length) {
      this.buffer = Arrays.copyOf(buffer, length + 16);
    }
    buffer[length++] = (byte) (value & 0xff);
  }

  @Override
  public void add(final byte[] value) {
    add(value, 0, value.length);
  }

  @Override
  public void add(final byte[] value, final int off, final int len) {
    if ((length + len) >= buffer.length) {
      this.buffer = Arrays.copyOf(buffer, length + len + 16);
    }
    System.arraycopy(value, off, buffer, length, len);
    length += len;
  }

  public void insert(final int index, final int value) {
    if (index == length) {
      if (length == buffer.length) {
        this.buffer = Arrays.copyOf(buffer, length + 16);
      }
      length++;
    }
    buffer[index] = (byte) (value & 0xff);
  }

  public void swap(final int aIndex, final int bIndex) {
    ArrayUtil.swap(buffer, aIndex, bIndex);
  }

  public void fill(final int value) {
    Arrays.fill(buffer, (byte) (value & 0xff));
  }

  public void forEach(final ByteArrayConsumer consumer) {
    consumer.accept(buffer, 0, length);
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder(16 + size());
    builder.append("ByteArray [");
    builder.append(size());
    builder.append(':');
    BytesUtil.toHexString(builder, buffer, 0, length);
    builder.append(']');
    return builder.toString();
  }
}
