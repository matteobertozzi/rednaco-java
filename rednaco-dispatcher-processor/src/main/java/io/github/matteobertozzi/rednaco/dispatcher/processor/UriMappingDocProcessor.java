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
package io.github.matteobertozzi.rednaco.dispatcher.processor;

import java.beans.Introspector;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import io.github.matteobertozzi.rednaco.dispatcher.annotations.message.HeaderValue;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.message.MetaParam;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.message.QueryParam;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.uri.UriPattern;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.uri.UriVariable;
import io.github.matteobertozzi.rednaco.dispatcher.processor.UriMappingDocProcessor.DocsClassBuilder;
import io.github.matteobertozzi.rednaco.dispatcher.routing.UriMethod;
import io.github.matteobertozzi.rednaco.strings.StringUtil;

public class UriMappingDocProcessor extends AbstractUriMappingProcessor<DocsClassBuilder> {
  record FieldInfo(String name, String type) {}

  // ====================================================================================================
  // Process @Uri*Mapping
  // ====================================================================================================
  @Override
  protected void processUriMapping(final Map<String, DocsClassBuilder> builders, final DirectUriRoute route,
      final Element element) {
    processRoute(builders, route, element);
  }

  @Override
  protected void processUriVariableMapping(final Map<String, DocsClassBuilder> builders, final PatternUriRoute route,
      final Element element) {
    processRoute(builders, route, element);
  }

  @Override
  protected void processUriPatternMapping(final Map<String, DocsClassBuilder> builders, final PatternUriRoute route,
      final Element element) {
    processRoute(builders, route, element);
  }

