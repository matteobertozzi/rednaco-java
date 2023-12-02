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

import java.util.regex.Pattern;

public final class SecureStringUtil {
  private SecureStringUtil() {
    // no-op
  }

  public static String maskMailAddress(final String email) {
    final int atIndex = email.lastIndexOf('@');
    final int atDomain = email.lastIndexOf('.');

    final StringBuilder maskMail = new StringBuilder(email.length());
    maskMail.append(email, 0, Math.min(3, atIndex - 1));
    while (maskMail.length() < atIndex) maskMail.append("*");
    maskMail.append(email, atIndex, atIndex + 2);
    while (maskMail.length() < atDomain) maskMail.append("*");
    maskMail.append(email.substring(atDomain));
    return maskMail.toString();
  }

  private static final Pattern CLEAN_PHONE_PATTERN = Pattern.compile("[\\s\\-()]");
  public static String maskPhoneNumber(final String phoneNumber) {
    // +39 348 047 0028
    final String cleanPhoneNumber = CLEAN_PHONE_PATTERN.matcher(phoneNumber).replaceAll("");

    final StringBuilder maskPhone = new StringBuilder(cleanPhoneNumber.length());

    // >=10 *** *** *** ABCD
    final int maskLength = cleanPhoneNumber.length() - 4;

    final int n = maskLength % 3;
    maskPhone.append("*".repeat(n));
    if (n > 0) maskPhone.append(" ");

    for (int i = 0; i < (maskLength - n); ++i) {
      if (i > 0 && (i % 3) == 0) maskPhone.append(" ");
      maskPhone.append("*");
    }

    maskPhone.append(" ");
    maskPhone.append(cleanPhoneNumber.substring(maskLength));

    return maskPhone.toString();
  }
}
