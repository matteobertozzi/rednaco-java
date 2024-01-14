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

package io.github.matteobertozzi.rednaco.threading;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import io.github.matteobertozzi.rednaco.collections.Hashing;
import io.github.matteobertozzi.rednaco.util.BitUtil;

public class StripedLock<T> {
  private final Cell<T>[] cells;

  @SuppressWarnings("unchecked")
  public StripedLock(final int stripes, final Supplier<T> supplier) {
    this.cells = new Cell[BitUtil.nextPow2(stripes)];
    for (int i = 0; i < cells.length; ++i) {
      this.cells[i] = new Cell<T>(i, supplier.get());
    }
  }

  public int stripes() {
    return this.cells.length;
  }

  public Cell<T> get() {
    final Thread thread = Thread.currentThread();
    //final int index = (int) (Hashing.keyHashCode(thread.threadId()) & (cells.length - 1));
    final int index = Hashing.keyHashCode(thread.hashCode()) & (cells.length - 1);
    return cells[index];
  }

  public Cell<T> get(final Object key) {
    final int index = Hashing.hash32(key) & (cells.length - 1);
    return cells[index];
  }

  public Cell<T> get(final int index) {
    return cells[index];
  }

  public static final class Cell<T> implements Lock {
    private final ReentrantLock lock = new ReentrantLock();
    private final T data;
    private final int index;

    public Cell(final int index, final T data) {
      this.data = data;
      this.index = index;
    }

    public int index() { return index; }
    public T data() { return data; }
    @Override public void lock() { lock.lock(); }
    @Override public void unlock() { lock.unlock(); }
    @Override public boolean tryLock() { return lock.tryLock(); }
    @Override public Condition newCondition() { return lock.newCondition(); }

    @Override
    public void lockInterruptibly() throws InterruptedException {
      lock.lockInterruptibly();
    }
    @Override
    public boolean tryLock(final long timeout, final TimeUnit unit) throws InterruptedException {
      return lock.tryLock(timeout, unit);
    }
  }
}