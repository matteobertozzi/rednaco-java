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

package io.github.matteobertozzi.rednaco.dispatcher.message;

import java.io.ByteArrayInputStream;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;

import io.github.matteobertozzi.rednaco.bytes.BytesUtil;
import io.github.matteobertozzi.rednaco.collections.arrays.ArrayUtil;
import io.github.matteobertozzi.rednaco.data.CborFormat;
import io.github.matteobertozzi.rednaco.data.DataFormat;
import io.github.matteobertozzi.rednaco.data.JsonFormat;
import io.github.matteobertozzi.rednaco.data.XmlFormat;
import io.github.matteobertozzi.rednaco.data.YajbeFormat;
import io.github.matteobertozzi.rednaco.data.YamlFormat;
import io.github.matteobertozzi.rednaco.io.RuntimeIOException;
import io.github.matteobertozzi.rednaco.strings.StringUtil;

public final class MessageUtil {
  public static final String METADATA_FOR_HTTP_METHOD = ":method";
  public static final String METADATA_FOR_HTTP_URI = ":uri";
  public static final String METADATA_FOR_HTTP_STATUS = ":status";
  public static final String METADATA_AUTHORIZATION = "authorization";
  public static final String METADATA_ACCEPT = "accept";
  public static final String METADATA_CONTENT_TYPE = "content-type";
  public static final String METADATA_CONTENT_LENGTH = "content-length";
  public static final String METADATA_CONTENT_ENCODING = "content-encoding";

  public static final String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded";
  public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";
  public static final String CONTENT_TYPE_TEXT_HTML = "text/html";
  public static final String CONTENT_TYPE_TEXT_XML = "text/xml";
  public static final String CONTENT_TYPE_APP_XML = "application/xml";
  public static final String CONTENT_TYPE_APP_YAML = "application/yaml";
  public static final String CONTENT_TYPE_APP_CBOR = "application/cbor";
  public static final String CONTENT_TYPE_APP_JSON = "application/json";
  public static final String CONTENT_TYPE_APP_YAJBE = "application/yajbe";

  private MessageUtil() {
    // no-op
  }

  // ====================================================================================================
  //  Metadata util
  // ====================================================================================================
  public static DataFormat parseAcceptFormat(final MessageMetadata metadata) {
    return parseAcceptFormat(metadata, JsonFormat.INSTANCE);
  }

  public static DataFormat parseAcceptFormat(final MessageMetadata metadata, final DataFormat defaultFormat) {
    return parseAcceptFormat(metadata, defaultFormat, MessageUtil::parseTypeToDataFormat);
  }

  public static DataFormat parseAcceptFormat(final String accept, final DataFormat defaultFormat) {
    return parseAcceptFormat(accept, defaultFormat, MessageUtil::parseTypeToDataFormat);
  }

  public static <T> T parseAcceptFormat(final MessageMetadata metadata, final T defaultFormat,
      final Function<String, T> parseFormat) {
    final String accept = metadata.getString(METADATA_ACCEPT, null);
    return parseAcceptFormat(accept, defaultFormat, parseFormat);
  }

  public static <T> T parseAcceptFormat(final String accept, final T defaultFormat, final Function<String, T> parseFormat) {
    if (StringUtil.isEmpty(accept)) return defaultFormat;

    T format = parseFormat.apply(accept);
    if (format != null) return format;

    int lastIndex = 0;
    while (lastIndex < accept.length()) {
      int eof = accept.indexOf(',', lastIndex);
      if (eof < 0) eof = accept.length();

      String type = accept.substring(lastIndex, eof);
      final int qIndex = type.lastIndexOf(';');
      if (qIndex > 0) type = type.substring(0, qIndex);

      format = parseFormat.apply(type.trim());
      if (format != null) return format;

      lastIndex = eof + 1;
    }
    return defaultFormat;
  }

  public static DataFormat parseContentType(final MessageMetadata metadata) {
    return parseContentType(metadata, JsonFormat.INSTANCE);
  }

  public static DataFormat parseContentType(final MessageMetadata metadata, final DataFormat defaultFormat) {
    return parseContentType(metadata, defaultFormat, MessageUtil::parseTypeToDataFormat);
  }

  public static <T> T parseContentType(final MessageMetadata metadata, final T defaultFormat,
      final Function<String, T> parseFormat) {
    final String accept = metadata.getString(METADATA_CONTENT_TYPE, null);
    return parseContentType(accept, defaultFormat, parseFormat);
  }

  public static <T> T parseContentType(final String contentType, final T defaultFormat, final Function<String, T> parseFormat) {
    if (StringUtil.isEmpty(contentType)) return defaultFormat;

    T format = parseFormat.apply(contentType);
    if (format != null) return format;

    int eof = contentType.indexOf(';');
    if (eof < 0) eof = contentType.length();

    final String type = contentType.substring(0, eof);
    format = parseFormat.apply(type.trim());
    return format != null ? format : defaultFormat;
  }

