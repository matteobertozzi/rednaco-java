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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serial;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

import io.github.matteobertozzi.rednaco.data.json.JsonElementModule;
import io.github.matteobertozzi.rednaco.data.modules.DataMapperModules;
import io.github.matteobertozzi.rednaco.data.modules.TraceIdsModule;
import io.github.matteobertozzi.rednaco.util.Serialization.SerializationName;
import io.github.matteobertozzi.rednaco.util.Serialization.SerializeWithSnakeCase;

public class DataFormatMapperJackson implements DataFormatMapper {
  private static final String JSON_DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSX";

  private final ObjectMapper mapper;

  protected DataFormatMapperJackson(final ObjectMapper objectMapper) {
    this.mapper = objectMapper;
    this.mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
    this.mapper.setVisibility(PropertyAccessor.GETTER, Visibility.NONE);
    this.mapper.setVisibility(PropertyAccessor.IS_GETTER, Visibility.NONE);

    // --- Deserialization ---
    // Just ignore unknown fields, don't stop parsing
    this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    // Trying to deserialize value into an enum, don't fail on unknown value, use null instead
    this.mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);

    // --- Serialization ---
    // Don't include properties with null value in JSON output
    this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    // Use default pretty printer
    this.mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
    this.mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    // Javascript JSON.stringify(new Date()) -> "2023-12-10T10:25:57.132Z"
    final SimpleDateFormat sdf = new SimpleDateFormat(JSON_DATE_FORMAT_PATTERN);
    sdf.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));
    this.mapper.setDateFormat(sdf);

    this.mapper.setAnnotationIntrospector(new ExtentedAnnotationIntrospector());

    // Default Modules
    registerModule(JsonElementModule.INSTANCE);
    registerModule(TraceIdsModule.INSTANCE);
    //registerModule(MapModule.INSTANCE);
    for (final Module module: DataMapperModules.INSTANCE.getModules()) {
      registerModule(module);
    }
  }

  public void registerModule(final Module module) {
    this.mapper.registerModule(module);
  }

  public JsonFactory getFactory() {
    return mapper.getFactory();
  }

  public ObjectMapper getObjectMapper() {
    return mapper;
  }

  // ===============================================================================================
  //  JsonNode conversions
  // ===============================================================================================
  @Override
  public JsonNode toTreeNode(final Object value) {
    return mapper.valueToTree(value);
  }

  @Override
  public <T> T fromTreeNode(final JsonNode node, final Class<T> valueType) throws JsonProcessingException, IllegalArgumentException {
    return mapper.treeToValue(node, valueType);
  }

  // ===============================================================================================
  //  Object to Object conversions
  // ===============================================================================================
  @Override
  public <T> T convert(final Object value, final Class<T> valueType) {
    return mapper.convertValue(value, valueType);
  }

  @Override
  public <T> T convert(final Object value, final TypeReference<T> valueType) {
    return mapper.convertValue(value, valueType);
  }

  // ===============================================================================================
  //  From file/stream/byte[]/string/... conversions
  // ===============================================================================================
  @Override
  public <T> T fromStream(final InputStream stream, final Class<T> valueType) throws IOException {
    return mapper.readValue(stream, valueType);
  }

  @Override
  public <T> T fromStream(final InputStream stream, final TypeReference<T> valueType) throws IOException {
    return mapper.readValue(stream, valueType);
  }

  @Override
  public <T> T fromBytes(final byte[] data, final Class<T> valueType) {
    try {
      return mapper.readValue(data, valueType);
    } catch (final Exception e) {
      throw new DataFormatException(e);
    }
  }

  @Override
  public <T> T fromBytes(final byte[] data, final TypeReference<T> valueType) {
    try {
      return mapper.readValue(data, valueType);
    } catch (final Exception e) {
      throw new DataFormatException(e);
    }
  }

  @Override
  public <T> T fromBytes(final byte[] data, final int off, final int len, final Class<T> valueType) {
    try {
      return mapper.readValue(data, off, len, valueType);
    } catch (final Exception e) {
      throw new DataFormatException(e);
    }
  }

  @Override
  public <T> T fromString(final String data, final Class<T> valueType) {
    try {
      return mapper.readValue(data, valueType);
    } catch (final JsonProcessingException e) {
      throw new DataFormatException(e);
    }
  }

  @Override
  public <T> T fromString(final String data, final TypeReference<T> valueType) {
    try {
      return mapper.readValue(data, valueType);
    } catch (final JsonProcessingException e) {
      throw new DataFormatException(e);
    }
  }

  // ===============================================================================================
  //  To file/stream/byte[]/string/... conversions
  // ===============================================================================================
  @Override
  public void addToStream(final OutputStream stream, final Object obj) throws IOException {
    mapper.writeValue(stream, obj);
  }

  @Override
  public void addToPrettyPrintStream(final OutputStream stream, final Object obj) throws IOException {
    mapper.writerWithDefaultPrettyPrinter().writeValue(stream, obj);
  }

  @Override
  public String asPrettyPrintString(final Object value) {
    try {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
    } catch (final Exception e) {
      throw new DataFormatException(e);
    }
  }

  @Override
  public String asString(final Object value) {
    try {
      return mapper.writeValueAsString(value);
    } catch (final Exception e) {
      throw new DataFormatException(e);
    }
  }

  @Override
  public byte[] asBytes(final Object value) {
    try {
      return mapper.writeValueAsBytes(value);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static final class ExtentedAnnotationIntrospector extends JacksonAnnotationIntrospector {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public Object findNamingStrategy(final AnnotatedClass ac) {
      final SerializeWithSnakeCase ann = _findAnnotation(ac, SerializeWithSnakeCase.class);
      return (ann == null) ? super.findNamingStrategy(ac) : PropertyNamingStrategies.SNAKE_CASE;
    }

    @Override
    public PropertyName findNameForSerialization(final Annotated a) {
      final SerializationName ann = _findAnnotation(a, SerializationName.class);
      return ann == null ? super.findNameForSerialization(a) : PropertyName.construct(ann.value());
    }
  }
}
