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

package io.github.matteobertozzi.rednaco.hashes;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class CryptographicHash extends AbstractHash<CryptographicHash> {
  public enum HashAlgo {
    MD5("MD5"),
    SHA_1("SHA-1"),
    SHA_224("SHA-224"),
    SHA_256("SHA-256"),
    SHA_384("SHA-384"),
    SHA_512("SHA-512"),
    SHA_512_224("SHA-512/224"),
    SHA_512_256("SHA-512/256"),
    SHA3_224("SHA3-224"),
    SHA3_256("SHA3-256"),
    SHA3_384("SHA3-384"),
    SHA3_512("SHA3-512");

    public final String algorithm;

    HashAlgo(final String algorithm) {
      this.algorithm = algorithm;
    }

    public String algorithm() {
      return algorithm;
    }
  }

  public abstract CryptographicHash update(byte[] buf, int off, int len);
  public abstract void digestTo(byte[] buf, int off, int len);
  public abstract byte[] digest();
  public abstract int digestLength();

  public void digestTo(final byte[] buf) {
    digestTo(buf, 0, buf.length);
  }

  public static CryptographicHash of(final HashAlgo algorithm) {
    try {
      return new DigestHash(MessageDigest.getInstance(algorithm.algorithm()));
    } catch (final NoSuchAlgorithmException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static CryptographicHash of(final MessageDigest digest) {
    return new DigestHash(digest);
  }

  private static final class DigestHash extends CryptographicHash {
    private final MessageDigest digest;

    private DigestHash(final MessageDigest digest) {
      this.digest = digest;
    }

    @Override
    public CryptographicHash update(final byte[] buf, final int off, final int len) {
      digest.update(buf, off, len);
      return this;
    }

    @Override
    public void digestTo(final byte[] buf, final int off, final int len) {
      final byte[] hash = digest();
      System.arraycopy(hash, 0, buf, off, Math.min(len, hash.length));
    }

    @Override
    public byte[] digest() {
      return digest.digest();
    }

    @Override
    public int digestLength() {
      return digest.getDigestLength();
    }
  }
}
