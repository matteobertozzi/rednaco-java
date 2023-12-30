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

package io.github.matteobertozzi.rednaco.dispatcher.routing;

import java.util.Arrays;

import io.github.matteobertozzi.rednaco.bytes.BytesSearch;
import io.github.matteobertozzi.rednaco.bytes.BytesUtil;
import io.github.matteobertozzi.rednaco.dispatcher.routing.RoutesMapping.RouteMapping;
import io.github.matteobertozzi.rednaco.dispatcher.routing.RoutesMapping.RouteMatcher;

public class RouterTrie {
  private static final byte WILDCARD_CHAR = (byte)'*';

  private Node root;

  // ==========================================================================================
  //  Insert related
  // ==========================================================================================
  public void put(final byte[] path, final RouteMapping mapping) {
    root = put(root, path, 0, mapping);
  }

  private static Node put(final Node node, final byte[] path, int offset, final RouteMapping mapping) {
    if (node == null) {
      return newLeafNode(path, offset, mapping);
    }

    if (node instanceof final LeafNode leaf) {
      if (leaf.pathEquals(path)) {
        return node;
      }

      final int p = prefix(leaf.path, offset, path, offset);
      final InternalNode parentNode = new InternalNode4();
      parentNode.setPrefix(path, offset, p);

      offset += parentNode.prefixLength();
      parentNode.add(path[offset], newLeafNode(path, offset + 1, mapping));
      parentNode.add(leaf.path[offset], leaf);
      return parentNode;
    }

    final InternalNode internalNode = (InternalNode)node;
    if (path[offset] == WILDCARD_CHAR) {
      final Node newNode = put(internalNode.get(WILDCARD_CHAR), path, offset + 1, mapping);
      internalNode.add(path[offset], newNode);
      return internalNode;
    }

    final int p = prefix(internalNode.prefix, 0, path, offset);
    if (p != internalNode.prefixLength()) {
      final InternalNode newNode = new InternalNode4();
      newNode.add(path[offset + p], newLeafNode(path, offset + p + 1, mapping));
      newNode.add(internalNode.prefix[p], node);

      newNode.setPrefix(internalNode.prefix, 0, p);
      internalNode.setPrefix(internalNode.prefix, p + 1, internalNode.prefixLength() - (p + 1));
      return newNode;
    }

    offset += internalNode.prefixLength();
    final Node next = internalNode.get(path[offset]);
    if (next == null) {
      final InternalNode parent = internalNode.isFull() ? grow(internalNode) : internalNode;
      final Node newNode = newLeafNode(path, offset + 1, mapping);
      parent.add(path[offset], newNode);
      return parent;
    }

    final Node newNode = put(next, path, offset + 1, mapping);
    internalNode.add(path[offset], newNode);
    return node;
  }

  private static Node newLeafNode(final byte[] path, final int offset, final RouteMapping mapping) {
    final int wildcardIndex = BytesSearch.indexOf(path, offset, WILDCARD_CHAR);
    if (wildcardIndex < 0) {
      return new LeafNode(path, mapping);
    }

    final InternalNode internalNode = new InternalNode4();
    internalNode.setPrefix(path, offset, wildcardIndex - offset);
    internalNode.add(WILDCARD_CHAR, newLeafNode(path, wildcardIndex + 1, mapping));
    return internalNode;
  }

  private static int prefix(final byte[] aPath, final int aOff, final byte[] bPath, final int bOff) {
    return Arrays.mismatch(aPath, aOff, aPath.length, bPath, bOff, bPath.length);
  }

  // ==========================================================================================
  //  Search related
  // ==========================================================================================
  public RouteMatcher get(final String path) {
    return switch (root) {
      case null -> null;
      case final LeafNode leaf -> leaf.matches(path);
      case final InternalNode internalNode -> searchInternal(internalNode, path, 0);
      default -> throw new IllegalArgumentException("Unexpected value");
    };
  }

  private static RouteMatcher searchInternal(final InternalNode internalNode, final String path, int offset) {
    if (internalNode.prefixMismatch(path, offset)) {
      return null;
    }

    offset += internalNode.prefixLength();
    final RouteMatcher matcher = switch (internalNode.get(offset < path.length() ? (byte)path.charAt(offset) : 0)) {
      case null -> null;
      case final LeafNode leaf -> leaf.matches(path);
      case final InternalNode internalNext -> searchInternal(internalNext, path, offset + 1);
      default -> throw new IllegalArgumentException("Unexpected value");
    };

    if (matcher != null) return matcher;
    return switch (internalNode.get(WILDCARD_CHAR)) {
      case null -> null;
      case final LeafNode leaf -> leaf.matches(path);
      case final InternalNode internalNext -> searchWildcardInternal(internalNext, path, offset);
      default -> throw new IllegalArgumentException("Unexpected value");
    };
  }

