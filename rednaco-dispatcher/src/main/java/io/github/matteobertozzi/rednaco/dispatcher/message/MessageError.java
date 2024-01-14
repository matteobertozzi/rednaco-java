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

import io.github.matteobertozzi.easerinsights.tracing.TraceId;
import io.github.matteobertozzi.easerinsights.tracing.Tracer;
import io.github.matteobertozzi.rednaco.data.DataFormat;
import io.github.matteobertozzi.rednaco.localization.LocalizedResource;
import io.github.matteobertozzi.rednaco.localization.LocalizedText;
import io.github.matteobertozzi.rednaco.strings.StringUtil;

public class MessageError {
  enum ErrorStatus {
    NOT_IMPLEMENTED,
    BAD_REQUEST,
    NOT_FOUND,
    FORBIDDEN,
    UNAUTHORIZED,
    INTERNAL_SERVER_ERROR,
    TOO_MANY_REQUESTS,
    NOT_MODIFIED,
  }

  private transient int statusCode;
  private TraceId traceId;
  private String status;
  private String message;
  private Object data;

  public MessageError(final Enum<?> status, final String message) {
    this(status, null, message);
  }

  public MessageError(final Enum<?> status, final Object data, final String message) {
    this(500, status, data, message);
  }

  public MessageError(final int statusCode, final Enum<?> status, final String message) {
    this(statusCode, status, null, message);
  }

  public MessageError(final int statusCode, final Enum<?> status, final Object data, final String message) {
    this(statusCode, Tracer.getThreadLocalSpan().traceId(), status, data, message);
  }

  public MessageError(final int statusCode, final TraceId traceId, final Enum<?> status, final Object data, final String message) {
    this.statusCode = statusCode;
    this.traceId = traceId;
    this.status = status.name();
    this.message = message;
    this.data = data;
  }

  public MessageError() {
    // used by jackson deserialize
  }

  public boolean hasBody() {
    return StringUtil.isNotEmpty(status);
  }

  public static MessageError fromBytes(final DataFormat format, final int statusCode, final byte[] data) {
    final MessageError error = format.fromBytes(data, MessageError.class);
    error.statusCode = statusCode;
    return error;
  }

  public int statusCode() { return statusCode; }
  public TraceId traceId() { return traceId; }
  public String status() { return status; }
  public String message() { return message; }
  public Object data() { return data; }

  // ==============================================================================================================
  //  3xx - Redirection
  // ==============================================================================================================
  private static final LocalizedResource NOT_MODIFIED_LOCALIZED_RESOURCE = new LocalizedResource("message.error.not.modified", "not modified");
  public static MessageError notModified() {
    return notModified(NOT_MODIFIED_LOCALIZED_RESOURCE);
  }

  public static MessageError notModified(final LocalizedResource message, final Object... args) {
    return new MessageError(304, ErrorStatus.NOT_MODIFIED, LocalizedText.INSTANCE.get(message, args));
  }

  // ==============================================================================================================
  //  4xx - Client Error
  // ==============================================================================================================
  public static MessageError newBadRequestError(final LocalizedResource message, final Object... args) {
    return newBadRequestError(ErrorStatus.BAD_REQUEST, message, args);
  }

  public static MessageError newBadRequestError(final Enum<?> status, final LocalizedResource message, final Object... args) {
    return newBadRequestError(status, null, LocalizedText.INSTANCE.get(message, args));
  }

  public static MessageError newBadRequestError(final String message) {
    return newBadRequestError(ErrorStatus.BAD_REQUEST, null, message);
  }

  public static MessageError newBadRequestError(final Enum<?> status, final String message) {
    return newBadRequestError(status, null, message);
  }

  public static MessageError newBadRequestError(final Enum<?> status, final Object data, final LocalizedResource message, final Object... args) {
    return newBadRequestError(status, data, LocalizedText.INSTANCE.get(message, args));
  }

  public static MessageError newBadRequestError(final Enum<?> status, final Object data, final String message) {
    return new MessageError(400, status, data, message);
  }

  public static MessageError newUnauthorized(final LocalizedResource message, final Object... args) {
    return new MessageError(401, ErrorStatus.UNAUTHORIZED, LocalizedText.INSTANCE.get(message, args));
  }

