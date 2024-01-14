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

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import io.github.matteobertozzi.rednaco.collections.arrays.ArrayUtil;
import io.github.matteobertozzi.rednaco.dispatcher.MessageExecutor.ExecutionType;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.NoTraceDump;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.execution.AsyncResult;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.execution.InlineFast;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.execution.Slow;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.message.HeaderValue;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.message.MetaParam;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.message.QueryParam;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.session.AllowPublicAccess;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.session.RequirePermission;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.session.TokenSession;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.uri.UriMapping;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.uri.UriPattern;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.uri.UriPatternMapping;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.uri.UriPrefix;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.uri.UriVariable;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.uri.UriVariableMapping;
import io.github.matteobertozzi.rednaco.dispatcher.message.Message;
import io.github.matteobertozzi.rednaco.dispatcher.routing.RoutePathUtil;
import io.github.matteobertozzi.rednaco.dispatcher.routing.RoutePathUtil.RouterPathSpec;
import io.github.matteobertozzi.rednaco.dispatcher.routing.UriMethod;
import io.github.matteobertozzi.rednaco.dispatcher.session.AuthSession;
import io.github.matteobertozzi.rednaco.strings.StringFormat;
import io.github.matteobertozzi.rednaco.strings.StringUtil;

public class UriMappingProcessor extends AbstractProcessor {
  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Set.of(
      UriMapping.class.getCanonicalName(),
      UriVariableMapping.class.getCanonicalName(),
      UriPatternMapping.class.getCanonicalName()
    );
  }

  private TypeMirror authSessionType;
  private TypeMirror messageType;

  @Override
  public synchronized void init(final ProcessingEnvironment processingEnv) {
    super.init(processingEnv);

    this.authSessionType = processingEnv.getElementUtils().getTypeElement(AuthSession.class.getCanonicalName()).asType();
    this.messageType = processingEnv.getElementUtils().getTypeElement(Message.class.getCanonicalName()).asType();
  }

  // ====================================================================================================
  //  Process @Uri*Mapping
  // ====================================================================================================
  @Override
  public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
    if (annotations.isEmpty()) return false;

    final HashMap<String, DispatchClassBuilder> dispatchBuilder = new HashMap<>();
    processUriMapping(dispatchBuilder, roundEnv.getElementsAnnotatedWith(UriMapping.class));
    processUriVariableMapping(dispatchBuilder, roundEnv.getElementsAnnotatedWith(UriVariableMapping.class));
    processUriPatternMapping(dispatchBuilder, roundEnv.getElementsAnnotatedWith(UriPatternMapping.class));

    writeDispatchers(dispatchBuilder.values());
    return true;
  }

  private void processUriMapping(final Map<String, DispatchClassBuilder> dispatchBuilder, final Set<? extends Element> elementsAnnotatedWith) {
    if (elementsAnnotatedWith.isEmpty()) {
      log("no @UriMapping annotations found");
      return;
    }

    for (final Element element: elementsAnnotatedWith) {
      final DirectUriRoute route = parseUriMapping(element);
      processClassMethodMapping(dispatchBuilder, route.uri(), route.execMethodName(), element, builder -> builder.addDirectMapping(route));
    }
  }

  private void processUriVariableMapping(final Map<String, DispatchClassBuilder> dispatchBuilder, final Set<? extends Element> elementsAnnotatedWith) {
    if (elementsAnnotatedWith.isEmpty()) {
      log("no @UriVariableMapping annotations found");
      return;
    }

    for (final Element element: elementsAnnotatedWith) {
      final PatternUriRoute route = parseUriVariableMapping(element);
      processClassMethodMapping(dispatchBuilder, route.uri(), route.execMethodName(), element, builder -> builder.addVariableMapping(route));
    }
  }

  private void processUriPatternMapping(final Map<String, DispatchClassBuilder> dispatchBuilder, final Set<? extends Element> elementsAnnotatedWith) {
    if (elementsAnnotatedWith.isEmpty()) {
      log("no @UriPatternMapping annotations found");
      return;
    }

    for (final Element element: elementsAnnotatedWith) {
      final PatternUriRoute route = parseUriPatternMapping(element);
      processClassMethodMapping(dispatchBuilder, route.uri(), route.execMethodName(), element, builder -> builder.addPatternMapping(route));
    }
  }

  private void processClassMethodMapping(final Map<String, DispatchClassBuilder> dispatchBuilder,
      final String uri, final String execMethodName, final Element element,
      final Consumer<DispatchClassBuilder> builderConsumer) {
    final TypeElement classElement = (TypeElement)element.getEnclosingElement();
    final ExecutableElement methodElement = (ExecutableElement)element;
    final String fullClassName = classElement.getQualifiedName().toString();

    final DispatchClassBuilder builder = dispatchBuilder.computeIfAbsent(fullClassName, DispatchClassBuilder::new);
    generateMethodMapping(builder, uri, execMethodName, classElement, methodElement);
    builderConsumer.accept(builder);
  }

  private DirectUriRoute parseUriMapping(final Element element) {
    final UriPrefix uriPrefix = element.getEnclosingElement().getAnnotation(UriPrefix.class);
    final UriMapping uri = element.getAnnotation(UriMapping.class);
    final boolean noTraceDump = element.getAnnotation(NoTraceDump.class) != null;
    final ExecutionType execType = parseExecutionType(element);
    final String execMethodName = execMethodName(element);
    final String fullUri = (uriPrefix != null) ? uriPrefix.value() + uri.uri() : uri.uri();
    return new DirectUriRoute(uri.method(), fullUri, execType, execMethodName, noTraceDump);
  }

  private PatternUriRoute parseUriVariableMapping(final Element element) {
    final UriPrefix uriPrefix = element.getEnclosingElement().getAnnotation(UriPrefix.class);
    final UriVariableMapping uri = element.getAnnotation(UriVariableMapping.class);
    final boolean noTraceDump = element.getAnnotation(NoTraceDump.class) != null;
    final ExecutionType execType = parseExecutionType(element);
    final String execMethodName = execMethodName(element);
    final String fullUri = (uriPrefix != null) ? uriPrefix.value() + uri.uri() : uri.uri();
    final RouterPathSpec spec = RoutePathUtil.parsePathWithVariables(fullUri);
    return new PatternUriRoute(uri.method(), fullUri, execType, execMethodName, noTraceDump, spec.path(), spec.pattern());
  }

  private PatternUriRoute parseUriPatternMapping(final Element element) {
    final UriPrefix uriPrefix = element.getEnclosingElement().getAnnotation(UriPrefix.class);
    final UriPatternMapping uri = element.getAnnotation(UriPatternMapping.class);
    final boolean noTraceDump = element.getAnnotation(NoTraceDump.class) != null;
    final ExecutionType execType = parseExecutionType(element);
    final String execMethodName = execMethodName(element);
    final String fullUri = (uriPrefix != null) ? uriPrefix.value() + uri.uri() : uri.uri();
    final RouterPathSpec spec = RoutePathUtil.parsePathWithPattern(fullUri);
    return new PatternUriRoute(uri.method(), fullUri, execType, execMethodName, noTraceDump, spec.path(), spec.pattern());
  }

  private ExecutionType parseExecutionType(final Element element) {
    if (element.getAnnotation(InlineFast.class) != null) {
      return ExecutionType.INLINE_FAST;
    } else if (element.getAnnotation(AsyncResult.class) != null) {
      return ExecutionType.ASYNC;
    } else if (element.getAnnotation(Slow.class) != null) {
      final Slow slow = element.getAnnotation(Slow.class);
      return switch (slow.value()) {
        case CPU_BOUND -> ExecutionType.CPU_SLOW;
        case IO_BOUND -> ExecutionType.IO_SLOW;
      };
    } else {
      return ExecutionType.DEFAULT;
    }
  }

  public record DirectUriRoute(UriMethod[] methods, String uri, ExecutionType execType, String execMethodName, boolean noTraceDump) {}
  public record PatternUriRoute(UriMethod[] methods, String uri, ExecutionType execType, String execMethodName, boolean noTraceDump, byte[] path, Pattern pattern) {}

  private long methodSeqId = 0;
  private String execMethodName(final Element method) {
    return "exec_" + Long.toString(methodSeqId++, 32) + "_" + method.getSimpleName();
  }

  // ====================================================================================================
  //  Dispatch Code Generator
  // ====================================================================================================
  private static class DispatchClassBuilder {
    private final ArrayList<PatternUriRoute> variableMappings = new ArrayList<>();
    private final ArrayList<PatternUriRoute> patternMappings = new ArrayList<>();
    private final ArrayList<DirectUriRoute> directMappings = new ArrayList<>();
    private final ArrayList<String> methodsCode = new ArrayList<>();

    private final String fullName;
    private final String name;
    private final String ns;

    private boolean hasPublicEmptyConstructor = false;

    private DispatchClassBuilder(final String fullName) {
      final int nsEndIndex = fullName.lastIndexOf('.');
      this.fullName = fullName;
      this.ns = fullName.substring(0, nsEndIndex);
      this.name = fullName.substring(nsEndIndex + 1);
    }

    public void setHasPublicEmptyConstructor() {
      hasPublicEmptyConstructor = true;
    }

    public void addDirectMapping(final DirectUriRoute route) {
      directMappings.add(route);
    }

    public void addVariableMapping(final PatternUriRoute route) {
      variableMappings.add(route);
    }

    public void addPatternMapping(final PatternUriRoute route) {
      patternMappings.add(route);
    }

    public void addMethodCode(final CodeBuilder code) {
      methodsCode.add(code.toString());
    }

    private void writeSource(final ProcessingEnvironment processingEnv) throws IOException {
      final String resolverClassName = name + "RouteMapping";

      final JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(ns + ".autogen." + resolverClassName);
      try (final FluentPrintWriter out = new FluentPrintWriter(fileObject.openWriter())) {
        out.add("// Autogen from @UriMapping processor ").add(ZonedDateTime.now()).addLine();
        out.add("package ").add(ns).addLine(".autogen;");
        out.addLine();
        out.addLine("import java.util.regex.Pattern;");
        out.addLine();
        out.addLine("import io.github.matteobertozzi.rednaco.dispatcher.MessageContext;");
        out.addLine("import io.github.matteobertozzi.rednaco.dispatcher.MessageDispatcher;");
        out.addLine("import io.github.matteobertozzi.rednaco.dispatcher.MessageDispatcher.DispatcherProviders;");
        out.addLine("import io.github.matteobertozzi.rednaco.dispatcher.MessageDispatcher.DispatcherContext;");
        out.addLine("import io.github.matteobertozzi.rednaco.dispatcher.MessageExecutor.ExecutionType;");
        out.addLine("import io.github.matteobertozzi.rednaco.dispatcher.message.Message;");
        out.addLine("import io.github.matteobertozzi.rednaco.dispatcher.message.MessageMetadata;");
        out.addLine("import io.github.matteobertozzi.rednaco.dispatcher.message.MessageUtil;");
        out.addLine("import io.github.matteobertozzi.rednaco.dispatcher.routing.RoutesMapping;");
        out.addLine("import io.github.matteobertozzi.rednaco.dispatcher.routing.UriMessage;");
        out.addLine("import io.github.matteobertozzi.rednaco.dispatcher.routing.UriMethod;");
        out.addLine("import io.github.matteobertozzi.rednaco.dispatcher.session.AuthSession;");
        out.addLine();
        out.add("public class ").add(resolverClassName).addLine(" implements RoutesMapping {");
        out.addLine("  private final DispatcherProviders dispatcher;");
        out.add("  private final ").add(fullName).addLine(" target;");
        out.addLine();
        if (hasPublicEmptyConstructor) {
          out.add("  public ").add(resolverClassName).add("(final MessageDispatcher dispatcher) {").addLine();
          out.add("    this(dispatcher, new ").add(fullName).add("());").addLine();
          out.addLine("  }");
          out.addLine();
        }
        out.add("  public ").add(resolverClassName).add("(final MessageDispatcher dispatcher, final ").add(fullName).addLine(" target) {");
        out.addLine("    this.dispatcher = dispatcher.providers();");
        out.addLine("    this.target = target;");
        out.addLine("  }");
        out.addLine();
        out.addLine("  // RouteMapping");
        // direct mappings
        out.addLine("  private final DirectRouteMapping[] DIRECT_MAPPINGS = new DirectRouteMapping[] {");
        for (final DirectUriRoute mapping: directMappings) {
          out.add("    new DirectRouteMapping(");
          writeUriMethods(out, mapping.methods());
          out.add(", \"").add(mapping.uri()).add("\", ");
          out.add("ExecutionType.").add(mapping.execType().name()).add(", ");
          out.add("this::").add(mapping.execMethodName()).addLine("),");
        }
        out.addLine("  };");
        // variable mappings
        out.addLine("  private final PatternRouteMapping[] VARIABLE_MAPPINGS = new PatternRouteMapping[] {");
        for (final PatternUriRoute mapping: variableMappings) {
          out.add("    new PatternRouteMapping(");
          writeUriMethods(out, mapping.methods());
          out.add(", ").add("Pattern.compile(\"").add(mapping.pattern()).add("\"), ");
          out.add("ExecutionType.").add(mapping.execType().name()).add(", ");
          out.add("this::").add(mapping.execMethodName()).add(", ");
          writeByteArray(out, mapping.path());
          out.addLine("),");
        }
        out.addLine("  };");
        out.addLine("  private final PatternRouteMapping[] PATTERN_MAPPINGS = new PatternRouteMapping[] {");
        for (final PatternUriRoute mapping: patternMappings) {
          out.add("    new PatternRouteMapping(");
          writeUriMethods(out, mapping.methods());
          out.add(", ").add("Pattern.compile(\"").add(mapping.pattern()).add("\"), ");
          out.add("ExecutionType.").add(mapping.execType().name()).add(", ");
          out.add("this::").add(mapping.execMethodName()).add(", ");
          writeByteArray(out, mapping.path());
          out.addLine("),");
        }
        out.addLine("  };");
        out.addLine();
        out.addLine("  @Override public DirectRouteMapping[] directRouteMappings() { return DIRECT_MAPPINGS; }");
        out.addLine("  @Override public PatternRouteMapping[] variableRouteMappings() { return VARIABLE_MAPPINGS; }");
        out.addLine("  @Override public PatternRouteMapping[] patternRouteMappings() { return PATTERN_MAPPINGS; }");

        for (final String code: methodsCode) {
          out.add(code);
        }
        out.addLine("}");
      }
    }

    private void writeUriMethods(final FluentPrintWriter writer, final UriMethod[] methods) {
      if (methods.length == 1) {
        switch (methods[0]) {
          case GET: writer.add("UriMethod.METHODS_GET"); return;
          case PUT: writer.add("UriMethod.METHODS_PUT"); return;
          case POST: writer.add("UriMethod.METHODS_POST"); return;
          case DELETE: writer.add("UriMethod.METHODS_DELETE"); return;
          default: break;
        }
      }

      int index = 0;
      writer.add("new UriMethod[] { ");
      for (final UriMethod method: methods) {
        if (index++ != 0) writer.add(", ");
        writer.add("UriMethod.").add(method.toString());
      }
      writer.add(" }");
    }
  }

  private static void writeByteArray(final FluentPrintWriter writer, final byte[] values) {
    writer.add("new byte[] {");
    for (int i = 0; i < values.length; ++i) {
      if (i != 0) writer.add(", ");
      final int v = values[i] & 0xff;
      if (v < 127) {
        writer.add(v);
      } else {
        writer.add("(byte)").add(v);
      }
    }
    writer.add("}");
  }

  private void writeDispatchers(final Collection<DispatchClassBuilder> dispatchers) {
    for (final DispatchClassBuilder builder: dispatchers) {
      try {
        builder.writeSource(processingEnv);
      } catch (final Throwable e) {
        fatalError("unable to generate uri mapper for {}: {}", builder.fullName, e.getMessage());
      }
    }
  }

  // @ClassAnnotation (e.g. UriPrefix)
  // public class Foo {
  //    @MethodAnnotation
  //    public void method(@ParamAnnotation String x) {...}
  // }
  private void generateMethodMapping(final DispatchClassBuilder classBuilder, final String uri, final String execMethodName,
      final TypeElement classElement, final ExecutableElement methodElement) {
    final ClassMethodBuilder method = new ClassMethodBuilder(classElement, methodElement);
    if (hasPublicEmptyConstructor(classElement)) {
      classBuilder.setHasPublicEmptyConstructor();
    }

    // Verify Permission annotations
    final RequirePermission requirePermission = method.removeAnnotation(RequirePermission.class);
    final AllowPublicAccess allowPublicAccess = method.removeAnnotation(AllowPublicAccess.class);
    verifyPermissionConsistency(classBuilder.fullName, method.name(), uri, requirePermission, allowPublicAccess);

    log("processing {class} {method}", classElement.getQualifiedName(), methodElement.getSimpleName());

    final String methodConstPrefix = execMethodName.toUpperCase();

    final CodeBuilder code = new CodeBuilder();
    code.addLine();
    code.indent().add("// URI: ").add(uri).addLine();

    // write permission constants
    final String permissionConstantName = methodConstPrefix + "_PERMISSIONS";
    if (requirePermission != null) {
      code.indent().add("private static final String[] ").add(permissionConstantName).add(" = {");
      final String[] permissions = ArrayUtil.isNotEmpty(requirePermission.actions()) ? requirePermission.actions() : requirePermission.oneOf();
      for (int i = 0; i < permissions.length; ++i) {
        if (i != 0) code.add(", ");
        code.add("\"").add(permissions[i]).add("\"");
      }
      code.add("};").addLine();
    }

    // write execution method code
    code.indent().add("private Message ").add(execMethodName).add("(final MessageContext ctx, final Message inMsg) throws Exception ").openBlock();

    if (requirePermission != null || method.hasParams()) {
      // Extract Session Param
      code.indent().add("// Session and Permissions").addLine();
      final int sessionParamIndex = method.findSessionParam();
      final String sessionVarName = (sessionParamIndex < 0) ? "session" : "p_" + method.param(sessionParamIndex).getSimpleName();
      if (sessionParamIndex >= 0) {
        final TypeMirror sessionType = method.paramType(sessionParamIndex);
        verifyTokenSession(classBuilder.fullName, method.name(), uri, sessionType);
        code.indent().addVariableDecl(sessionType, sessionVarName).add(" = ").add("dispatcher.verifySession(inMsg, ").add(sessionType).add(".class);").addLine();
      }

      if (requirePermission != null) {
        if (sessionParamIndex < 0) {
          final String sessionType = "AuthSession";
          code.indent().addVariableDecl(sessionType, sessionVarName).add(" = ").add("dispatcher.verifySession(inMsg, ").add(sessionType).add(".class);").addLine();
        }
        if (ArrayUtil.isNotEmpty(requirePermission.actions())) {
          code.indent().add("dispatcher.requirePermissions(").add(sessionVarName).add(", \"").add(requirePermission.module()).add("\", ").add(permissionConstantName).add(");").addLine();
        } else {
          code.indent().add("dispatcher.requireOneOfPermission(").add(sessionVarName).add(", \"").add(requirePermission.module()).add("\", ").add(permissionConstantName).add(");").addLine();
        }
      }

      // compute parameters
      code.indent().add("// Parse Params").addLine();
      if (method.hasHeaderParams()) code.indent().add("final MessageMetadata metadata = inMsg.metadata();").addLine();
      if (method.hasQueryParams()) code.indent().add("final MessageMetadata queryParams = ((UriMessage)inMsg).queryParams();").addLine();

      for (int i = 0, n = method.paramCount(); i < n; ++i) {
        if (i == sessionParamIndex) continue;
        final VariableElement p = method.param(i);
        final TypeMirror t = method.paramType(i);
        processParamMapping(code, p, t);
      }

      code.indent().add("ctx.stats().setParamParseNs(System.nanoTime() - ctx.stats().execStartNs());").addLine();
    }

    // call the real method
    code.indent().add("// Execute").addLine();
    code.indent();
    if (method.hasReturnValue()) {
      code.addVariableDecl(method.returnType(), "res").add(" = ");
    }

    code.add("target.").add(method.name()).add("(");
    for (int i = 0, n = method.paramCount(); i < n; ++i) {
      if (i != 0) code.add(", ");
      final VariableElement p = method.param(i);
      code.add("p_" + p.getSimpleName().toString());
    }
    code.add(");").addLine();

    // convert the response
    if (!method.hasReturnValue()) {
      // no response body (204 NO CONTENT)
      code.indent().add("return MessageUtil.emptyMessage();").addLine();
    } else if (isTypeAssignable(method.returnType(), messageType)) {
      // already in a Message format
      code.indent().add("return res;").addLine();
    } else if (isByteTypeArray(method.returnType())) {
      // raw bytes
      code.indent().add("return MessageUtil.newRawMessage(res);").addLine();
    } else if (isFilePath(method.returnType())) {
      // file path
      code.indent().add("return MessageUtil.newFileMessage(res);").addLine();
    } else {
      // Java object
      code.indent().add("return MessageUtil.newDataMessage(res);").addLine();
    }

    code.closeBlock();
    classBuilder.addMethodCode(code);
  }


  private void processParamMapping(final CodeBuilder code, final VariableElement param, final TypeMirror paramType) {
    final String varName = "p_" + param.getSimpleName();
    code.indent().addVariableDecl(paramType, varName).add(" = ");

    if (param.getAnnotation(UriVariable.class) != null) {
      final UriVariable uriVariable = param.getAnnotation(UriVariable.class);
      processPatternVariable(code, paramType, "((DispatcherContext)ctx).pathVariable(\"" + uriVariable.value() + "\")");
    } else if (param.getAnnotation(UriPattern.class) != null) {
      final UriPattern uriVariable = param.getAnnotation(UriPattern.class);
      processPatternVariable(code, paramType, "((DispatcherContext)ctx).pathPatternVariable(" + uriVariable.value() + ")");
    } else if (param.getAnnotation(HeaderValue.class) != null) {
      final HeaderValue headerValue = param.getAnnotation(HeaderValue.class);
      processMetadataParam(code, "metadata", param, paramType, StringUtil.defaultIfEmpty(headerValue.value(), headerValue.name()), headerValue.defaultValue());
    } else if (param.getAnnotation(QueryParam.class) != null) {
      final QueryParam queryParam = param.getAnnotation(QueryParam.class);
      processMetadataParam(code, "queryParams", param, paramType, StringUtil.defaultIfEmpty(queryParam.value(), queryParam.name()), queryParam.defaultValue());
    } else if (param.getAnnotation(MetaParam.class) != null) {
      final MetaParam metaParam = param.getAnnotation(MetaParam.class);

      // ...
    } else if (isByteTypeArray(paramType)) {
      code.add("inMsg.convertContentToBytes();");
    } else if (isSameType(paramType, messageType)) {
      code.add("inMsg;");
    } else if (isTypeAssignable(paramType, messageType)) {
      code.add("(").add(paramType).add(")").add("inMsg;");
    } else {
      code.add("MessageUtil.convertInputContent(inMsg, ").add(paramType).add(".class);");
    }

    code.addLine();
  }

  private static void processPatternVariable(final CodeBuilder code, final TypeMirror paramType, final String fetchPathVariable) {
    switch (paramType.getKind()) {
      case BOOLEAN -> code.add("Boolean.parseBoolean(").add(fetchPathVariable).add(");");
      case SHORT -> code.add("Short.parseShort(").add(fetchPathVariable).add(");");
      case INT -> code.add("Integer.parseInt(").add(fetchPathVariable).add(");");
      case LONG -> code.add("Long.parseLong(").add(fetchPathVariable).add(");");
      case FLOAT -> code.add("Float.parseFloat(").add(fetchPathVariable).add(");");
      case DOUBLE -> code.add("Double.parseDouble(").add(fetchPathVariable).add(");");
      case DECLARED -> {
        switch (paramType.toString()) {
          case "java.lang.String" -> code.add(fetchPathVariable).add(";");
          default -> throw new UnsupportedOperationException("unsupported @UriVariable type " + paramType.getKind() + " " + paramType);
        }
      }
      default -> throw new UnsupportedOperationException("unsupported @UriVariable type " + paramType.getKind() + " " + paramType);
    }
  }

  private static void processMetadataParam(final CodeBuilder code, final String metaParamName, final VariableElement param, final TypeMirror paramType, final String paramName, final String defaultValue) {
    switch (paramType.getKind()) {
      case BOOLEAN -> code.add(metaParamName).add(".getBoolean(\"").add(paramName).add("\", ").add(StringUtil.isEmpty(defaultValue) ? "false" : defaultValue).add(");");
      case SHORT -> code.add(metaParamName).add(".getShort(\"").add(paramName).add("\", ").add(StringUtil.isEmpty(defaultValue) ? "0" : defaultValue).add(");");
      case INT -> code.add(metaParamName).add(".getInt(\"").add(paramName).add("\", ").add(StringUtil.isEmpty(defaultValue) ? "0" : defaultValue).add(");");
      case LONG -> code.add(metaParamName).add(".getLong(\"").add(paramName).add("\", ").add(StringUtil.isEmpty(defaultValue) ? "0" : defaultValue).add(");");
      case FLOAT -> code.add(metaParamName).add(".getFloat(\"").add(paramName).add("\", ").add(StringUtil.isEmpty(defaultValue) ? "0" : defaultValue).add(");");
      case DOUBLE -> code.add(metaParamName).add(".getDouble(\"").add(paramName).add("\", ").add(StringUtil.isEmpty(defaultValue) ? "0" : defaultValue).add(");");
      case DECLARED -> {
        switch (paramType.toString()) {
          case "java.lang.String" -> code.add(metaParamName).add(".getString(\"").add(paramName).add("\", ").add(StringUtil.isEmpty(defaultValue) ? "null" : '"' + defaultValue + '"').add(");");
          case "java.util.List<java.lang.String>" -> code.add(metaParamName).add(".getList(\"").add(paramName).add("\");");
          case "java.util.Set<java.lang.String>" -> code.add(metaParamName).add(".getStringSet(\"").add(paramName).add("\");");
          default -> throw new UnsupportedOperationException("unsupported " + paramName + " type " + paramType.getKind() + " " + paramType);
        }
      }
      case ARRAY -> {
        final TypeMirror componentType = ((ArrayType)paramType).getComponentType();
        parseMetadataArrayParam(code, metaParamName, param, componentType, paramName, defaultValue);
      }
      default -> throw new UnsupportedOperationException("unsupported " + paramName + " type " + paramType.getKind() + " " + paramType);
    }
  }

  private static void parseMetadataArrayParam(final CodeBuilder code, final String metaParamName, final VariableElement param, final TypeMirror paramType, final String paramName, final String defaultValue) {
    if (StringUtil.isNotEmpty(defaultValue)) {
      throw new UnsupportedOperationException("unsupported default value for Array param: " + param + " " + paramName + " " + defaultValue);
    }

    switch (paramType.getKind()) {
      case BOOLEAN -> code.add(metaParamName).add(".getBooleanArray(\"").add(paramName).add("\");");
      case SHORT -> code.add(metaParamName).add(".getShortArray(\"").add(paramName).add("\");");
      case INT -> code.add(metaParamName).add(".getIntArray(\"").add(paramName).add("\");");
      case LONG -> code.add(metaParamName).add(".getLongArray(\"").add(paramName).add("\");");
      case FLOAT -> code.add(metaParamName).add(".getFloatArray(\"").add(paramName).add("\");");
      case DOUBLE -> code.add(metaParamName).add(".getDoubleArray(\"").add(paramName).add("\");");
      case DECLARED -> {
        switch (paramType.toString()) {
          case "java.lang.String" -> code.add(metaParamName).add(".getStringArray(\"").add(paramName).add("\");");
          default -> throw new UnsupportedOperationException("unsupported " + paramName + " type " + paramType.getKind() + " " + paramType);
        }
      }
      default -> throw new UnsupportedOperationException("unsupported " + paramName + " Array type " + paramType.getKind() + " " + paramType);
    }
  }

  private void verifyPermissionConsistency(final String className, final String methodName, final String uri,
      final RequirePermission requirePermission, final AllowPublicAccess allowPublicAccess) {
    if (requirePermission == null && allowPublicAccess == null) {
      throw new UnsupportedOperationException(StringFormat.namedFormat(
        "missing @RequirePermission or @AllowPublicAccess on {class} {method} {uri}",
        className, methodName, uri
      ));
    }

    if (requirePermission != null && allowPublicAccess != null) {
      throw new UnsupportedOperationException(StringFormat.namedFormat(
        "conflict: both @RequirePermission and @AllowPublicAccess specified on {class} {method} {uri}",
        className, methodName, uri
      ));
    }

    if (requirePermission != null) {
      if (StringUtil.isEmpty(requirePermission.module())) {
        throw new UnsupportedOperationException(StringFormat.namedFormat(
          "@RequirePermission has missing/empty 'module' parameter on {class} {method} {uri}",
          className, methodName, uri
        ));
      }
      if (ArrayUtil.isEmpty(requirePermission.actions()) && ArrayUtil.isEmpty(requirePermission.oneOf())) {
        throw new UnsupportedOperationException(StringFormat.namedFormat(
          "@RequirePermission has missing action list on {class} {method} {uri}",
          className, methodName, uri, requirePermission
        ));
      }
      if (ArrayUtil.isNotEmpty(requirePermission.actions()) && ArrayUtil.isNotEmpty(requirePermission.oneOf())) {
        throw new UnsupportedOperationException(StringFormat.namedFormat(
          "conflict: @RequirePermission has both 'actions' and 'oneOf' on {class} {method} {uri}: {}",
          className, methodName, uri, requirePermission
        ));
      }
    }
  }

  private void verifyTokenSession(final String className, final String methodName, final String uri, final TypeMirror sessionType) {
    if (!isTypeAssignable(sessionType, authSessionType)) {
      throw new UnsupportedOperationException(StringFormat.namedFormat(
        "@TokenSession type must be a AuthSession derivate got {}. fix {class} {method} {uri}",
        sessionType, className, methodName, uri
      ));
    }
  }

  private static class ClassMethodBuilder {
    private final TypeElement classElement;
    private final ExecutableElement method;
    private final List<? extends VariableElement> params;
    private final List<? extends TypeMirror> paramTypes;
    private final ArrayList<String> methodAnnotations;

    public ClassMethodBuilder(final TypeElement classElement, final ExecutableElement method) {
      final ExecutableType methodType = (ExecutableType)method.asType();
      this.classElement = classElement;
      this.method = method;
      this.params = method.getParameters();
      this.paramTypes = methodType.getParameterTypes();

      final List<? extends AnnotationMirror> annotations = method.getAnnotationMirrors();
      methodAnnotations = new ArrayList<>(annotations.size());
      for (final AnnotationMirror annotation: annotations) {
        methodAnnotations.add(annotation.getAnnotationType().toString());
      }
    }

    public boolean hasParams() {
      return !params.isEmpty();
    }

    public int paramCount() {
      return params.size();
    }

    public VariableElement param(final int index) {
      return params.get(index);
    }

    public TypeMirror paramType(final int index) {
      return paramTypes.get(index);
    }

    public int findSessionParam() {
      int index = 0;
      for (final VariableElement p: params) {
        if (p.getAnnotation(TokenSession.class) != null) {
          return index;
        }
        index++;
      }
      return -1;
    }

    public boolean hasQueryParams() {
      for (final VariableElement p: params) {
        if (p.getAnnotation(QueryParam.class) != null || p.getAnnotation(MetaParam.class) != null) {
          return true;
        }
      }
      return false;
    }

    public boolean hasHeaderParams() {
      for (final VariableElement p: params) {
        if (p.getAnnotation(HeaderValue.class) != null || p.getAnnotation(MetaParam.class) != null) {
          return true;
        }
      }
      return false;
    }

    public <T extends Annotation> T removeAnnotation(final Class<T> annotation) {
      final T value = method.getAnnotation(annotation);
      methodAnnotations.remove(annotation.getCanonicalName());
      return value;
    }

    public String name() {
      return method.getSimpleName().toString();
    }

    public TypeMirror returnType() {
      return method.getReturnType();
    }

    public boolean hasReturnValue() {
      return method.getReturnType().getKind() != TypeKind.VOID;
    }
  }

  private static boolean isString(final TypeMirror paramType) {
    return paramType.toString().equals("java.lang.String");
  }

  public boolean isByteTypeArray(final TypeMirror typeMirror) {
    if (typeMirror.getKind() == TypeKind.ARRAY) {
      final ArrayType arrayType = (ArrayType) typeMirror;
      return arrayType.getComponentType().getKind() == TypeKind.BYTE;
    }
    return false;
  }

  private static boolean isFilePath(final TypeMirror paramType) {
    switch (paramType.toString()) {
      case "java.io.File":
      case "java.nio.file.Path":
        return true;
      default:
        return false;
    }
  }

  public boolean isTypeAssignable(final TypeMirror t1, final TypeMirror t2) {
    return processingEnv.getTypeUtils().isAssignable(t1, t2);
  }

  public boolean isSameType(final TypeMirror t1, final TypeMirror t2) {
    return processingEnv.getTypeUtils().isSameType(t1, t2);
  }

  public boolean hasPublicEmptyConstructor(final Element classElement) {
    final List<? extends Element> enclosedElements = classElement.getEnclosedElements();
    for (final Element element: enclosedElements) {
      if (element.getKind() != ElementKind.CONSTRUCTOR) continue;
      if (!element.getModifiers().contains(Modifier.PUBLIC)) continue;

      if (((ExecutableElement)element).getParameters().isEmpty()) {
        return true;
      }
    }
    return false;
  }


  // ====================================================================================================
  //  Code Generator helpers
  // ====================================================================================================
  private static class CodeBuilder {
    private final StringBuilder builder = new StringBuilder();

    private int indent = 2;

    public CodeBuilder pushIndent() {
      indent += 2;
      return this;
    }

    public CodeBuilder popIndent() {
      indent -= 2;
      return this;
    }

    public CodeBuilder indent() {
      builder.repeat(' ', indent);
      return this;
    }

    public CodeBuilder add(final Object text) {
      builder.append(text);
      return this;
    }

    public CodeBuilder addVariableDecl(final Object type, final Object name) {
      builder.append("final ");
      builder.append(type);
      builder.append(' ');
      builder.append(name);
      return this;
    }

    public CodeBuilder openBlock() { return add("{").addLine().pushIndent(); }
    public CodeBuilder closeBlock() { return popIndent().indent().add("}").addLine(); }
    public CodeBuilder addLine() { return add(System.lineSeparator()); }
    public CodeBuilder addTry() { return indent().add("try {").addLine().pushIndent(); }
    public CodeBuilder addCatch() { return popIndent().indent().add("} catch (Throwable e) {").addLine().pushIndent(); }
    public CodeBuilder addFinally() { return popIndent().indent().add("} finally {").addLine().pushIndent(); }

    public String toString() { return builder.toString(); }
  }

  private static class FluentPrintWriter implements Closeable {
    private final PrintWriter out;

    private FluentPrintWriter(final Writer writer) {
      this.out = new PrintWriter(writer);
    }

    @Override
    public void close() {
      out.close();
    }

    public FluentPrintWriter add(final Object text) {
      out.print(text);
      return this;
    }

    public FluentPrintWriter addLine(final Object text) {
      out.println(text);
      return this;
    }

    public FluentPrintWriter addLine() {
      out.println();
      return this;
    }
  }

  // ====================================================================================================
  //  Logging helpers
  // ====================================================================================================
  private void log(final String msg, final Object... args) {
    if (processingEnv.getOptions().containsKey("debug")) {
      processingEnv.getMessager().printMessage(Kind.NOTE, StringFormat.namedFormat(msg, args));
    } else {
      processingEnv.getMessager().printMessage(Kind.NOTE, StringFormat.namedFormat(msg, args));
    }
  }

  private void warning(final String msg, final Element element, final AnnotationMirror annotation) {
    processingEnv.getMessager().printMessage(Kind.WARNING, msg, element, annotation);
  }

  private void error(final String msg, final Element element, final AnnotationMirror annotation) {
    processingEnv.getMessager().printMessage(Kind.ERROR, msg, element, annotation);
  }

  private void fatalError(final String msg, final Object... args) {
    processingEnv.getMessager().printMessage(Kind.ERROR, "FATAL ERROR: " + StringFormat.namedFormat(msg, args));
  }
}
