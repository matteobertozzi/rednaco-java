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

import java.io.IOException;

import io.github.matteobertozzi.rednaco.dispatcher.message.MessageUtil.EmptyMetadata;

public class MessageException extends IOException {
  private final MessageMetadata metadata;
  private final MessageError error;
  private final boolean logTrace;

  public MessageException(final MessageError error) {
    this(error, true);
  }

  public MessageException(final MessageError error, final boolean logTrace) {
    this(EmptyMetadata.INSTANCE, error, logTrace);
  }

  public MessageException(final MessageMetadata metadata, final MessageError error) {
    this(metadata, error, true);
  }

  public MessageException(final MessageMetadata metadata, final MessageError error, final boolean logTrace) {
    super(error.message());
    this.metadata = metadata;
    this.error = error;
    this.logTrace = logTrace;
  }

  public MessageException(final Throwable e, final MessageError error) {
    this(e, error, true);
  }

  public MessageException(final Throwable e, final MessageError error, final boolean logTrace) {
    this(e, EmptyMetadata.INSTANCE, error, logTrace);
  }

  public MessageException(final Throwable e, final MessageMetadata metadata, final MessageError error) {
    this(e, metadata, error, true);
  }

  public MessageException(final Throwable e, final MessageMetadata metadata, final MessageError error, final boolean logTrace) {
    super(error.message(), e);
    this.metadata = metadata;
    this.error = error;
    this.logTrace = logTrace;
  }

  public boolean shouldLogTrace() {
    return logTrace;
  }

  public MessageMetadata getMetadata() {
    return metadata;
  }

  public MessageError getMessageError() {
    return error;
  }
}
