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

package io.github.matteobertozzi.rednaco.localization;

import io.github.matteobertozzi.rednaco.strings.StringFormat;

public final class LocalizedText {
  public static final LocalizedText INSTANCE = new LocalizedText();

  private LocalizationProvider provider = new NoOpLocalizationProvider();

  private LocalizedText() {
    // no-op
  }

  public LocalizationProvider provider() {
    return provider;
  }

  public void setProvider(final LocalizationProvider provider) {
    this.provider = provider;
  }

  public String get(final LocalizedResource resourceId, final Object... args) {
    return provider.get(resourceId, args);
  }

  private static final class NoOpLocalizationProvider implements LocalizationProvider {
    private NoOpLocalizationProvider() {
      // no-op
    }

    @Override
    public String get(final LocalizedResource resourceId, final Object... args) {
      return StringFormat.positionalFormat(resourceId.defaultValue(), args);
    }
  }
}
