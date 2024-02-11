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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import io.github.matteobertozzi.rednaco.threading.ShutdownUtil.StopSignal;

public abstract class SpinningThread extends Thread implements StopSignal {
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition waitCond = lock.newCondition();
  private final AtomicBoolean running;

  public SpinningThread(final String name) {
    this(name, new AtomicBoolean(true));
  }

  public SpinningThread(final String name, final AtomicBoolean running) {
    super(name);
    this.running = running;
  }

  public boolean isRunning() {
    return running.get() && isAlive();
  }

  public void wake() {
    if (lock.tryLock()) {
      try {
        waitCond.signal();
      } finally {
        lock.unlock();
      }
    }
  }

  @Override
  public boolean sendStopSignal() {
    running.set(false);
    wake();
    return true;
  }

  @Override
  public void run() {
    lock.lock();
    try {
      runLoop();
    } finally {
      lock.unlock();
    }
  }

  protected void runLoop() {
    while (isRunning()) {
      process();
    }
  }

  protected boolean waitFor(final long time, final TimeUnit unit) {
    lock.lock();
    try {
      return this.waitCond.await(time, unit);
    } catch (final InterruptedException e) {
      Thread.interrupted();
      return false;
    } finally {
      lock.unlock();
    }
  }

  protected abstract void process();
}