  public static MessageError newForbidden(final LocalizedResource message, final Object... args) {
    return new MessageError(403, ErrorStatus.FORBIDDEN, LocalizedText.INSTANCE.get(message, args));
  }

  private static final LocalizedResource NOT_FOUND_LOCALIZED_RESOURCE = new LocalizedResource("message.error.not.found", "not found");
  public static MessageError notFound() {
    return newNotFound(NOT_FOUND_LOCALIZED_RESOURCE);
  }

  public static MessageError newNotFound(final LocalizedResource message, final Object... args) {
    return new MessageError(404, ErrorStatus.NOT_FOUND, LocalizedText.INSTANCE.get(message, args));
  }

  public static MessageError newNotFound(final Enum<?> status, final Object data, final LocalizedResource message, final Object... args) {
    return new MessageError(404, status, data, LocalizedText.INSTANCE.get(message, args));
  }

  public static MessageError newConflict(final Enum<?> status, final LocalizedResource message, final Object... args) {
    return new MessageError(409, status, LocalizedText.INSTANCE.get(message, args));
  }

  public static MessageError newPreconditionFailed(final Enum<?> status, final LocalizedResource message, final Object... args) {
    return new MessageError(412, status, LocalizedText.INSTANCE.get(message, args));
  }

  public static MessageError newPayloadTooLarge(final Enum<?> status, final LocalizedResource message, final Object... args) {
    return new MessageError(413, status, LocalizedText.INSTANCE.get(message, args));
  }

  public static MessageError newExpectationFailed(final Enum<?> status, final LocalizedResource message, final Object... args) {
    return newExpectationFailed(status, null, message, args);
  }

  public static MessageError newExpectationFailed(final Enum<?> status, final Object data, final LocalizedResource message, final Object... args) {
    return new MessageError(417, status, data, LocalizedText.INSTANCE.get(message, args));
  }

  private static final LocalizedResource TOO_MANY_REQUESTS_LOCALIZED_RESOURCE = new LocalizedResource("message.error.too.many.requests", "too many requests");
  public static MessageError newTooManyRequests() {
    return newTooManyRequests(ErrorStatus.TOO_MANY_REQUESTS, TOO_MANY_REQUESTS_LOCALIZED_RESOURCE);
  }

  public static MessageError newTooManyRequests(final Enum<?> status, final LocalizedResource message, final Object... args) {
    return new MessageError(429, status, LocalizedText.INSTANCE.get(message, args));
  }

  // ==============================================================================================================
  //  5xx - Server Error
  // ==============================================================================================================
  private static final LocalizedResource INTERNAL_SERVER_ERROR_LOCALIZED_RESOURCE = new LocalizedResource("message.error.internal.server.error", "internal server error");
  public static MessageError internalServerError() {
    return newInternalServerError(INTERNAL_SERVER_ERROR_LOCALIZED_RESOURCE);
  }

  public static MessageError newInternalServerError(final LocalizedResource message, final Object... args) {
    return newInternalServerError(LocalizedText.INSTANCE.get(message, args));
  }

  public static MessageError newInternalServerError(final String message) {
    return new MessageError(500, ErrorStatus.INTERNAL_SERVER_ERROR, message);
  }

  public static MessageError newInternalServerError(final Enum<?> status, final LocalizedResource message, final Object... args) {
    return new MessageError(500, status, LocalizedText.INSTANCE.get(message, args));
  }

  public static MessageError newInternalServerError(final Enum<?> status, final String message) {
    return new MessageError(500, status, message);
  }

  private static final LocalizedResource NOT_IMPLEMENTED_LOCALIZED_RESOURCE = new LocalizedResource("message.error.not.implemented", "not implemented");
  public static MessageError notImplemented() {
    return newNotImplemented(NOT_IMPLEMENTED_LOCALIZED_RESOURCE);
  }

  public static MessageError newNotImplemented(final LocalizedResource message, final Object... args) {
    return newNotImplemented(LocalizedText.INSTANCE.get(message, args));
  }

  public static MessageError newNotImplemented(final String message) {
    return new MessageError(501, ErrorStatus.NOT_IMPLEMENTED, message);
  }

  @Override
  public String toString() {
    return "MessageError [status=" + status + ", statusCode=" + statusCode
        + ", traceId=" + traceId + ", message=" + message + ", data=" + data + "]";
  }
}
