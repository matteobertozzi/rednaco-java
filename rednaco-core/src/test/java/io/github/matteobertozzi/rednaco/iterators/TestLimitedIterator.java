package io.github.matteobertozzi.rednaco.iterators;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.matteobertozzi.rednaco.collections.iterators.LimitedIterator;

public class TestLimitedIterator {
  @Test
  public void testLimitedIterator() {
    final List<String> items = List.of("aaa", "bbb", "ccc", "ddd", "eee");

    final LimitedIterator<String> it = new LimitedIterator<>(3, items.iterator());
    Assertions.assertTrue(it.hasNext());
    for (int i = 0; i < 3; ++i) {
      Assertions.assertTrue(it.hasNext());
      Assertions.assertEquals(items.get(i), it.next());
    }
    Assertions.assertFalse(it.hasNext());
  }
}
