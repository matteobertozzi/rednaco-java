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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

import io.github.matteobertozzi.easerinsights.logging.LogUtil;
import io.github.matteobertozzi.rednaco.dispatcher.MessageExecutor.ExecutionType;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.NoTraceDump;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.execution.AsyncQueue;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.execution.AsyncResult;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.execution.InlineFast;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.execution.Slow;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.session.RateLimited;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.uri.UriMapping;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.uri.UriPatternMapping;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.uri.UriPrefix;
import io.github.matteobertozzi.rednaco.dispatcher.annotations.uri.UriVariableMapping;
import io.github.matteobertozzi.rednaco.dispatcher.message.Message;
import io.github.matteobertozzi.rednaco.dispatcher.routing.RoutePathUtil;
import io.github.matteobertozzi.rednaco.dispatcher.routing.RoutePathUtil.RouterPathSpec;
import io.github.matteobertozzi.rednaco.dispatcher.routing.UriMethod;
import io.github.matteobertozzi.rednaco.dispatcher.session.AuthSession;
import io.github.matteobertozzi.rednaco.strings.StringFormat;

public abstract class AbstractUriMappingProcessor<T> extends AbstractProcessor {
  protected TypeMirror authSessionType;
  protected TypeMirror messageType;

  @Override
  public synchronized void init(final ProcessingEnvironment processingEnv) {
    super.init(processingEnv);

    this.authSessionType = processingEnv.getElementUtils().getTypeElement(AuthSession.class.getCanonicalName()).asType();
    this.messageType = processingEnv.getElementUtils().getTypeElement(Message.class.getCanonicalName()).asType();
  }

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

  // ====================================================================================================
  //  Process @Uri*Mapping
  // ====================================================================================================
  protected abstract void writeBuilders(Collection<T> builders);
  protected abstract void processUriMapping(final Map<String, T> builders, final DirectUriRoute route, final Element element);
  protected abstract void processUriVariableMapping(final Map<String, T> builders, final PatternUriRoute route, final Element element);
  protected abstract void processUriPatternMapping(final Map<String, T> builders, final PatternUriRoute route, final Element element);

  // ====================================================================================================
  //  Process @Uri*Mapping
  // ====================================================================================================
  public interface UriRoute {
    UriMethod[] methods();
    String uri();
    ExecutionType execType();
  }

  public record DirectUriRoute(UriMethod[] methods, String uri, ExecutionType execType, String execMethodName, boolean noTraceDump) implements UriRoute {}
  public record PatternUriRoute(UriMethod[] methods, String uri, ExecutionType execType, String execMethodName, boolean noTraceDump, byte[] path, Pattern pattern) implements UriRoute {}

  @Override
  public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
    if (annotations.isEmpty()) return false;

    log("run {} processor on annotations {}", this.getClass(), annotations);

    final HashMap<String, T> builders = new HashMap<>();
    processUriMapping(builders, roundEnv.getElementsAnnotatedWith(UriMapping.class));
    processUriVariableMapping(builders, roundEnv.getElementsAnnotatedWith(UriVariableMapping.class));
    processUriPatternMapping(builders, roundEnv.getElementsAnnotatedWith(UriPatternMapping.class));

