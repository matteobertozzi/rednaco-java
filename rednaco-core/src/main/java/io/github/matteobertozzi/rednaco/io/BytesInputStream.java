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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class BytesInputStream extends InputStream {
  @Override
  public void close() {
    // no-op
  }

  @Override
  public abstract void reset();

  public abstract boolean isEmpty();

  public boolean isNotEmpty() {
    return !isEmpty();
  }

  public abstract void seekTo(int offset);

  public abstract int length();

  public abstract void copyTo(int blockLen, OutputStream stream) throws IOException;
}
