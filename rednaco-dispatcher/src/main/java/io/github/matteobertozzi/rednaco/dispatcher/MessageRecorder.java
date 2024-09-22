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

package io.github.matteobertozzi.rednaco.dispatcher;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.matteobertozzi.easerinsights.logging.LogBuffer;
import io.github.matteobertozzi.easerinsights.logging.LogProvider.LogEntry;
import io.github.matteobertozzi.easerinsights.logging.Logger;
import io.github.matteobertozzi.easerinsights.tracing.Span;
import io.github.matteobertozzi.easerinsights.tracing.Tracer;
import io.github.matteobertozzi.rednaco.bytes.BytesUtil;
import io.github.matteobertozzi.rednaco.data.DataFormat;
import io.github.matteobertozzi.rednaco.data.json.JsonUtil;
import io.github.matteobertozzi.rednaco.dispatcher.message.Message;
import io.github.matteobertozzi.rednaco.dispatcher.message.MessageFile;
import io.github.matteobertozzi.rednaco.dispatcher.message.MessageUtil;
import io.github.matteobertozzi.rednaco.dispatcher.message.MessageUtil.EmptyMessage;
import io.github.matteobertozzi.rednaco.dispatcher.message.MessageUtil.ErrorMessage;
import io.github.matteobertozzi.rednaco.dispatcher.message.MessageUtil.TypedMessage;
import io.github.matteobertozzi.rednaco.dispatcher.routing.UriMessage;
import io.github.matteobertozzi.rednaco.time.TimeUtil;

public final class MessageRecorder {
  public record MessageReqLogEntry(
    long nanos, long hstamp,
    String traceId, String spanId, String thread, String level,
    String method, String uri, List<Map.Entry<String, String>> headers
  ) implements LogEntry {
    public MessageReqLogEntry(final String method, final String uri, final List<Map.Entry<String, String>> headers) {
      this(Tracer.getThreadLocalSpan(), TimeUtil.currentEpochNanos(), method, uri, headers);
    }

    public MessageReqLogEntry(final Span span, final long nanos, final String method, final String uri, final List<Map.Entry<String, String>> headers) {
      this(nanos, millisToHumanDate(nanos / 1_000_000L),
        span.traceId().toString(), span.spanId().toString(), Thread.currentThread().getName(),
        "REQUEST", method, uri, headers);
    }

    @Override
    public void writeTextTo(final LogBuffer buffer) {
      // no-op
    }
  }

  public record MessageRespLogEntry(
    long nanos, long hstamp,
    //long queueTime, long execTime,
    String traceId, String spanId, String thread, String level,
    int statusCode, String method, String uri, List<Map.Entry<String, String>> headers
  ) implements LogEntry {
    public MessageRespLogEntry(final int statusCode, final String method, final String uri, final List<Map.Entry<String, String>> headers) {
      this(Tracer.getThreadLocalSpan(), TimeUtil.currentEpochNanos(), statusCode, method, uri, headers);
    }

    public MessageRespLogEntry(final Span span, final long nanos, final int statusCode, final String method, final String uri, final List<Map.Entry<String, String>> headers) {
      this(nanos, millisToHumanDate(nanos / 1_000_000L),
        span.traceId().toString(), span.spanId().toString(), Thread.currentThread().getName(),
        "RESPONSE", statusCode, method, uri, headers);
    }

    @Override
    public void writeTextTo(final LogBuffer buffer) {
      // no-op
    }
  }

  private MessageRecorder() {
    // no-op
  }

  public static void record(final UriMessage request) {
    Logger.debug("----------\nREQUEST: {} {}\nQUERY: {}\nHEADERS: {}\nBODY: {}\n----------",
      request.method(), request.path(),
      request.queryParams(),
      request.metadata(),
      contentToString(request));
  }

  public static void record(final UriMessage request, final Message response, final MessageStats stats) {
    if (response == null) {
      Logger.debug(new Exception(), "----------\nRESPONSE: {} {} - NONE\nSTATS: {}\n-----", request.method(), request.path(), stats);
      return;
    }
    try {
      Logger.debug("----------\nRESPONSE: {} {} {}\nSTATS: {}\nHEADERS: {}\nBODY: {}\n----------",
        httpStatus(response), request.method(), request.path(), stats,
        response.metadata(), contentToString(response));
    } catch (final Throwable e) {
      Logger.error(e, "unable to dump response {} {} {}", request.method(), request.path(), response.metadata());
    }
  }

  private static String httpStatus(final Message response) {
    final String status = response.metadataValue(MessageUtil.METADATA_FOR_HTTP_STATUS);
    if (status != null) return status;

    return switch (response) {
      case final EmptyMessage emptyResult -> "204";
      case final ErrorMessage errorResult -> String.valueOf(errorResult.error().statusCode());
      default -> "200";
    };
  }

  private static String contentToString(final Message message) {
    final String r = switch (message) {
      case final TypedMessage<?> objResult -> JsonUtil.toJson(objResult.content());
      case final EmptyMessage emptyResult -> "NO-CONTENT";
      case final ErrorMessage errorResult -> JsonUtil.toJson(errorResult.error());
      case final MessageFile fileResult -> "file:" + fileResult.path();
      default -> {
        if (!message.hasContent()) {
          yield "NO-CONTENT";
        }

        final String contentType = message.metadataValue(MessageUtil.METADATA_CONTENT_TYPE);
        final DataFormat dataFormat = MessageUtil.parseContentType(contentType, null);
        if (dataFormat != null) {
          yield message.convertContent(dataFormat, JsonNode.class).toString();
        } else if (contentType != null && contentType.startsWith("text/")) {
          yield new String(message.convertContentToBytes());
        }
        yield BytesUtil.toHexString(message.convertContentToBytes());
      }
    };
    final int PACKET_DUMP_LIMIT = 128 << 10;
    return r.length() > PACKET_DUMP_LIMIT ? r.substring(0, PACKET_DUMP_LIMIT) : r;
  }

  private static long millisToHumanDate(final long millis) {
    final ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC);
    return (zdt.getYear() * 100_00_00_00_00L) + (zdt.getMonthValue() * 100_00_00_00L) + (zdt.getDayOfMonth() * 100_00_00)
        + (zdt.getHour() * 100_00) + (zdt.getMinute() * 100) + zdt.getSecond();
  }
}