  private static RouteMatcher searchWildcardInternal(final InternalNode internalNext, final String path, int offset) {
    // skip this path part
    final int length = path.length();
    while (offset < length && path.charAt(offset) != '/') {
      offset++;
    }

    final RouteMatcher matcher = searchInternal(internalNext, path, offset);
    if (matcher != null) return matcher;

    // match regex wildcard
    final Node leafPatternNode = internalNext.get((byte)0);
    if (leafPatternNode instanceof final LeafNode leafPattern) {
      return leafPattern.matches(path);
    }
    return null;
  }

  // ==========================================================================================
  //  Internal Node related
  // ==========================================================================================
  private interface Node {}

  private static InternalNode grow(final InternalNode node) {
    return switch (node) {
      case final InternalNode4 node4 -> new InternalNode8(node4);
      case final InternalNode8 node8 -> new InternalNode16(node8);
      case final InternalNode16 node16 -> new InternalNode32(node16);
      case final InternalNode32 node32 -> new InternalNode64(node32);
      case final InternalNode64 node64 -> new InternalNode80(node64);
      default -> throw new IllegalStateException();
    };
  }

  private static abstract class InternalNode implements Node {
    private byte[] prefix;

    protected InternalNode() {}

    protected InternalNode(final InternalNode node) {
      this.prefix = node.prefix;
    }

    final int prefixLength() {
      return prefix.length;
    }

    final void setPrefix(final byte[] path, final int off, final int len) {
      prefix = new byte[len];
      System.arraycopy(path, off, prefix, 0, len);
    }

    final boolean prefixMismatch(final String path, final int offset) {
      final byte[] localPrefix = prefix;
      final int len = Math.min(localPrefix.length, path.length() - offset);
      for (int i = 0; i < len; ++i) {
        if (path.charAt(offset + i) != (localPrefix[i] & 0xff)) {
          return true;
        }
      }
      return false;
    }

    abstract int size();
    abstract boolean isFull();
    abstract Node get(final byte c);
    abstract void add(final byte c, final Node node);

    abstract int getKey(int index);
    abstract Node getNode(int index);
  }

  private static class InternalNode4 extends InternalMapNode {
    InternalNode4() {
      super(4);
    }
  }

  private static class InternalNode8 extends InternalMapNode {
    InternalNode8(final InternalNode4 other) {
      super(8, other);
    }
  }

  private static class InternalNode16 extends InternalMapNode {
    InternalNode16(final InternalNode8 other) {
      super(16, other);
    }
  }

  private static class InternalNode32 extends InternalMapNode {
    InternalNode32(final InternalNode16 other) {
      super(32, other);
    }
  }

  private static class InternalNode64 extends InternalMapNode {
    InternalNode64(final InternalNode32 other) {
      super(64, other);
    }
  }

  private static class InternalNode80 extends InternalMapNode {
    InternalNode80(final InternalNode64 other) {
      super(80, other);
    }
  }

  private static class InternalMapNode extends InternalNode {
    private final Node[] children;
    private final byte[] keys;
    private int count;

    InternalMapNode(final int size) {
      this.keys = new byte[size];
      this.children = new Node[size];
    }

    InternalMapNode(final int size, final InternalNode other) {
      super(other);
      this.keys = new byte[size];
      this.children = new Node[size];
      count = other.size();
      for (int i = 0; i < count; ++i) {
        keys[i] = (byte)other.getKey(i);
        children[i] = other.getNode(i);
      }
    }

    @Override
    int size() {
      return count;
    }

    @Override
    boolean isFull() {
      return count == keys.length;
    }

    @Override
    Node get(final byte c) {
      for (int i = 0; i < count; ++i) {
        if (c == keys[i]) {
          return children[i];
        }
      }
      return null;
    }

    @Override
    void add(final byte c, final Node node) {
      for (int i = 0; i < count; ++i) {
        if (c == keys[i]) {
          children[i] = node;
          return;
        }
      }
      keys[count] = c;
      children[count] = node;
      count++;
    }

    @Override
    int getKey(final int index) {
      return keys[index];
    }

    @Override
    Node getNode(final int index) {
      return children[index];
    }
  }

  // ==========================================================================================
  //  Leaf Node related
  // ==========================================================================================
  private record LeafNode(byte[] path, RouteMapping mapping) implements Node {
    public RouteMatcher matches(final String searchPath) {
      return mapping.match(searchPath);
    }

    public boolean pathEquals(final byte[] other) {
      return BytesUtil.equals(path, other);
    }
  }
}