  public static DataFormat parseTypeToDataFormat(final String type) {
    return switch (type) {
      case CONTENT_TYPE_APP_CBOR -> CborFormat.INSTANCE;
      case CONTENT_TYPE_APP_JSON -> JsonFormat.INSTANCE;
      case CONTENT_TYPE_APP_YAJBE -> YajbeFormat.INSTANCE;
      case CONTENT_TYPE_APP_YAML -> YamlFormat.INSTANCE;
      case CONTENT_TYPE_APP_XML, CONTENT_TYPE_TEXT_XML -> XmlFormat.INSTANCE;
      default -> null;
    };
  }

  public static <T> T convertInputContent(final Message message, final Class<T> classOfT) {
    final DataFormat dataFormat = parseContentType(message.metadata());
    return message.convertContent(dataFormat, classOfT);
  }

  public static <T> T convertOutputContent(final Message message, final Class<T> classOfT) {
    final DataFormat dataFormat = parseAcceptFormat(message.metadata());
    return message.convertContent(dataFormat, classOfT);
  }

  // ====================================================================================================
  //  Auth helpers
  // ====================================================================================================
  public static Message newBasicAuthRequired(final String realm) {
    final MessageMetadataMap metadata = new MessageMetadataMap();
    metadata.set(MessageUtil.METADATA_FOR_HTTP_STATUS, 401);
    metadata.set("WWW-Authenticate",  "Basic realm=\"" + realm + "\"");
    return new EmptyMessage(metadata);
  }

  // ====================================================================================================
  //  Message util
  // ====================================================================================================
  public static <T> Message newDataMessage(final Map<String, String> metadata, final T data) {
    return new TypedMessage<>(metadata, data);
  }

  public static <T> Message newDataMessage(final MessageMetadata metadata, final T data) {
    return new TypedMessage<>(metadata, data);
  }

  public static <T> Message newDataMessage(final T data) {
    return new TypedMessage<>(EmptyMetadata.INSTANCE, data);
  }

  public static Message newRawMessage(final Map<String, String> metadata, final byte[] content) {
    return new RawMessage(metadata, content);
  }

  public static Message newRawMessage(final MessageMetadata metadata, final byte[] content) {
    return new RawMessage(metadata, content);
  }

  public static Message newRawMessage(final Map<String, String> metadata, final String content) {
    return new RawMessage(metadata, content.getBytes(StandardCharsets.UTF_8));
  }

  public static Message newRawMessage(final MessageMetadata metadata, final String content) {
    return new RawMessage(metadata, content.getBytes(StandardCharsets.UTF_8));
  }

  public static Message newRawMessage(final byte[] content) {
    return new RawMessage(EmptyMetadata.INSTANCE, content);
  }

  public static Message emptyMessage() {
    return newEmptyMessage(EmptyMetadata.INSTANCE);
  }

  public static Message newEmptyMessage(final Map<String, String> metadata) {
    return new EmptyMessage(metadata);
  }

  public static Message newEmptyMessage(final MessageMetadata metadata) {
    return new EmptyMessage(metadata);
  }

  public static Message newErrorMessage(final MessageError error) {
    return new ErrorMessage(error);
  }

  public static Message newHtmlMessage(final String html) {
    final MessageMetadataMap metadata = new MessageMetadataMap();
    metadata.add(METADATA_CONTENT_TYPE, CONTENT_TYPE_TEXT_HTML);
    return newRawMessage(metadata, html.getBytes(StandardCharsets.UTF_8));
  }

  public static Message newTextMessage(final String text) {
    final MessageMetadataMap metadata = new MessageMetadataMap();
    metadata.add(METADATA_CONTENT_TYPE, CONTENT_TYPE_TEXT_PLAIN);
    return newRawMessage(metadata, text.getBytes(StandardCharsets.UTF_8));
  }

  public static MessageFile newFileMessage(final File file) {
    return newFileMessage(file, null);
  }

  public static MessageFile newFileMessage(final File file, final String contentType) {
    final long length = file.length();
    return newFileMessage(file.toPath(), contentType, 0, length, length);
  }

  public static MessageFile newFileMessage(final File file, final String contentType, final long rangeOffset, final long rangeLength, final long length) {
    return newFileMessage(file.toPath(), contentType, rangeOffset, rangeLength, length);
  }

  public static MessageFile newFileMessage(final Path path) throws IOException {
    return newFileMessage(path, null);
  }

  public static MessageFile newFileMessage(final Path path, final String contentType) throws IOException {
    final long length = Files.size(path);
    return newFileMessage(path, contentType, 0, length, length);
  }

  public static MessageFile newFileMessage(final Path path, final String contentType, final long rangeOffset, final long rangeLength, final long length) {
    final MessageMetadataMap metadata = new MessageMetadataMap();
    if (contentType != null) metadata.add(METADATA_CONTENT_TYPE, contentType);
    metadata.add(METADATA_CONTENT_LENGTH, length);
    return new MessageFile(metadata, path, rangeOffset, rangeLength, length);
  }

