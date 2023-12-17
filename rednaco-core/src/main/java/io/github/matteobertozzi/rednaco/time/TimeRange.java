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

package io.github.matteobertozzi.rednaco.time;

import java.util.function.IntConsumer;

public class TimeRange {
  private final int window;
  private long lastInterval;
  private long next;

  public TimeRange(final int window, final long timestamp) {
    this.window = window;
    this.lastInterval = TimeUtil.alignToWindow(timestamp, window);
    this.next = 0;
  }

  public int window() {
    return window;
  }

  public long lastInterval() {
    return lastInterval;
  }

  public int size(final int slotsCount) {
    return (int) Math.min(next + 1, slotsCount);
  }

  public void copy(final int slotsCount, final int length, final CopyTimeRangeSlots copyFunc) {
    final int eofIndex = 1 + (int) (next % slotsCount);
    //System.out.println(" ---> COPY - next:" + next + " length:" + length + " eofIndex:" + eofIndex);
    if (next >= length) {
      // 5, 6, 7, 8, 1, 2, 3, 4
      copyFunc.copyTimeRangeSlots(0, eofIndex, slotsCount);
      copyFunc.copyTimeRangeSlots(slotsCount - eofIndex, 0, eofIndex);
    } else {
      //System.out.println("eofIndex: " + eofIndex + " -> next:" + next + "/" + length);
      copyFunc.copyTimeRangeSlots(0, 0, eofIndex); // 1, 2, 3, 4
    }
  }

  public void iterReverse(final int slotsCount, final IntConsumer consumer) {
    final int eofIndex = (int) (next % slotsCount);
    if (next >= slotsCount) {
      for (int i = eofIndex; i >= 0; --i) {
        consumer.accept(i);
      }
      for (int i = slotsCount - 1; i > eofIndex; --i) {
        consumer.accept(i);
      }
    } else {
      for (int i = eofIndex; i >= 0; --i) {
        consumer.accept(i);
      }
    }
  }

  public void update(final long timestamp, final int totalSlots, final ResetTimeRangeSlots resetFunc) {
    update(timestamp, totalSlots, resetFunc, TimeRange::noOpUpdate);
  }

  public void update(final long timestamp, final int totalSlots, final ResetTimeRangeSlots resetFunc, final UpdateTimeRangeSlot updateFunc) {
    final long alignedTs = TimeUtil.alignToWindow(timestamp, window);
    final long deltaTime = alignedTs - lastInterval;
    //System.out.println(" -> TS DELTA: " + deltaTime + " -> " + lastInterval + "/" + timestamp);

    // update the current slot
    if (deltaTime == 0) {
      //System.out.println(" ----> update current slots: " + (next % totalSlots));
      updateFunc.updateTimeRangeSlot((int)(next % totalSlots));
      return;
    }

    if (deltaTime == window) {
      lastInterval = alignedTs;
      final int index = (int)(++next % totalSlots);
      //System.out.println(" ----> update next slots: " + index);
      resetFunc.resetTimeRangeSlots(index, index + 1);
      updateFunc.updateTimeRangeSlot(index);
      return;
    }

    // inject slots
    if (deltaTime > 0) {
      injectSlots(deltaTime, totalSlots, resetFunc);
      //System.out.println(" ----> inject slots: " + (next % totalSlots));
      lastInterval = alignedTs;
      updateFunc.updateTimeRangeSlot((int)(next % totalSlots));
      return;
    }

    // update past slot
    final int availSlots = (int) Math.min(next + 1, totalSlots);
    final int pastIndex = (int) (-deltaTime / window);
    if (pastIndex >= availSlots) {
      // ignore, too far in the past
      //System.out.println(" ----> ignore, too far in the past");
      return;
    }

    //System.out.println(" ----> update past slots. " + availSlots + "/" + pastIndex);
    updateFunc.updateTimeRangeSlot((int)((next - pastIndex) % totalSlots));
  }

  private void injectSlots(final long deltaTime, final int totalSlots, final ResetTimeRangeSlots resetFunc) {
    final int slots = (int) (deltaTime / window);
    if (slots >= totalSlots) {
      //System.out.println(" ----> clear");
      resetFunc.resetTimeRangeSlots(0, totalSlots);
      next += slots;
      return;
    }

    final int fromIndex = (int) ((next + 1) % totalSlots);
    final int toIndex = (int) ((next + 1 + slots) % totalSlots);
    if (fromIndex < toIndex) {
      //System.out.println(" ----> inject " + fromIndex + "-" + toIndex);
      resetFunc.resetTimeRangeSlots(fromIndex, toIndex);
    } else {
      //System.out.println(" ----> inject " + fromIndex + "-" + totalSlots + " + 0-" + toIndex);
      resetFunc.resetTimeRangeSlots(fromIndex, totalSlots);
      resetFunc.resetTimeRangeSlots(0, toIndex);
    }
    next += slots;
  }

  private static void noOpUpdate(final int slotIndex) {
    // no-op
  }

  @FunctionalInterface
  public interface UpdateTimeRangeSlot {
    void updateTimeRangeSlot(int slotIndex);
  }

  @FunctionalInterface
  public interface ResetTimeRangeSlots {
    void resetTimeRangeSlots(int fromIndex, int toIndex);
  }

  @FunctionalInterface
  public interface CopyTimeRangeSlots {
    void copyTimeRangeSlots(int dstIndex, int srcFromIndex, int srcToIndex);
  }
}
