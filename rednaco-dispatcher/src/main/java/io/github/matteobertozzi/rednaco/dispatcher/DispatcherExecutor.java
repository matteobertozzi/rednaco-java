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

import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

import io.github.matteobertozzi.easerinsights.DatumUnit;
import io.github.matteobertozzi.easerinsights.logging.Logger;
import io.github.matteobertozzi.easerinsights.metrics.MetricDimension;
import io.github.matteobertozzi.easerinsights.metrics.Metrics;
import io.github.matteobertozzi.easerinsights.metrics.collectors.Heatmap;
import io.github.matteobertozzi.easerinsights.metrics.collectors.Histogram;
import io.github.matteobertozzi.easerinsights.metrics.collectors.TimeRangeDrag;
import io.github.matteobertozzi.easerinsights.tracing.Span;
import io.github.matteobertozzi.easerinsights.tracing.Tracer;
import io.github.matteobertozzi.rednaco.dispatcher.MessageDispatcher.DispatcherContext;
import io.github.matteobertozzi.rednaco.dispatcher.message.Message;
import io.github.matteobertozzi.rednaco.dispatcher.message.MessageError;
import io.github.matteobertozzi.rednaco.dispatcher.message.MessageException;
import io.github.matteobertozzi.rednaco.dispatcher.message.MessageUtil;
import io.github.matteobertozzi.rednaco.dispatcher.routing.RoutesMapping.RouteMatcher;
import io.github.matteobertozzi.rednaco.dispatcher.routing.UriMessage;
import io.github.matteobertozzi.rednaco.strings.StringConverter;
import io.github.matteobertozzi.rednaco.util.Verify.VerifyArgInvalidArgumentException;

class DispatcherExecutor {
  private static final MetricDimension<Heatmap> globalExecTime = Metrics.newCollectorWithDimensions()
    .dimensions("type")
    .unit(DatumUnit.NANOSECONDS)
    .name("message.dispatcher.exec.time")
    .label("Message Dispatcher Exec Time")
    .register(() -> Heatmap.newMultiThreaded(60, 1, TimeUnit.MINUTES, Histogram.DEFAULT_DURATION_BOUNDS_NS));

  private static final MetricDimension<TimeRangeDrag> globalQueueLength = Metrics.newCollectorWithDimensions()
    .dimensions("type")
    .unit(DatumUnit.COUNT)
    .name("message.dispatcher.queue.length")
    .label("Message Dispatcher Queue Length over time")
    .register(() -> TimeRangeDrag.newMultiThreaded(60, 1, TimeUnit.MINUTES));

  public static DispatcherExecutor inline() {
    return new DispatcherExecutor("inline");
  }

  public static DispatcherExecutor of(final String name, final ExecutorService executors) {
    return new DispatcherExecutorService(name, executors);
  }

  private final Heatmap execTime;

  private DispatcherExecutor(final String name) {
    this.execTime = globalExecTime.get(name);
  }

  public Message submit(final DispatcherContext ctx, final RouteMatcher mapping, final UriMessage message) {
    return execTask(ctx, mapping, message);
  }

  private final long QUEUE_EXEC_ABORT_NS = TimeUnit.MILLISECONDS.toNanos(StringConverter.toLong(System.getProperty("rednaco.dispatcher.queue.exec.timeout.ms"), 10_000));
  public Message execTask(final DispatcherContext ctx, final RouteMatcher mapping, final UriMessage message) {
    final long startTime = System.nanoTime();
    ctx.stats().execStartNs(startTime);
    try {
      if ((ctx.stats().queuePushNs() - startTime) > QUEUE_EXEC_ABORT_NS) {
        return MessageUtil.newErrorMessage(MessageError.newTooManyRequests());
      }

      return mapping.executor().execute(ctx, message);
    } catch (final MessageException e) {
      final MessageError error = e.getMessageError();
      if (error.statusCode() >= 200 && error.statusCode() <= 399 || error.statusCode() == 404) {
        Logger.error("execution terminated with {status} {} {}: {}", error.status(), message.method(), message.path(), error);
      } else {
        Logger.error("execution failed with {status} {} {}: {}", error.status(), message.method(), message.path(), error);
      }
      return MessageUtil.newErrorMessage(error);
    } catch (final VerifyArgInvalidArgumentException e) {
      Logger.error("Verify arg found an illegal argument {} {}: {}", message.method(), message.path(), e.getMessage());
      return MessageUtil.newErrorMessage(MessageError.newBadRequestError(e.getMessage()));
    } catch (final FileNotFoundException e) {
      Logger.error("file not found {} {}: {}", message.method(), message.path(), e.getMessage());
      return MessageUtil.newErrorMessage(MessageError.notFound());
    } catch (final Throwable e) {
      Logger.error(e, "execution failed {} {}", message.method(), message.path());
      return MessageUtil.newErrorMessage(MessageError.internalServerError());
    } finally {
      ctx.stats().setExecEndNs(System.nanoTime());
      execTime.sample(ctx.stats().execTimeNs());
    }
  }

  private static final class DispatcherExecutorService extends DispatcherExecutor {
    private final AtomicLongArray execTimes = new AtomicLongArray(64);
    private final AtomicLong execTimesOffset = new AtomicLong();
    private final AtomicInteger queueSize = new AtomicInteger();
    private final TimeRangeDrag queueLength;
    private final ExecutorService executor;

    private DispatcherExecutorService(final String name, final ExecutorService executor) {
      super(name);
      this.queueLength = globalQueueLength.get(name);
      this.executor = executor;

      // 100ms as default latency
      for (int i = 0; i < 64; ++i) {
        execTimes.set(i, 100_000_000);
      }
    }

    @Override
    public Message submit(final DispatcherContext ctx, final RouteMatcher mapping, final UriMessage message) {
      final Span span = Tracer.getThreadLocalSpan();
      final int qSize = incQueueSize();
      //if (qSize > 200 && (qSize * expectedLatency()) > 10000) {
      //  return tooManyRequests();
      //}

      message.retain();
      executor.submit(() -> runTask(span, ctx, mapping, message));
      return null;
    }

    private Message tooManyRequests() {
      decQueueSize();
      return MessageUtil.newErrorMessage(MessageError.newTooManyRequests());
    }

    private void runTask(final Span parentSpan, final DispatcherContext ctx, final RouteMatcher mapping, final UriMessage message) {
      try (Span span = Tracer.newSpan(parentSpan)) {
        decQueueSize();

        final Message response = execTask(ctx, mapping, message);
        MessageRecorder.record(message, response, ctx.stats());

        message.release();
        ctx.writeAndFlush(response);

        addExecTime(ctx.stats().execTimeNs());
      }
    }

    private int incQueueSize() {
      queueLength.inc();
      return queueSize.incrementAndGet();
    }

    private void decQueueSize() {
      queueLength.dec();
      queueSize.decrementAndGet();
    }

    private void addExecTime(final long execTime) {
      final int index = (int) (execTimesOffset.incrementAndGet() & 63);
      execTimes.set(index, execTime);
    }

    private long expectedLatency() {
      /*
      final long[] sortedTimes = new long[64];
      for (int i = 0; i < sortedTimes.length; ++i) {
        sortedTimes[i] = execTimes.get(i);
      }
      Arrays.sort(sortedTimes);
      return sortedTimes[57];
       */
      long sum = 0;
      for (int i = 0; i < 64; ++i) {
        sum += execTimes.get(i);
      }
      return sum / 64;
    }
  }
}
