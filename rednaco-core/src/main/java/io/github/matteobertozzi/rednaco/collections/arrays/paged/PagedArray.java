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

package io.github.matteobertozzi.rednaco.collections.arrays.paged;

import java.util.Arrays;

import io.github.matteobertozzi.rednaco.collections.arrays.ArrayUtil;
import io.github.matteobertozzi.rednaco.collections.arrays.ArrayUtil.ArrayConsumer;
import io.github.matteobertozzi.rednaco.collections.arrays.ObjectArray;
import io.github.matteobertozzi.rednaco.util.BitUtil;

public class PagedArray<T> {
  private static final int DEFAULT_PAGES_GROWTH = 16;

  private final Class<T> classOfT;
  private final int pageSize;

  private int pageItems;
  private int pageCount;
  private T[] lastPage;
  private Object[][] pages;

  public PagedArray(final Class<T> classOfT, final int pageSize) {
    this.classOfT = classOfT;
    this.pageSize = BitUtil.nextPow2(pageSize);
    this.pageItems = 0;
    this.pageCount = 1;
    this.lastPage = ArrayUtil.newArray(pageSize, classOfT);
    this.pages = null;
  }

  public int size() {
    // NOTE: pageCount expected to always be > 0
    return ((pageCount - 1) * pageSize) + pageItems;
  }

  public boolean isEmpty() {
    // NOTE: pageCount expected to always be > 0
    return pageItems == 0 && pageCount == 1;
  }

  public boolean isNotEmpty() {
    return pageItems > 0 || pageCount > 1;
  }

  // ================================================================================
  //  PUBLIC Clear related methods
  // ================================================================================
  public void clear() {
    clear(false);
  }

  public void clear(final boolean forceEviction) {
    this.lastPage = pages != null ? getPage(0) : lastPage;
    this.pageCount = 1;
    this.pageItems = 0;
    if (forceEviction && pages != null) {
      for (int i = 1, n = pages.length; i < n; ++i) {
        this.pages[i] = null;
      }
      this.pages = null;
    }
  }

  // ================================================================================
  //  PUBLIC write related methods
  // ================================================================================
  public void add(final T value) {
    if (pageItems == pageSize) rollPage();
    lastPage[pageItems++] = value;
  }

  public void add(final T[] buf, int off, int len) {
    while (len > 0) {
      int avail = lastPage.length - pageItems;
      if (avail == 0) {
        rollPage();
        avail = pageSize;
      }

      final int copyLen = Math.min(avail, len);
      System.arraycopy(buf, off, lastPage, pageItems, copyLen);
      pageItems += copyLen;
      off += copyLen;
      len -= copyLen;
    }
  }

  public void set(final int index, final T value) {
    final int pageIndex = index / pageSize;
    final int pageOffset = index & (pageSize - 1);
    if (pages == null) {
      lastPage[pageOffset] = value;
    } else {
      pages[pageIndex][pageOffset] = value;
    }
  }

  public void set(final int index, final T[] buf, final int off, final int len) {
    // TODO: speedup
    for (int i = 0; i < len; ++i) {
      set(index + i, buf[off + i]);
    }
  }

  public void fill(final T value) {
    if (pages == null) {
      Arrays.fill(lastPage, value);
    } else {
      for (int i = 0, n = pages.length; i < n; ++i) {
        Arrays.fill(pages[i], value);
      }
    }
  }

  public void fill(final T value, int len) {
    while (len > 0) {
      int avail = lastPage.length - pageItems;
      if (avail == 0) {
        rollPage();
        avail = pageSize;
      }

      final int copyLen = Math.min(avail, len);
      Arrays.fill(lastPage, pageItems, pageItems + copyLen, value);
      pageItems += copyLen;
      len -= copyLen;
    }
  }

  // ================================================================================
  //  PUBLIC read related methods
  // ================================================================================
  public T get(final int index) {
    // TODO: do we need boundary checks
    final int pageIndex = index / pageSize;
    final int pageOffset = index & (pageSize - 1);
    if (pages == null) {
      return ArrayUtil.getItemAt(lastPage, pageOffset);
    }
    return ArrayUtil.getItemAt(pages[pageIndex], pageOffset);
  }

  public void get(final int index, final T[] buf, final int off, final int len) {
    // TODO: speedup
    for (int i = 0; i < len; ++i) {
      buf[off + i] = get(index + i);
    }
  }

  // ================================================================================
  //  PUBLIC forEach
  // ================================================================================
  public int forEach(final ArrayConsumer<T> consumer) {
    if (pages == null) {
      consumer.accept(getLastPage(), 0, pageItems);
      return pageItems;
    }

    return forEach(0, size(), consumer);
  }

  public int forEach(final int off, int len, final ArrayConsumer<T> consumer) {
    if (len == 0) return 0;

    final int pageIndex = off / pageSize;
    final int pageOffset = off & (pageSize - 1);
    if (pageCount == 1) {
      final int avail = Math.min(len, pageItems - pageOffset);
      consumer.accept(getLastPage(), pageOffset, avail);
      return avail;
    }

    int wlen = 0;
    int avail = Math.min(len, pageSize - pageOffset);
    consumer.accept(getPage(pageIndex), pageOffset, avail);
    wlen += avail;
    len -= avail;
    for (int i = pageIndex + 1, n = pageCount - 1; len > 0 && i < n; ++i) {
      avail = Math.min(len, pageSize);
      consumer.accept(getPage(i), 0, avail);
      wlen += avail;
      len -= avail;
    }
    if (len > 0) {
      avail = Math.min(len, pageItems);
      consumer.accept(getLastPage(), 0, avail);
      wlen += avail;
    }
    return wlen;
  }

  private T[] getLastPage() {
    return lastPage;
  }

  @SuppressWarnings("unchecked")
  private T[] getPage(final int index) {
    return (T[])pages[index];
  }

  // ================================================================================
  //  PUBLIC toByteArray related methods
  // ================================================================================
  public T[] toArray() {
    return toArray(0);
  }

  public T[] toArray(final int off) {
    return toArray(off, size() - off);
  }

  public T[] toArray(final int off, final int len) {
    final ObjectArray<T> array = new ObjectArray<>(classOfT, len);
    forEach(off, len, array::add);
    return array.drain();
  }

  // ================================================================================
  //  PRIVATE helpers
  // ================================================================================
  private void rollPage() {
    if (pages == null) {
      pages = new Object[DEFAULT_PAGES_GROWTH][];
      pages[0] = lastPage;
    } else if (pageCount == pages.length) {
      pages = Arrays.copyOf(pages, pages.length + DEFAULT_PAGES_GROWTH);
    }
    lastPage = getPage(pageCount);
    if (lastPage == null) {
      lastPage = ArrayUtil.newArray(pageSize, classOfT);
    }
    pages[pageCount++] = lastPage;
    pageItems = 0;
  }
}