  public static MessageFile newGzEncodedFile(final Path path, final String contentType) throws IOException {
    final long length = Files.size(path);
    return newGzEncodedFile(path, contentType, 0, length, length);
  }

  public static MessageFile newGzEncodedFile(final Path path, final String contentType, final long rangeOffset, final long rangeLength, final long length) {
    final MessageMetadataMap metadata = new MessageMetadataMap();
    if (contentType != null) metadata.set(METADATA_CONTENT_TYPE, contentType);
    metadata.set(METADATA_CONTENT_ENCODING, "gzip");
    return new MessageFile(metadata, path, rangeOffset, rangeLength, length);
  }

  // ====================================================================================================
  //  Messages
  // ====================================================================================================
  public record RawMessage(MessageMetadata metadata, byte[] content) implements Message {
    public RawMessage(final Map<String, String> metadata, final byte[] content) {
      this(new MessageMetadataMap(metadata), content);
    }

    @Override public Message retain() { return this; }
    @Override public Message release() { return this; }

    @Override
    public boolean hasContent() {
      return BytesUtil.isNotEmpty(content);
    }

    @Override
    public long writeContentToStream(final OutputStream stream) throws IOException {
      if (ArrayUtil.isEmpty(content)) return 0;

      stream.write(content);
      return content.length;
    }

    @Override public long writeContentToStream(final DataOutput stream) throws IOException {
      if (ArrayUtil.isEmpty(content)) return 0;

      stream.write(content);
      return content.length;
    }

    @Override
    public <T> T convertContent(final DataFormat format, final Class<T> classOfT) {
      return switch (metadata().getString(METADATA_CONTENT_ENCODING, null)) {
        case "gzip" -> convertGzContent(format, classOfT);
        default -> format.fromBytes(content, classOfT);
      };
    }

    private <T> T convertGzContent(final DataFormat format, final Class<T> classOfT) {
      try (ByteArrayInputStream stream = new ByteArrayInputStream(content())) {
        try (GZIPInputStream gz = new GZIPInputStream(stream)) {
          return format.fromStream(gz, classOfT);
        }
      } catch (final IOException e) {
        throw new RuntimeIOException(e);
      }
    }

    @Override
    public byte[] convertContentToBytes() {
      return content;
    }
  }

  public record TypedMessage<TData>(MessageMetadata metadata, TData content) implements Message {
    public TypedMessage(final Map<String, String> metadata, final TData content) {
      this(new MessageMetadataMap(metadata), content);
    }

    @Override public Message retain() { return this; }
    @Override public Message release() { return this; }

    @Override public boolean hasContent() { return content() != null; }
    @Override public long writeContentToStream(final OutputStream stream) { throw new UnsupportedOperationException(); }
    @Override public long writeContentToStream(final DataOutput stream) { throw new UnsupportedOperationException(); }
    @Override public byte[] convertContentToBytes() { throw new UnsupportedOperationException(); }

    @Override
    public <T> T convertContent(final DataFormat format, final Class<T> classOfT) {
      return format.convert(content(), classOfT);
    }
  }

  public record ErrorMessage(MessageMetadata metadata, MessageError error) implements Message {
    public ErrorMessage(final MessageError error) {
      this(EmptyMetadata.INSTANCE, error);
    }

    @Override public Message retain() { return this; }
    @Override public Message release() { return this; }

    @Override public boolean hasContent() { throw new UnsupportedOperationException(); }
    @Override public long writeContentToStream(final OutputStream stream) { throw new UnsupportedOperationException(); }
    @Override public long writeContentToStream(final DataOutput stream) { throw new UnsupportedOperationException(); }
    @Override public byte[] convertContentToBytes() { throw new UnsupportedOperationException(); }

    @Override
    public <T> T convertContent(final DataFormat format, final Class<T> classOfT) {
      throw new UnsupportedOperationException();
    }
  }

  public record EmptyMessage(MessageMetadata metadata) implements Message {
    private EmptyMessage(final Map<String, String> metadata) {
      this(new MessageMetadataMap(metadata));
    }

    @Override public Message retain() { return this; }
    @Override public Message release() { return this; }

    @Override public boolean hasContent() { return false; }
    @Override public long writeContentToStream(final OutputStream stream) { return 0; }
    @Override public long writeContentToStream(final DataOutput stream) { return 0; }
    @Override public <T> T convertContent(final DataFormat format, final Class<T> classOfT) { return null; }
    @Override public byte[] convertContentToBytes() { return BytesUtil.EMPTY_BYTES; }
  }

  public static final class EmptyMetadata implements MessageMetadata {
    public static final EmptyMetadata INSTANCE = new EmptyMetadata();

    private EmptyMetadata() {
      // no-op
    }

    @Override public boolean isEmpty() { return true; }
    @Override public int size() { return 0; }

    @Override public String get(final String key) { return null; }
    @Override public List<String> getList(final String key) { return null; }

    @Override public List<Map.Entry<String, String>> entries() { return List.of(); }

    @Override public void forEach(final BiConsumer<? super String, ? super String> action) { }

    @Override public String toString() { return "{}"; }
  }
}
