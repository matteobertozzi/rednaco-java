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

package io.github.matteobertozzi.rednaco.data.processor;

import java.io.Writer;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import io.github.matteobertozzi.rednaco.strings.StringFormat;
import io.github.matteobertozzi.rednaco.strings.StringUtil;
public class RecordEditorBuilderProcessor extends AbstractProcessor {
  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Collections.singleton(RecordEditorBuilder.class.getName());
  }

  @Override
  public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
    final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(RecordEditorBuilder.class);
    if (elements.isEmpty()) return false;

    for (final Element element: elements) {
      if (element.getKind() != ElementKind.RECORD) {
        fatalError("unexpected @RecordEditorBuilder on a non record type: {}", element);
        return false;
      }

      final TypeElement recordElement = (TypeElement) element;
      processRecord(recordElement);
    }
    return false;
  }

  private void processRecord(final TypeElement recordElement) {
    final List<? extends RecordComponentElement> recordComponents = recordElement.getRecordComponents();
    final String recordFullName = recordElement.getQualifiedName().toString();
    final int nsEndIndex = recordFullName.lastIndexOf('.');
    final String ns = recordFullName.substring(0, nsEndIndex);
    final String recordName = recordFullName.substring(nsEndIndex + 1);
    final String editorClassName = recordName + "EditorBuilder";

    final int bitmapSize = (recordComponents.size() + 63) >>> 6;

    final StringBuilder code = new StringBuilder();

    code.append("// autogen on ").append(ZonedDateTime.now()).append(System.lineSeparator());
    code.append("package ").append(ns).append(".autogen;").append(System.lineSeparator());
    code.append(System.lineSeparator());

    code.append("import java.io.IOException;").append(System.lineSeparator());
    code.append("import java.util.ArrayList;").append(System.lineSeparator());
    code.append("import java.util.Objects;").append(System.lineSeparator());
    code.append("import java.util.Set;").append(System.lineSeparator());
    code.append(System.lineSeparator());
    code.append("import com.fasterxml.jackson.annotation.JsonProperty;").append(System.lineSeparator());
    code.append("import com.fasterxml.jackson.core.JsonGenerator;").append(System.lineSeparator());
    code.append("import com.fasterxml.jackson.databind.SerializerProvider;").append(System.lineSeparator());
    code.append("import com.fasterxml.jackson.databind.annotation.JsonSerialize;").append(System.lineSeparator());
    code.append("import com.fasterxml.jackson.databind.ser.std.StdSerializer;").append(System.lineSeparator());
    code.append(System.lineSeparator());
    code.append("import io.github.matteobertozzi.rednaco.collections.ImmutableCollections;").append(System.lineSeparator());
    code.append("import io.github.matteobertozzi.rednaco.strings.StringBuilderUtil;").append(System.lineSeparator());
    code.append("import ").append(ns).append(".autogen.").append(editorClassName).append('.').append(editorClassName).append("Serializer;").append(System.lineSeparator());
    code.append(System.lineSeparator());

    code.append("import ").append(recordElement.getQualifiedName()).append(';').append(System.lineSeparator());
    code.append(System.lineSeparator());

    code.append("@JsonSerialize(using = ").append(editorClassName).append("Serializer.class)").append(System.lineSeparator());
    code.append("public final class ").append(editorClassName).append(" {").append(System.lineSeparator());

    code.append("  private transient boolean hasOriginal;").append(System.lineSeparator());
    for (int i = 0; i < bitmapSize; ++i) {
      code.append("  private transient long changesBitmap").append(i).append(" = 0L;").append(System.lineSeparator());
    }
    code.append(System.lineSeparator());

    // ctor()
    code.append("  public ").append(editorClassName).append("() {").append(System.lineSeparator());
    code.append("    // no-op (init with default values)").append(System.lineSeparator());
    code.append("    this.hasOriginal = false;").append(System.lineSeparator());
    code.append("  }").append(System.lineSeparator());
    code.append(System.lineSeparator());

    // ctor(record)
    code.append("  public ").append(editorClassName).append("(final ").append(recordName).append(" obj) {").append(System.lineSeparator());
    code.append("    this.hasOriginal = true;").append(System.lineSeparator());
    for (final RecordComponentElement component: recordComponents) {
      final String componentName = component.getSimpleName().toString();
      final TypeMirror componentType = component.asType();

      if (componentType.getKind().isPrimitive()) {
        code.append("    this.").append(componentName).append("Original = obj.").append(componentName).append("();").append(System.lineSeparator());
        code.append("    this.").append(componentName).append(" = obj.").append(componentName).append("();").append(System.lineSeparator());
      } else {
        code.append("    this.").append(componentName).append("Original = null;").append(System.lineSeparator());
        code.append("    this.").append(componentName).append(" = obj.").append(componentName).append("();").append(System.lineSeparator());
      }
    }
    code.append("  }").append(System.lineSeparator());
    code.append(System.lineSeparator());


    // hasChanges()
    code.append("  public boolean hasChanges() {").append(System.lineSeparator());
    code.append("    return ");
    for (int i = 0; i < bitmapSize; ++i) {
      if (i != 0) code.append(" || ");
      code.append("changesBitmap").append(i).append(" != 0");
    }
    code.append(';').append(System.lineSeparator());
    code.append("  }").append(System.lineSeparator());
    code.append(System.lineSeparator());

    // hasNoChanges()
    code.append("  public boolean hasNoChanges() {").append(System.lineSeparator());
    code.append("    return ");
    for (int i = 0; i < bitmapSize; ++i) {
      if (i != 0) code.append(" && ");
      code.append("changesBitmap").append(i).append(" == 0");
    }
    code.append(';').append(System.lineSeparator());
    code.append("  }").append(System.lineSeparator());
    code.append(System.lineSeparator());

    // fields def, setter, getter, ...
    int fieldIndex = 0;
    for (final RecordComponentElement component: recordComponents) {
      final String componentName = component.getSimpleName().toString();
      final String capitalizedComponentName = StringUtil.capitalize(componentName);
      final TypeMirror componentType = component.asType();
      final boolean isComponentPrimitive = componentType.getKind().isPrimitive();
      final int bitmapGroup = fieldIndex >>> 6;
      final int bitmapIndex = fieldIndex & 63;

      code.append(System.lineSeparator());
      code.append("  // ----- ").append(componentName).append(" -----").append(System.lineSeparator());
      code.append("  private ").append(componentType).append(' ').append(componentName).append("Original;").append(System.lineSeparator());
      code.append("  private ").append(componentType).append(' ').append(componentName).append(';').append(System.lineSeparator());
      code.append(System.lineSeparator());

      code.append("  public ").append(componentType).append(" ").append(componentName).append("() {").append(System.lineSeparator());
      code.append("     return ").append(componentName).append(';').append(System.lineSeparator());
      code.append("  }").append(System.lineSeparator());
      code.append(System.lineSeparator());

      code.append("  public boolean ").append(componentName).append("HasChanges() {").append(System.lineSeparator());
      code.append("    return (this.changesBitmap").append(bitmapGroup).append(" & ").append(1L << bitmapIndex).append("L) == ").append(1L << bitmapIndex).append("L;").append(System.lineSeparator());
      code.append("  }").append(System.lineSeparator());
      code.append(System.lineSeparator());

      code.append("  public void set").append(capitalizedComponentName).append('(').append(componentType).append(" value) {").append(System.lineSeparator());
      if (isComponentPrimitive) {
        code.append("    if (hasOriginal) {").append(System.lineSeparator());
        code.append("      if (this.").append(componentName).append("Original == value) {").append(System.lineSeparator());
        code.append("        this.changesBitmap").append(bitmapGroup).append(" &= ").append(~(1L << bitmapIndex)).append("L;").append(System.lineSeparator());
        code.append("      } else {").append(System.lineSeparator());
        code.append("        this.changesBitmap").append(bitmapGroup).append(" |= ").append(1L << bitmapIndex).append("L;").append(System.lineSeparator());
        code.append("      }").append(System.lineSeparator());
        code.append("    }").append(System.lineSeparator());
        code.append("    this.").append(componentName).append(" = value;").append(System.lineSeparator());
      } else {
        code.append("    if (hasOriginal) {").append(System.lineSeparator());
        code.append("      if (this.").append(componentName).append("Original == null) {").append(System.lineSeparator());
        code.append("        if (Objects.equals(value, this.").append(componentName).append(")) {").append(System.lineSeparator());
        code.append("          return;").append(System.lineSeparator());
        code.append("        }").append(System.lineSeparator());
        code.append("        this.").append(componentName).append("Original = this.").append(componentName).append(";").append(System.lineSeparator());
        code.append("      } else if (Objects.equals(value, this.").append(componentName).append("Original)) {").append(System.lineSeparator());
        code.append("        this.").append(componentName).append(" = ").append("this.").append(componentName).append("Original;").append(System.lineSeparator());
        code.append("        this.").append(componentName).append("Original = null;").append(System.lineSeparator());
        code.append("        this.changesBitmap").append(bitmapGroup).append(" &= ").append(~(1L << bitmapIndex)).append("L;").append(System.lineSeparator());
        code.append("        return;").append(System.lineSeparator());
        code.append("      }").append(System.lineSeparator());
        code.append("    }").append(System.lineSeparator());
        code.append("    this.").append(componentName).append(" = value;").append(System.lineSeparator());
        code.append("    this.changesBitmap").append(bitmapGroup).append(" |= ").append(1L << bitmapIndex).append("L;").append(System.lineSeparator());
      }
      code.append("  }").append(System.lineSeparator());
      code.append(System.lineSeparator());

      code.append("  public void replace").append(capitalizedComponentName).append('(').append(componentType).append(" value) {").append(System.lineSeparator());
      code.append("    this.").append(componentName).append(" = value;").append(System.lineSeparator());
      code.append("    this.changesBitmap").append(bitmapGroup).append(" |= ").append(1L << bitmapIndex).append("L;").append(System.lineSeparator());
      code.append("  }").append(System.lineSeparator());
      code.append(System.lineSeparator());

      fieldIndex++;
    }

    // changedFields()
    code.append("  public Set<String> changedFields() {").append(System.lineSeparator());
    code.append("    if (");
    for (int i = 0; i < bitmapSize; ++i) {
      if (i != 0) code.append(" && ");
      code.append("changesBitmap").append(i).append(" == 0");
    }
    code.append(") {").append(System.lineSeparator());
    code.append("      return Set.of();").append(System.lineSeparator());
    code.append("    }").append(System.lineSeparator());
    code.append(System.lineSeparator());

    code.append("    final ArrayList<String> fields = new ArrayList(").append(recordComponents.size()).append(");").append(System.lineSeparator());
    for (final RecordComponentElement component: recordComponents) {
      final String componentName = component.getSimpleName().toString();
      code.append("    if (").append(componentName).append("HasChanges()) fields.add(\"").append(componentName).append("\");").append(System.lineSeparator());
    }
    code.append("    return ImmutableCollections.setOf(fields);").append(System.lineSeparator());
    code.append("  }").append(System.lineSeparator());
    code.append(System.lineSeparator());

    // toString()
    code.append("  @Override").append(System.lineSeparator());
    code.append("  public String toString() {").append(System.lineSeparator());
    code.append("    final StringBuilder builder = new StringBuilder();").append(System.lineSeparator());
    code.append("    builder.append(\"").append(editorClassName).append(" [\");").append(System.lineSeparator());
    fieldIndex = 0;
    for (final RecordComponentElement component: recordComponents) {
      final String componentName = component.getSimpleName().toString();
      if (fieldIndex != 0) {
        code.append("    builder.append(\", ").append(componentName).append(":\");").append(System.lineSeparator());
      } else {
        code.append("    builder.append(\"").append(componentName).append(":\");").append(System.lineSeparator());
      }
      code.append("    StringBuilderUtil.appendValue(builder, ").append(componentName).append(");").append(System.lineSeparator());
      code.append("    if (").append(componentName).append("HasChanges()) {").append(System.lineSeparator());
      code.append("      StringBuilderUtil.appendValue(builder.append('/'), ").append(componentName).append("Original);").append(System.lineSeparator());
      code.append("    }").append(System.lineSeparator());
      fieldIndex++;
    }
    code.append("    builder.append(\"]\");").append(System.lineSeparator());
    code.append("    return builder.toString();").append(System.lineSeparator());
    code.append("  }").append(System.lineSeparator());
    code.append(System.lineSeparator());

    // serializer
    code.append("  public static final class ").append(editorClassName).append("Serializer extends StdSerializer<").append(editorClassName).append("> {").append(System.lineSeparator());
    code.append("    public ").append(editorClassName).append("Serializer() {").append(System.lineSeparator());
    code.append("      this(").append(editorClassName).append(".class);").append(System.lineSeparator());
    code.append("    }").append(System.lineSeparator());
    code.append(System.lineSeparator());
    code.append("    public ").append(editorClassName).append("Serializer(final Class<").append(editorClassName).append("> t) {").append(System.lineSeparator());
    code.append("      super(t);").append(System.lineSeparator());
    code.append("    }").append(System.lineSeparator());
    code.append(System.lineSeparator());
    code.append("    @Override").append(System.lineSeparator());
    code.append("    public void serialize(final ").append(editorClassName).append(" value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {").append(System.lineSeparator());
    code.append("      jgen.writeStartObject();").append(System.lineSeparator());
    for (final RecordComponentElement component: recordComponents) {
      final String componentName = component.getSimpleName().toString();
      final TypeMirror componentType = component.asType();
      final String writeFieldFunc = switch (componentType.getKind()) {
        case BOOLEAN -> "writeBooleanField";
        case BYTE, SHORT, INT, LONG, FLOAT, DOUBLE -> "writeNumberField";
        case DECLARED -> switch (componentType.toString()) {
          case "java.lang.String" -> "writeStringField";
          default -> "writeObjectField";
        };
        default -> "writeObjectField";
      };

      code.append("      jgen.").append(writeFieldFunc).append("(\"").append(componentName).append("\", value.").append(componentName).append(");").append(System.lineSeparator());
      code.append("      if (value.").append(componentName).append("HasChanges()) {").append(System.lineSeparator());
      code.append("        jgen.").append(writeFieldFunc).append("(\"").append(componentName).append("Original\", value.").append(componentName).append("Original);").append(System.lineSeparator());
      code.append("      }").append(System.lineSeparator());
    }
    code.append("      jgen.writeArrayFieldStart(\"_changes\");").append(System.lineSeparator());
    code.append("      if (value.hasChanges()) {").append(System.lineSeparator());
    for (final RecordComponentElement component: recordComponents) {
      final String componentName = component.getSimpleName().toString();
      code.append("      if (value.").append(componentName).append("HasChanges()) ");
      code.append("jgen.writeString(\"").append(componentName).append("\");").append(System.lineSeparator());
    }
    code.append("      }").append(System.lineSeparator());
    code.append("      jgen.writeEndArray();").append(System.lineSeparator());
    code.append("      jgen.writeEndObject();").append(System.lineSeparator());
    code.append("    }").append(System.lineSeparator());
    code.append("  }").append(System.lineSeparator());

    code.append("}").append(System.lineSeparator());

    // write java class
    writeJavaFile(ns, editorClassName, code.toString());
  }

  private void writeJavaFile(final String ns, final String className, final String code) {
    try {
      final JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(ns + ".autogen." + className);
      try (Writer writer = fileObject.openWriter()) {
        writer.write(code.toString());
      }
      log(code.toString());
    } catch (final Throwable e) {
      fatalError("unable to write {} class", className);
    }
  }

  // ====================================================================================================
  //  Logging helpers
  // ====================================================================================================
  protected void log(final String msg, final Object... args) {
    if (processingEnv.getOptions().containsKey("debug")) {
      processingEnv.getMessager().printMessage(Kind.NOTE, StringFormat.namedFormat(msg, args));
    } else {
      processingEnv.getMessager().printMessage(Kind.NOTE, StringFormat.namedFormat(msg, args));
    }
  }

  protected void warning(final String msg, final Element element, final AnnotationMirror annotation) {
    processingEnv.getMessager().printMessage(Kind.WARNING, msg, element, annotation);
  }

  protected void error(final String msg, final Element element, final AnnotationMirror annotation) {
    processingEnv.getMessager().printMessage(Kind.ERROR, msg, element, annotation);
  }

  protected void fatalError(final String msg, final Object... args) {
    processingEnv.getMessager().printMessage(Kind.ERROR, "FATAL ERROR: " + StringFormat.namedFormat(msg, args));
  }
}
