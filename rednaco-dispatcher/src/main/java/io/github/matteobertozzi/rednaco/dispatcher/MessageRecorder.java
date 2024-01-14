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

import io.github.matteobertozzi.easerinsights.logging.LogBuffer;
import io.github.matteobertozzi.easerinsights.logging.LogProvider.LogEntry;
import io.github.matteobertozzi.easerinsights.tracing.Span;
import io.github.matteobertozzi.easerinsights.tracing.Tracer;
import io.github.matteobertozzi.rednaco.dispatcher.message.Message;
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
  }

  public static void record(final UriMessage request, final Message response, final MessageStats stats) {

  }

  private static long millisToHumanDate(final long millis) {
    final ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC);
    return (zdt.getYear() * 100_00_00_00_00L) + (zdt.getMonthValue() * 100_00_00_00L) + (zdt.getDayOfMonth() * 100_00_00)
        + (zdt.getHour() * 100_00) + (zdt.getMinute() * 100) + zdt.getSecond();
  }
}