    writeBuilders(builders.values());
    return false;
  }

  private void processUriMapping(final Map<String, T> builders, final Set<? extends Element> elementsAnnotatedWith) {
    if (elementsAnnotatedWith.isEmpty()) {
      log("no @UriMapping annotations found");
      return;
    }

    for (final Element element: elementsAnnotatedWith) {
      try {
        final DirectUriRoute route = parseUriMapping(element);
        processUriMapping(builders, route, element);
      } catch (final Throwable e) {
        fatalError(e, "failed while processing {}", element);
      }
    }
  }

  private void processUriVariableMapping(final Map<String, T> builders, final Set<? extends Element> elementsAnnotatedWith) {
    if (elementsAnnotatedWith.isEmpty()) {
      log("no @UriVariableMapping annotations found");
      return;
    }

    for (final Element element: elementsAnnotatedWith) {
      final PatternUriRoute route = parseUriVariableMapping(element);
      processUriPatternMapping(builders, route, element);
    }
  }

  private void processUriPatternMapping(final Map<String, T> builders, final Set<? extends Element> elementsAnnotatedWith) {
    if (elementsAnnotatedWith.isEmpty()) {
      log("no @UriPatternMapping annotations found");
      return;
    }

    for (final Element element: elementsAnnotatedWith) {
      final PatternUriRoute route = parseUriPatternMapping(element);
      processUriPatternMapping(builders, route, element);
    }
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
    if (element.getAnnotation(AsyncQueue.class) != null) {
      return ExecutionType.ASYNC;
    } else if (element.getAnnotation(RateLimited.class) != null) {
        return ExecutionType.ASYNC;
    } else if (element.getAnnotation(AsyncResult.class) != null) {
      return ExecutionType.ASYNC;
    } else if (element.getAnnotation(InlineFast.class) != null) {
      return ExecutionType.INLINE_FAST;
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

  private long methodSeqId = 0;
  private String execMethodName(final Element method) {
    return "exec_" + Long.toString(methodSeqId++, 32) + "_" + method.getSimpleName();
  }

  // ====================================================================================================
  //  Type helpers
  // ====================================================================================================
  protected static boolean isString(final TypeMirror paramType) {
    return paramType.toString().equals("java.lang.String");
  }

  protected static boolean isByteTypeArray(final TypeMirror typeMirror) {
    if (typeMirror instanceof final ArrayType arrayType) {
      return arrayType.getComponentType().getKind() == TypeKind.BYTE;
    }
    return false;
  }

  protected boolean isEnum(final TypeMirror typeMirror) {
    if (typeMirror.getKind() != TypeKind.DECLARED) return false;

    final Element element = this.processingEnv.getTypeUtils().asElement(typeMirror);
    return element != null && element.getKind() == ElementKind.ENUM;
  }

  protected static boolean isArray(final TypeMirror typeMirror) {
    return typeMirror.getKind() == TypeKind.ARRAY;
  }

  protected boolean isArray(final TypeMirror typeMirror, final Class<?> componentType) {
    if (typeMirror instanceof final ArrayType arrayType) {
      log("ARRAY " + arrayType);
      return isTypeAssignable(arrayType.getComponentType(), componentType);
    }
    return false;
  }

  protected static boolean isFilePath(final TypeMirror paramType) {
    switch (paramType.toString()) {
      case "java.io.File":
      case "java.nio.file.Path":
        return true;
      default:
        return false;
    }
  }

  protected boolean isTypeAssignable(final TypeMirror t1, final Class<?> t2) {
    final Elements elements = processingEnv.getElementUtils();
    final Types types = processingEnv.getTypeUtils();
    return processingEnv.getTypeUtils().isAssignable(t1, types.getDeclaredType(elements.getTypeElement(t2.getCanonicalName())));
  }

  protected boolean isTypeAssignable(final TypeMirror t1, final TypeMirror t2) {
    return processingEnv.getTypeUtils().isAssignable(t1, t2);
  }

  protected boolean isSameType(final TypeMirror t1, final TypeMirror t2) {
    return processingEnv.getTypeUtils().isSameType(t1, t2);
  }

  protected <E> boolean isSubType(final DeclaredType declaredType, final Class<E> classOfT) {
    final Elements elements = processingEnv.getElementUtils();
    final Types types = processingEnv.getTypeUtils();
    return types.isSubtype(declaredType, types.getDeclaredType(elements.getTypeElement(classOfT.getCanonicalName())));
  }

  protected boolean isListType(final DeclaredType declaredType) {
    return isSubType(declaredType, List.class);
  }

  protected boolean isSetType(final DeclaredType declaredType) {
    return isSubType(declaredType, Set.class);
  }

  protected boolean isMapType(final DeclaredType declaredType) {
    return isSubType(declaredType, Map.class);
  }

  protected boolean isCollectionType(final DeclaredType declaredType) {
    return isSubType(declaredType, Collection.class);
  }

  protected boolean isCollectionType(final DeclaredType declaredType, final Class<?> componentType) {
    if (isCollectionType(declaredType)) {
      final TypeMirror collectionComponentType = declaredType.getTypeArguments().getFirst();
      return isCollectionType(collectionComponentType, componentType);
    }
    return false;
  }

  protected boolean isCollectionType(final TypeMirror type, final Class<?> componentType) {
    if (type instanceof final DeclaredType declaredType) {
      return isCollectionType(declaredType, componentType);
    }
    return false;
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

  protected void fatalError(final Throwable exception, final String msg, final Object... args) {
    processingEnv.getMessager().printMessage(Kind.ERROR, "FATAL ERROR: " + StringFormat.namedFormat(msg, args) + " - " + exception.getMessage() + "\n" + LogUtil.stackTraceToString(exception));
  }
}
