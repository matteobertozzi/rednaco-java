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
import java.util.Map;
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
import io.github.matteobertozzi.rednaco.strings.TemplateUtil;
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
    TemplateUtil.appendTemplate(code, """
    // autogen on ${now}
    package ${ns}.autogen;

    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.Objects;
    import java.util.Set;

    import com.fasterxml.jackson.core.JsonGenerator;
    import com.fasterxml.jackson.databind.SerializerProvider;
    import com.fasterxml.jackson.databind.annotation.JsonSerialize;
    import com.fasterxml.jackson.databind.ser.std.StdSerializer;

    import io.github.matteobertozzi.rednaco.collections.ImmutableCollections;
    import io.github.matteobertozzi.rednaco.strings.StringBuilderUtil;
    import ${ns}.autogen.${editorClassName}.${editorClassName}Serializer;
    """, Map.of(
      "now", ZonedDateTime.now().toString(),
      "ns", ns,
      "editorClassName", editorClassName
    ));

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
    TemplateUtil.appendTemplate(code, """
      public ${editorClassName}() {
        // no-op (init with default values)
        this.hasOriginal = false;
      }

    """, Map.of("editorClassName", editorClassName));

    // ctor(record)
    TemplateUtil.appendTemplate(code, """
      public ${editorClassName}(final ${recordName} obj) {
        if (this.hasOriginal = (obj != null)) {
    """, Map.of(
      "editorClassName", editorClassName,
      "recordName", recordName
    ));
    for (final RecordComponentElement component: recordComponents) {
      final String componentName = component.getSimpleName().toString();
      final TypeMirror componentType = component.asType();

      if (componentType.getKind().isPrimitive()) {
        TemplateUtil.appendTemplate(code, """
              this.${componentName}Original = obj.${componentName}();
              this.${componentName} = obj.${componentName}();
        """, Map.of("componentName", componentName));
      } else {
        TemplateUtil.appendTemplate(code, """
              this.${componentName}Original = null;
              this.${componentName} = obj.${componentName}();
        """, Map.of("componentName", componentName));
      }
    }
    code.append("    }").append(System.lineSeparator());
    code.append("  }").append(System.lineSeparator());
    code.append(System.lineSeparator());

    // toRecord()
    code.append("  public ").append(recordName).append(" toRecord() {").append(System.lineSeparator());
    code.append("    return new ").append(recordName).append('(');
    int fieldIndex = 0;
    for (final RecordComponentElement component: recordComponents) {
      if (fieldIndex != 0) code.append(", ");
      code.append(component.getSimpleName().toString());
      fieldIndex++;
    }
    code.append(");").append(System.lineSeparator());
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
    fieldIndex = 0;
    for (final RecordComponentElement component: recordComponents) {
      final String componentName = component.getSimpleName().toString();
      final String capitalizedComponentName = StringUtil.capitalize(componentName);
      final TypeMirror componentType = component.asType();
      final boolean isComponentPrimitive = componentType.getKind().isPrimitive();
      final int bitmapGroup = fieldIndex >>> 6;
      final int bitmapIndex = fieldIndex & 63;

      final Map<String, String> componentVars = Map.of(
        "capitalizedComponentName", capitalizedComponentName,
        "componentName", componentName,
        "componentType", componentType.toString(),
        "bitmapFieldReset", String.valueOf(~(1L << bitmapIndex)),
        "bitmapFieldSet", String.valueOf(1L << bitmapIndex),
        "changesBitmap", "changesBitmap" + bitmapGroup
      );

      TemplateUtil.appendTemplate(code, """
        // ----- ${componentName} -----
        private ${componentType} ${componentName}Original;
        private ${componentType} ${componentName};

        public ${componentType} ${componentName}() {
          return ${componentName};
        }

        public boolean ${componentName}HasChanges() {
          return (this.${changesBitmap} & ${bitmapFieldSet}L) == ${bitmapFieldSet}L;
        }

        public void replace${capitalizedComponentName}(final ${componentType} value) {
          this.${componentName} = value;
          this.${changesBitmap} |= ${bitmapFieldSet}L;
        }

      """, componentVars);

      if (isComponentPrimitive) {
        TemplateUtil.appendTemplate(code, """
          public void set${capitalizedComponentName}(final ${componentType} value) {
            if (hasOriginal) {
              if (this.${componentName}Original == value) {
                this.${changesBitmap} &= ${bitmapFieldReset}L;
              } else {
                this.${changesBitmap} |= ${bitmapFieldSet}L;
              }
            }
            this.${componentName} = value;
          }

        """, componentVars);
      } else {
        TemplateUtil.appendTemplate(code, """
          public void set${capitalizedComponentName}(final ${componentType} value) {
            if (hasOriginal) {
              if (this.${componentName}Original == null) {
                if (Objects.equals(value, this.${componentName})) {
                  return;
                }
                this.${componentName}Original = this.${componentName};
              } else if (Objects.equals(value, this.${componentName}Original)) {
                this.${componentName} = this.${componentName}Original;
                this.${componentName}Original = null;
                this.${changesBitmap} &= ${bitmapFieldReset}L;
                return;
              }
            }
            this.${componentName} = value;
            this.${changesBitmap} |= ${bitmapFieldSet}L;
          }

        """, componentVars);
      }

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

    code.append("    final ArrayList<String> fields = new ArrayList<>(").append(recordComponents.size()).append(");").append(System.lineSeparator());
    for (final RecordComponentElement component: recordComponents) {
      final String componentName = component.getSimpleName().toString();
      code.append("    if (").append(componentName).append("HasChanges()) fields.add(\"").append(componentName).append("\");").append(System.lineSeparator());
    }
    code.append("    return ImmutableCollections.setOf(fields);").append(System.lineSeparator());
    code.append("  }").append(System.lineSeparator());
    code.append(System.lineSeparator());

    // toString()
    TemplateUtil.appendTemplate(code, """
      @Override
      public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("${editorClassName} [");
    """, Map.of("editorClassName", editorClassName));
    fieldIndex = 0;
    for (final RecordComponentElement component: recordComponents) {
      final String componentName = component.getSimpleName().toString();
      TemplateUtil.appendTemplate(code, """
          StringBuilderUtil.appendValue(builder.append("${fieldName}"), ${componentName});
          if (${componentName}HasChanges()) {
            StringBuilderUtil.appendValue(builder.append('/'), ${componentName}Original);
          }
      """, Map.of(
        "componentName", componentName,
        "fieldName", (((fieldIndex != 0) ? ", " : "") + componentName)
      ));
      fieldIndex++;
    }
    code.append("    builder.append(\"]\");").append(System.lineSeparator());
    code.append("    return builder.toString();").append(System.lineSeparator());
    code.append("  }").append(System.lineSeparator());
    code.append(System.lineSeparator());

    // serializer
    TemplateUtil.appendTemplate(code, """
      public static final class ${editorClassName}Serializer extends StdSerializer<${editorClassName}> {
        public ${editorClassName}Serializer() {
          this(${editorClassName}.class);
        }

        public ${editorClassName}Serializer(final Class<${editorClassName}> t) {
          super(t);
        }
    """, Map.of("editorClassName", editorClassName));

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

      TemplateUtil.appendTemplate(code, """
            jgen.${writeFieldFunc}("${componentName}", value.${componentName});
            if (value.${componentName}HasChanges()) {
              jgen.${writeFieldFunc}("${componentName}Original", value.${componentName}Original);
            }
      """, Map.of(
        "writeFieldFunc", writeFieldFunc,
        "componentName", componentName
      ));
    }

    TemplateUtil.appendTemplate(code, """
          jgen.writeArrayFieldStart("_changes");
          if (value.hasChanges()) {
    """, Map.of());
    for (final RecordComponentElement component: recordComponents) {
      final String componentName = component.getSimpleName().toString();
      TemplateUtil.appendTemplate(code, """
              if (value.${componentName}HasChanges()) jgen.writeString("${componentName}");
      """, Map.of("componentName", componentName));
    }
    TemplateUtil.appendTemplate(code, """
          }
          jgen.writeEndArray();
          jgen.writeEndObject();
        }
      }
    }

    """, Map.of());

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
