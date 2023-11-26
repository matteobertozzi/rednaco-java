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

package io.github.matteobertozzi.rednaco.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LimitedInputStream extends FilterInputStream {
  private final boolean closeInput;
  private final long maxReadable;

  private long consumed;

  public LimitedInputStream(final InputStream in, final long maxReadable) {
    this(in, maxReadable, true);
  }

  public LimitedInputStream(final InputStream in, final long maxReadable, final boolean closeInput) {
    super(in);
    this.maxReadable = maxReadable;
    this.consumed = 0;
    this.closeInput = closeInput;
  }

  @Override
  public void close() throws IOException {
    if (closeInput) {
      super.close();
    }
  }

  public long consumed() {
    return consumed;
  }

  @Override
  public int read() throws IOException {
    if (consumed == maxReadable) return -1;

    final int c = super.read();
    if (c >= 0) consumed++;
    return c;
  }

  @Override
  public int read(final byte[] b) throws IOException {
    return this.read(b, 0, b.length);
  }

  @Override
  public int read(final byte[] b, final int off, final int len) throws IOException {
    if (consumed == maxReadable) return -1;

    final long avail = maxReadable - consumed;
    final int n = super.read(b, off, (len > avail) ? (int) avail : len);
    if (n >= 0) consumed += n;
    return n;
  }
}
