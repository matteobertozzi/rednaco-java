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

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import io.github.matteobertozzi.rednaco.bytes.BytesUtil;

public class HMac extends AbstractHash<HMac> {
  public enum HMacAlgo {
    SHA_1("HmacSHA1"),
    SHA_224("HmacSHA224"),
    SHA_256("HmacSHA256"),
    SHA_384("HmacSHA384"),
    SHA_512("HmacSHA512"),
    SHA_512_224("HmacSHA512/224"),
    SHA_512_256("HmacSHA512/256"),
    SHA3_224("HmacSHA3-224"),
    SHA3_256("HmacSHA3-256"),
    SHA3_384("HmacSHA3-384"),
    SHA3_512("HmacSHA3-512");

    public final String algorithm;

    HMacAlgo(final String algorithm) {
      this.algorithm = algorithm;
    }

    public String algorithm() {
      return algorithm;
    }
  }

  private final Mac hmac;

  private HMac(final Mac hmac) {
    this.hmac = hmac;
  }

  public static HMac of(final HMacAlgo algo, final byte[] key) {
    return of(algo, new SecretKeySpec(key, algo.algorithm()));
  }

  public static HMac of(final HMacAlgo algo, final Key key) {
    try {
      final Mac mac = Mac.getInstance(algo.algorithm());
      mac.init(key);
      return new HMac(mac);
    } catch (final NoSuchAlgorithmException | InvalidKeyException e) {
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  public HMac update(final byte[] buf, final int off, final int len) {
    this.hmac.update(buf, off, len);
    return this;
  }

  public void digestTo(final byte[] buf) {
    digestTo(buf, 0, buf.length);
  }

  public void digestTo(final byte[] buf, final int off, final int len) {
    final byte[] hash = digest();
    System.arraycopy(hash, 0, buf, off, Math.min(len, hash.length));
  }

  public byte[] digest() {
    return hmac.doFinal();
  }

  public String hexDigest() {
    return BytesUtil.toHexString(digest());
  }

  public int digestLength() {
    return hmac.getMacLength();
  }
}
