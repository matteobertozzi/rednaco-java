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

package io.github.matteobertozzi.rednaco.collections.queues;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public final class QueueUtil {
  private QueueUtil() {
    // no-op
  }

  public static <T> boolean isEmpty(final Queue<T> queue) {
    return queue == null || queue.isEmpty();
  }

  public static <T> boolean isNotEmpty(final Queue<T> queue) {
    return queue != null && !queue.isEmpty();
  }

  public static <T> int size(final Queue<T> queue) {
    return queue != null ? queue.size() : 0;
  }

  public static <T> boolean putWithoutInterrupt(final BlockingQueue<T> queue, final T item) {
    try {
      queue.put(item);
      return true;
    } catch (final InterruptedException e) {
      Thread.interrupted();
      return false;
    }
  }

  public static <T> T pollWithoutInterrupt(final BlockingQueue<T> queue, final long timeout, final TimeUnit unit) {
    try {
      return queue.poll(timeout, unit);
    } catch (final InterruptedException e) {
      Thread.interrupted();
      return null;
    }
  }
}