  @Override
  protected void writeBuilders(final Collection<DocsClassBuilder> builders) {
    final StringBuilder writer = new StringBuilder();
    writer.append("---").append(System.lineSeparator());
    for (final DocsClassBuilder builder: builders) {
      builder.write(writer);
    }

    try {
      final FileObject fileObject = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "docs/endpoints.yaml");
      try (final Writer out = fileObject.openWriter()) {
        out.write(writer.toString());
      }
    } catch (final Throwable e) {
      fatalError(e, "unable to generate endpoints.yaml");
    }
  }

  private void processRoute(final Map<String, DocsClassBuilder> builders, final UriRoute route, final Element element) {
    final TypeElement classElement = (TypeElement) element.getEnclosingElement();
    final ExecutableElement methodElement = (ExecutableElement) element;

    //log("process doc {}", route);
    for (final UriMethod method : route.methods()) {
      final String key = method.name() + ' ' + route.uri();
      if (builders.containsKey(key)) {
        throw new UnsupportedOperationException(
            "duplicate endpoint: " + method.name() + " " + route.uri() + ": " + route);
      }
      builders.put(key, createDoc(route, method, classElement, methodElement));
    }
  }

  // ====================================================================================================
  // Docs Code Generator
  // ====================================================================================================
  static class DocsClassBuilder {
    private final HashMap<String, List<FieldInfo>> objects = new HashMap<>();
    private final ArrayList<FieldInfo> uriVariables = new ArrayList<>();
    private final ArrayList<FieldInfo> uriPatterns = new ArrayList<>();
    private final ArrayList<FieldInfo> headerKeys = new ArrayList<>();
    private final ArrayList<FieldInfo> queryKeys = new ArrayList<>();
    private final UriMethod method;
    private final String uri;

    private String result;
    private String body;

    DocsClassBuilder(final UriMethod method, final String uri) {
      this.method = method;
      this.uri = uri;
    }

    void addUriVariableParam(final FieldInfo field) { uriVariables.add(field); }
    void addUriPatternParam(final FieldInfo field) { uriPatterns.add(field); }
    void addHeaderParam(final FieldInfo field) { headerKeys.add(field); }
    void addQueryParam(final FieldInfo field) { queryKeys.add(field); }
    void addBody(final String object) { this.body = object; }
    void addResult(final String object) { this.result = object; }
    void addObject(final String name, final List<FieldInfo> fields) { objects.put(name, fields); }

    void write(final StringBuilder writer) {
      writer.append("- uri: ").append(uri).append(System.lineSeparator());
      writer.append("  method: ").append(method).append(System.lineSeparator());
      writeFields(writer, "uri_variables", uriVariables);
      writeFields(writer, "uri_patterns", uriPatterns);
      writeFields(writer, "headers", headerKeys);
      writeFields(writer, "query", queryKeys);
      writeObject(writer, "body", body);
      writeObject(writer, "result", result);
      writeObjects(writer);
    }

    private void writeFields(final StringBuilder writer, final String section, final List<FieldInfo> fields) {
      writeFields(writer, 0, section, fields);
    }

    private void writeFields(final StringBuilder writer, final int indent, final String section, final List<FieldInfo> fields) {
      if (fields.isEmpty()) return;

      final String pad = " ".repeat(indent);
      writer.append(pad).append("  ").append(section).append(':').append(System.lineSeparator());
      for (final FieldInfo field: fields) {
        writer.append(pad).append("    - name: ").append(field.name()).append(System.lineSeparator());
        writer.append(pad).append("      type: ").append(field.type()).append(System.lineSeparator());
      }
    }

    private void writeObject(final StringBuilder writer, final String section, final String object) {
      if (object == null) return;

      writer.append("  ").append(section).append(':').append(System.lineSeparator());
      writer.append("    - type: ").append(object).append(System.lineSeparator());
    }

    private void writeObjects(final StringBuilder writer) {
      if (objects.isEmpty()) return;

      writer.append("  objects").append(':').append(System.lineSeparator());
      for (final Map.Entry<String, List<FieldInfo>> entry: objects.entrySet()) {
        writeFields(writer, 2, "- " + entry.getKey(), entry.getValue());
      }
    }
  }

  private DocsClassBuilder createDoc(final UriRoute route, final UriMethod method, final TypeElement classElement,
      final ExecutableElement methodElement) {
    final DocsClassBuilder doc = new DocsClassBuilder(method, route.uri());

    // return type
    final ExecutableType methodType = (ExecutableType) methodElement.asType();
    doc.addResult(parseObjectType(doc.objects, methodType.getReturnType()));

    // params
    final List<? extends VariableElement> params = methodElement.getParameters();
    final List<? extends TypeMirror> paramTypes = methodType.getParameterTypes();
    for (int i = 0, n = params.size(); i < n; ++i) {
      final VariableElement param = params.get(i);
      final TypeMirror paramType = paramTypes.get(i);
      final String paramName = param.getSimpleName().toString();

      if (param.getAnnotation(UriVariable.class) != null) {
        final UriVariable uriVariable = param.getAnnotation(UriVariable.class);
        doc.addUriVariableParam(new FieldInfo(uriVariable.value(), nameOfType(paramType)));
      } else if (param.getAnnotation(UriPattern.class) != null) {
        final UriPattern uriPattern = param.getAnnotation(UriPattern.class);
        doc.addUriPatternParam(new FieldInfo(String.valueOf(uriPattern.value()), nameOfType(paramType)));
      } else if (param.getAnnotation(HeaderValue.class) != null) {
        final HeaderValue header = param.getAnnotation(HeaderValue.class);
        final String headerName = StringUtil.defaultIfEmpty(header.value(), header.name());
        doc.addHeaderParam(new FieldInfo(headerName, nameOfType(paramType)));
      } else if (param.getAnnotation(QueryParam.class) != null) {
        final QueryParam query = param.getAnnotation(QueryParam.class);
        final String queryParamName = StringUtil.defaultIfEmpty(query.value(), query.name());
        doc.addQueryParam(new FieldInfo(queryParamName, nameOfType(paramType)));
      } else if (param.getAnnotation(MetaParam.class) != null) {
        final MetaParam meta = param.getAnnotation(MetaParam.class);
        doc.addHeaderParam(new FieldInfo(meta.header(), nameOfType(paramType)));
        doc.addQueryParam(new FieldInfo(meta.query(), nameOfType(paramType)));
      } else {
        doc.addBody(parseObjectType(doc.objects, paramType));
      }
    }
    return doc;
  }

  // ==========================================================================================
  // Object Fiels extractor
  // ==========================================================================================
  private String parseObjectType(final Map<String, List<FieldInfo>> objects, final TypeMirror type) {
    if (type.getKind() == TypeKind.VOID) return null;

    if (isByteTypeArray(type)) {
      return "byte[]";
    } else if (isFilePath(type)) {
      return "File";
    } else if (isSameType(type, messageType)) {
      return nameOfType(type);
    } else if (isTypeAssignable(type, messageType)) {
      return nameOfType(type);
    }

    return switch (type.getKind()) {
      case ARRAY -> parseArrayType(objects, (ArrayType)type);
      case DECLARED -> parseDeclaredType(objects, (DeclaredType)type);
      default -> nameOfType(type);
    };
  }

  private String parseArrayType(final Map<String, List<FieldInfo>> objects, final ArrayType type) {
    final String componentType = parseObjectType(objects, type.getComponentType());
    return "List[" + componentType + "]";
  }

  private String parseDeclaredType(final Map<String, List<FieldInfo>> objects, final DeclaredType type) {
    final List<? extends TypeMirror> typeArgs = type.getTypeArguments();
    if (isListType(type)) {
      final String componentType = parseObjectType(objects, typeArgs.getFirst());
      return "List[" + componentType + "]";
    } else if (isSetType(type)) {
      final String componentType = parseObjectType(objects, typeArgs.getFirst());
      return "Set[" + componentType + "]";
    } else if (isMapType(type) && !typeArgs.isEmpty()) {
      final String keyType = parseObjectType(objects, typeArgs.get(0));
      final String valType = parseObjectType(objects, typeArgs.get(1));
      return "Map[" + keyType + "," + valType + "]";
    }

    if (type.toString().startsWith("java.lang.")) {
      return nameOfType(type);
    }

    final Element element = type.asElement();
    switch (element.getKind()) {
      case RECORD, CLASS, INTERFACE:
        final String objectName = nameOfType(type);
        final List<FieldInfo> fields = extractClassFields(objects, (TypeElement)element, type);
        objects.put(objectName, fields);
        return objectName;
      default:
        return nameOfType(type);
    }
  }

  private static String nameOfType(final TypeMirror type) {
    final String name = type.toString();
    if (name.startsWith("java.lang.")) {
      return name.substring(10);
    }
    return name;
  }

  private List<FieldInfo> extractClassFields(final Map<String, List<FieldInfo>> objects, final TypeElement element, final DeclaredType type) {
    final ArrayList<FieldInfo> fields = new ArrayList<>();
    // Process fields
    for (final Element enclosedElement : element.getEnclosedElements()) {
      if (enclosedElement.getKind() == ElementKind.FIELD) {
        final VariableElement field = (VariableElement) enclosedElement;
        final String fieldName = field.getSimpleName().toString();
        final TypeMirror fieldType = field.asType();

        // Check if the field is serializable
        if (isSerializable(fieldType, field.getModifiers())) {
          fields.add(new FieldInfo(fieldName, parseObjectType(objects, fieldType)));
        }
      }
    }
    return fields;
  }


  private boolean isSerializable(final TypeMirror typeMirror, final Set<Modifier> modifiers) {
    return !modifiers.contains(Modifier.TRANSIENT) && !modifiers.contains(Modifier.STATIC);
  }

  private static final String[] METHOD_GETTER_PREFIXES = new String[] { "get", "is", "has" };
  private String getPropertyNameFromGetter(final ExecutableElement method) {
    final String methodName = method.getSimpleName().toString();
    for (int i = 0; i < METHOD_GETTER_PREFIXES.length; ++i) {
      if (methodName.startsWith(METHOD_GETTER_PREFIXES[i])) {
        return Introspector.decapitalize(methodName.substring(METHOD_GETTER_PREFIXES[i].length()));
      }
    }
    return methodName;
  }

  private boolean isGetter(final ExecutableElement method) {
    final String methodName = method.getSimpleName().toString();
    for (int i = 0; i < METHOD_GETTER_PREFIXES.length; ++i) {
      if (methodName.startsWith(METHOD_GETTER_PREFIXES[i])) {
        return true;
      }
    }
    return false;
  }
}
