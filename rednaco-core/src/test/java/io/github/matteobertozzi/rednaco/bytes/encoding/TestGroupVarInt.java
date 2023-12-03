package io.github.matteobertozzi.rednaco.bytes.encoding;

import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.matteobertozzi.rednaco.bytes.ByteArray;

public class TestGroupVarInt {
  @Test
  public void randEncodeDecode32() {
    final ByteArray buf = new ByteArray(32);
    final Random rand = new Random();
    for (int k = 0; k < 100_000; ++k) {
      final int[] input = new int[rand.nextInt(3, 64)];
      for (int i = 0; i < input.length; ++i) {
        input[i] = rand.nextInt();
      }

      buf.reset();
      GroupVarInt.encode32(buf, IntEncoder.LITTLE_ENDIAN, input, 0, input.length);

      final int[] result = new int[input.length];
      GroupVarInt.decode32(buf.buffer(), 0, IntDecoder.LITTLE_ENDIAN, result, 0, input.length);
      Assertions.assertArrayEquals(input, result);
    }
  }
}
