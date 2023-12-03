package io.github.matteobertozzi.rednaco.bytes.encoding;

import io.github.matteobertozzi.rednaco.bytes.ByteArrayAppender;

public final class GroupVarInt {
  private GroupVarInt() {
    // no-op
  }

  // ====================================================================================================
  //  Group VarInt Int32 (1byte 4sizes + 4 ints) Encode
  // ====================================================================================================
  public static void encode32(final ByteArrayAppender stream, final IntEncoder encoder, final int[] values, int off, int len) {
    while (len >= 4) {
      encodeBlock32(stream, encoder, values[off], values[off + 1], values[off + 2], values[off + 3]);
      len -= 4;
      off += 4;
    }
    switch (len) {
      case 3 -> encodeBlock32(stream, encoder, values[off], values[off + 1], values[off + 2]);
      case 2 -> encodeBlock32(stream, encoder, values[off], values[off + 1]);
      case 1 -> encode32(stream, encoder, values[off]);
    }
  }

  public static void encodeBlock32(final ByteArrayAppender stream, final IntEncoder encoder, final int[] values, final int off, final int len) {
    switch (len) {
      case 4 -> encodeBlock32(stream, encoder, values[off], values[off + 1], values[off + 2], values[off + 3]);
      case 3 -> encodeBlock32(stream, encoder, values[off], values[off + 1], values[off + 2]);
      case 2 -> encodeBlock32(stream, encoder, values[off], values[off + 1]);
      case 1 -> encode32(stream, encoder, values[off]);
      case 0 -> {}
      default -> throw new IllegalArgumentException();
    }
  }

  public static void encode32(final ByteArrayAppender stream, final IntEncoder encoder, final int a) {
    final int aSize = IntUtil.size(a);

    //      6    4    2    0
    // | 11 | 00 | 00 | 00 |
    stream.add((aSize - 1) << 6);
    encoder.writeFixed(stream, a, aSize);
  }

  public static void encodeBlock32(final ByteArrayAppender stream, final IntEncoder encoder, final int a, final int b) {
    final int aSize = IntUtil.size(a);
    final int bSize = IntUtil.size(b);

    //      6    4    2    0
    // | 11 | 11 | 00 | 00 |
    stream.add(((aSize - 1) << 6) | ((bSize - 1) << 4));
    encoder.writeFixed(stream, a, aSize);
    encoder.writeFixed(stream, b, bSize);
  }

  public static void encodeBlock32(final ByteArrayAppender stream, final IntEncoder encoder, final int a, final int b, final int c) {
    final int aSize = IntUtil.size(a);
    final int bSize = IntUtil.size(b);
    final int cSize = IntUtil.size(c);

    //      6    4    2    0
    // | 11 | 11 | 11 | 00 |
    stream.add(((aSize - 1) << 6) | ((bSize - 1) << 4) | ((cSize - 1) << 2));
    encoder.writeFixed(stream, a, aSize);
    encoder.writeFixed(stream, b, bSize);
    encoder.writeFixed(stream, c, cSize);
  }

  public static void encodeBlock32(final ByteArrayAppender stream, final IntEncoder encoder, final int a, final int b, final int c, final int d) {
    final int aSize = IntUtil.size(a);
    final int bSize = IntUtil.size(b);
    final int cSize = IntUtil.size(c);
    final int dSize = IntUtil.size(d);

    //      6    4    2    0
    // | 11 | 11 | 11 | 11 |
    stream.add(((aSize - 1) << 6) | ((bSize - 1) << 4) | ((cSize - 1) << 2) | (dSize - 1));
    encoder.writeFixed(stream, a, aSize);
    encoder.writeFixed(stream, b, bSize);
    encoder.writeFixed(stream, c, cSize);
    encoder.writeFixed(stream, d, dSize);
  }

  // ====================================================================================================
  //  Group VarInt Int32 (1byte 4sizes + 4 ints) Decode
  // ====================================================================================================
  public static void decode32(final byte[] buf, int bufOff, final IntDecoder decoder, final int[] out, int outOff, final int outLen) {
    int avail = outLen;
    while (avail >= 4) {
      final int head = buf[bufOff++] & 0xff;

      final int aSize = 1 + ((head >>> 6) & 3);
      final int bSize = 1 + ((head >>> 4) & 3);
      final int cSize = 1 + ((head >>> 2) & 3);
      final int dSize = 1 + (head & 3);
      out[outOff++] = (int) decoder.readFixed(buf, bufOff, aSize); bufOff += aSize;
      out[outOff++] = (int) decoder.readFixed(buf, bufOff, bSize); bufOff += bSize;
      out[outOff++] = (int) decoder.readFixed(buf, bufOff, cSize); bufOff += cSize;
      out[outOff++] = (int) decoder.readFixed(buf, bufOff, dSize); bufOff += dSize;
      avail -= 4;
    }

    if (avail != 0) {
      final int head = buf[bufOff++] & 0xff;
      switch (avail) {
        case 3 -> {
          final int aSize = 1 + ((head >>> 6) & 3);
          final int bSize = 1 + ((head >>> 4) & 3);
          final int cSize = 1 + ((head >>> 2) & 3);
          out[outOff++] = (int) decoder.readFixed(buf, bufOff, aSize); bufOff += aSize;
          out[outOff++] = (int) decoder.readFixed(buf, bufOff, bSize); bufOff += bSize;
          out[outOff] = (int) decoder.readFixed(buf, bufOff, cSize);
        }
        case 2 -> {
          final int aSize = 1 + ((head >>> 6) & 3);
          final int bSize = 1 + ((head >>> 4) & 3);
          out[outOff++] = (int) decoder.readFixed(buf, bufOff, aSize); bufOff += aSize;
          out[outOff] = (int) decoder.readFixed(buf, bufOff, bSize);
        }
        case 1 -> {
          final int aSize = 1 + ((head >>> 6) & 3);
          out[outOff] = (int) decoder.readFixed(buf, bufOff, aSize);
        }
      }
    }
  }

  // ====================================================================================================
  //  Group VarInt Int64 (3bytes 8 sizes + 8 ints)
  // ====================================================================================================
  // TODO
}
