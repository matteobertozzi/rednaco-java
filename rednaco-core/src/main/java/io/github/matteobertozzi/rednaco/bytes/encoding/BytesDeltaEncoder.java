package io.github.matteobertozzi.rednaco.bytes.encoding;

import io.github.matteobertozzi.rednaco.bytes.ByteArraySlice;
import io.github.matteobertozzi.rednaco.bytes.BytesSearch;

public class BytesDeltaEncoder {
  private final byte[] lastValue;
  private int length;

  public BytesDeltaEncoder(final int maxValueLength) {
    this.lastValue = new byte[maxValueLength];
    this.length = 0;
  }

  public void reset() {
    this.length = 0;
  }

  public int add(final ByteArraySlice value) {
    return add(value.rawBuffer(), value.offset(), value.length());
  }

  public int add(final byte[] buf) {
    return add(buf, 0, buf.length);
  }

  public int add(final byte[] buf, final int off, final int len) {
    final int prefix = BytesSearch.prefix(lastValue, 0, length, buf, off, len);
    System.arraycopy(buf, off, lastValue, 0, len);
    this.length = len;
    return prefix;
  }

  public ByteArraySlice getValue() {
    return new ByteArraySlice(lastValue, 0, length);
  }
}
