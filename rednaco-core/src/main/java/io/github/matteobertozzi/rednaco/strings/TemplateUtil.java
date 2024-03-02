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

import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TemplateUtil {
  private static final Pattern TEMPLATE_VAR_PATTERN = Pattern.compile("\\$\\{(.*?)}");

  private TemplateUtil() {
    // no-op
  }

  private static final String[][] XML_BASIC_ESCAPE = {
    {Pattern.quote("'"),  "&apos;"},  // XML apostrophe
    {Pattern.quote("\""), "&quot;"},  // " - double-quote
    {Pattern.quote("<"),  "&lt;"},    // < - less-than
    {Pattern.quote(">"),  "&gt;"},    // > - greater-than
  };

  public static String escapeHtml(String text) {
    text = text.replaceAll("&(?![A-Za-z]+;)", "&amp;");
    for (int i = 0; i < XML_BASIC_ESCAPE.length; ++i) {
      final String[] entry = XML_BASIC_ESCAPE[i];
      text = text.replaceAll(entry[0], entry[1]);
    }
    return text;
  }

  public static String processTemplate(final String template, final Map<String, String> templateVars) {
    return processTemplate(template, TEMPLATE_VAR_PATTERN, templateVars);
  }

  public static String processTemplate(final String template, final Function<String, String> templateVarResolver) {
    return processTemplate(template, TEMPLATE_VAR_PATTERN, templateVarResolver);
  }

  public static String processTemplate(final String template, final Pattern templateVarPattern, final Map<String, String> templateVars) {
    return processTemplate(template, templateVarPattern, templateVars::get);
  }

  public static String processTemplate(final String template, final Pattern templateVarPattern, final Function<String, String> templateVarResolver) {
    final StringBuilder buf = new StringBuilder(template.length());
    appendTemplate(buf, template, templateVarPattern, templateVarResolver);
    return buf.toString();
  }

  public static void appendTemplate(final StringBuilder buf, final String template, final Pattern templateVarPattern, final Function<String, String> templateVarResolver) {
    final Matcher m = templateVarPattern.matcher(template);
    while (m.find()) {
      final String key = m.group(1);
      final String text = templateVarResolver.apply(key);
      m.appendReplacement(buf, Matcher.quoteReplacement(text != null ? text : '[' + key + ']'));
    }
    m.appendTail(buf);
  }
}
