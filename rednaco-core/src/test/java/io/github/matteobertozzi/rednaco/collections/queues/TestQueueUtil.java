package io.github.matteobertozzi.rednaco.collections.queues;

import java.util.ArrayDeque;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestQueueUtil {
  @Test
  public void testLength() {
    Assertions.assertEquals(0, QueueUtil.size(null));
    Assertions.assertEquals(0, QueueUtil.size(new ArrayBlockingQueue<>(1)));
    Assertions.assertEquals(1, QueueUtil.size(new ArrayDeque<>(List.of("a"))));
    Assertions.assertEquals(2, QueueUtil.size(new LinkedBlockingDeque<>(List.of("a", "b"))));
  }

  @Test
  public void testEmpty() {
    Assertions.assertTrue(QueueUtil.isEmpty(null));
    Assertions.assertTrue(QueueUtil.isEmpty(new ArrayBlockingQueue<>(1)));
    Assertions.assertFalse(QueueUtil.isEmpty(new ArrayDeque<>(List.of("a"))));

    Assertions.assertFalse(QueueUtil.isNotEmpty(null));
    Assertions.assertFalse(QueueUtil.isNotEmpty(new LinkedBlockingQueue<>()));
    Assertions.assertTrue(QueueUtil.isNotEmpty(new ArrayDeque<>(List.of("a", "b", "c"))));
  }
}
