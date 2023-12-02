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

package io.github.matteobertozzi.rednaco.strings;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestSecureStringUtil {
  @Test
  public void testMailAddress() {
    Assertions.assertEquals("ab*@a**.com", SecureStringUtil.maskMailAddress("abc@abc.com"));
    Assertions.assertEquals("abc***@g*****.it", SecureStringUtil.maskMailAddress("abcdef@ghilmn.it"));
  }

  @Test
  public void testPhoneNumber() {
    Assertions.assertEquals("*** *** *** 0449", SecureStringUtil.maskPhoneNumber("+XT 408 759-0449")); // ** *** *** 0449
    Assertions.assertEquals("*** *** 9372", SecureStringUtil.maskPhoneNumber("0376-729372"));
  }
}
