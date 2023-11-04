package io.github.matteobertozzi.rednaco.threading;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class TestSpinningThread {
  @Test
  @Timeout(value = 5, unit = TimeUnit.SECONDS)
  public void testSpinningThread() throws Exception {
    final ConsumerThread consumerThread = new ConsumerThread("TestSpinningThread");
    try {
      consumerThread.start();

      final ArrayList<Item> items = new ArrayList<>(100);
      for (int i = 0; i < 100; ++i) {
        final Item item = new Item(i);
        items.add(item);
        consumerThread.add(item);
      }

      for (final Item item: items) {
        while (!item.isProcessed()) {
          Thread.sleep(50);
        }
      }
    } finally {
      consumerThread.sendStopSignal();
      ThreadUtil.shutdown(consumerThread);
    }
  }

  public static class Item {
    private final long value;
    private boolean processed;

    public Item(final long value) {
      this.processed = false;
      this.value = value;
    }

    private long value() {
      return value;
    }

    public boolean isProcessed() {
      return processed;
    }

    public void markProcessed() {
      if (processed) {
        throw new IllegalStateException("item already processed");
      }
      this.processed = true;
    }
  }

  private static final class ConsumerThread extends SpinningThread {
    private final ArrayBlockingQueue<Item> queue = new ArrayBlockingQueue<>(16);

    public ConsumerThread(final String name) {
      super(name);
    }

    public ConsumerThread(final String name, final AtomicBoolean running) {
      super(name, running);
    }

    public void add(final Item item) throws InterruptedException {
      queue.put(item);
    }

    @Override
    protected void process() {
      final Item item = ThreadUtil.poll(queue, 1, TimeUnit.SECONDS);
      if (item == null) return;

      item.markProcessed();
    }
  }
}
