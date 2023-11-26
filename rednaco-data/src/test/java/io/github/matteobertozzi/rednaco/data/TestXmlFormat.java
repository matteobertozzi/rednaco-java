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

package io.github.matteobertozzi.rednaco.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.matteobertozzi.rednaco.data.json.JsonElement;
import io.github.matteobertozzi.rednaco.data.json.JsonPrimitive;
import io.github.matteobertozzi.rednaco.data.json.JsonUtil;

public class TestXmlFormat {

  record Foo (String a, int b) {}

  @Test
  public void testFromXmlList() {
    final String[] items = new String[] { "Foo", "Bar", "Car" };
    final String xml = """
      <root>
        <item>Foo</item>
        <item>Bar</item>
        <item>Car</item>
      </root>
    """;

    Assertions.assertArrayEquals(items, XmlFormat.INSTANCE.fromString(xml, String[].class));

    final JsonNode node = XmlFormat.INSTANCE.fromString(xml, JsonNode.class);
    Assertions.assertTrue(node.isObject());
    Assertions.assertTrue(node.get("item").isArray());
    Assertions.assertEquals(3, node.get("item").size());
    for (int i = 0; i < items.length; ++i) {
      final JsonNode itemNode = node.get("item").get(i);
      Assertions.assertTrue(itemNode.isTextual());
      Assertions.assertEquals(items[i], itemNode.asText());
    }

    final JsonElement[] jsonElemArray = XmlFormat.INSTANCE.fromString(xml, JsonElement[].class);
    Assertions.assertEquals(3, jsonElemArray.length);
    for (int i = 0; i < items.length; ++i) {
      Assertions.assertTrue(jsonElemArray[i].isJsonPrimitive());
      final JsonPrimitive jsonPrimitive = jsonElemArray[i].getAsJsonPrimitive();
      Assertions.assertTrue(jsonPrimitive.isString());
      Assertions.assertEquals(items[i], jsonPrimitive.getAsString());
    }

    final JsonNode[] nodeArray = XmlFormat.INSTANCE.fromString(xml, JsonNode[].class);
    Assertions.assertEquals(3, nodeArray.length);
    for (int i = 0; i < items.length; ++i) {
      Assertions.assertTrue(nodeArray[i].isTextual());
      Assertions.assertEquals(items[i], nodeArray[i].asText());
    }
  }

  @Test
  public void testSimple() {
    final JsonElement j = JsonUtil.toJsonTree(new Foo[] { new Foo("a", 1), new Foo("b", 2) });
    System.out.println(XmlFormat.INSTANCE.asString(new Foo[] { new Foo("a", 1), new Foo("b", 2) }));
    System.out.println(XmlFormat.INSTANCE.asString(List.of(new Foo("a", 1), new Foo("b", 2))));
    System.out.println(XmlFormat.INSTANCE.asString(new ArrayList<>(List.of(new Foo("a", 1), new Foo("b", 2)))));
    System.out.println(XmlFormat.INSTANCE.asString(Map.of("x", j, "y", 10, "z", "hello")));

    final String xmlList = "<JsonArray><item><a>10</a><b>hello</b></item><item><a>10</a><b>hello</b></item></JsonArray>";
    System.out.println(XmlFormat.INSTANCE.fromString(xmlList, JsonElement.class));

    final String xml2 = XmlFormat.INSTANCE.asString(new Foo[] { new Foo("a", 1), new Foo("b", 2) });
    System.out.println(XmlFormat.INSTANCE.fromString(xml2, JsonElement.class));
    System.out.println(List.of(XmlFormat.INSTANCE.fromString(xml2, JsonElement[].class)));
  }
}
