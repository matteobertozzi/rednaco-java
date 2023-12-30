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

package io.github.matteobertozzi.rednaco.plugins;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.NoSuchFileException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

public class AutoWiredServiceProcessor extends AbstractProcessor {
  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Collections.singleton(AutoWiredServicePlugin.class.getName());
  }

  private static boolean hasAutoWiredServicePluginAnnotation(final Set<? extends TypeElement> annotations) {
    for (final TypeElement typeElement: annotations) {
      if (typeElement.getQualifiedName().contentEquals(AutoWiredServicePlugin.class.getName())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
    if (!hasAutoWiredServicePluginAnnotation(annotations)) {
      log("unsupported annotations: " + annotations);
      return false;
    }

    final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(AutoWiredServicePlugin.class);
    if (elements.isEmpty()) return true;

    final HashSet<String> services = new HashSet<>(elements.size());
    for (final Element element: elements) {
      final TypeElement typeElement = (TypeElement)element;
      services.add(typeElement.getQualifiedName().toString());
      log("Adding ServicePlugin: " + typeElement);
    }

    final String resourceFile = ServicesFiles.getPath(ServicePlugin.class);
    final Filer filer = processingEnv.getFiler();
    services.addAll(loadExistingServices(filer, resourceFile));
    return saveServices(filer, resourceFile, services);
  }

  private Set<String> loadExistingServices(final Filer filer, final String resourceName) {
    try {
      final FileObject serviceFile = filer.getResource(StandardLocation.CLASS_OUTPUT, "", resourceName);
      return loadExistingServices(serviceFile);
    } catch (final IOException e) {
      log("Unable to read " + resourceName + ", " + e);
      return Collections.emptySet();
    }
  }

  private Set<String> loadExistingServices(final FileObject serviceFile) throws IOException {
    try (InputStream stream = serviceFile.openInputStream()) {
      final Set<String> services = ServicesFiles.readServiceFile(stream);
      log("service file found: " + serviceFile.getName() + " (" + services.size() + ")");
      return services;
    } catch (final NoSuchFileException e) {
      log("no service file found: " + serviceFile);
      return Collections.emptySet();
    }
  }

  private boolean saveServices(final Filer filer, final String resourceName, final Set<String> services) {
    try {
      final FileObject fileObject = filer.createResource(StandardLocation.CLASS_OUTPUT, "", resourceName);
      saveServices(fileObject, services);
      log("service file written: " + services);
      return true;
    } catch (final IOException e) {
      fatalError("unable to write service file: " + resourceName);
      return false;
    }
  }

  private void saveServices(final FileObject serviceFile, final Set<String> services) throws IOException {
    try (OutputStream stream = serviceFile.openOutputStream()) {
      ServicesFiles.writeServiceFile(services, stream);
    }
  }

  private void log(final String msg) {
    if (processingEnv.getOptions().containsKey("debug")) {
      processingEnv.getMessager().printMessage(Kind.NOTE, msg);
    } else {
      processingEnv.getMessager().printMessage(Kind.NOTE, msg);
    }
  }

  private void fatalError(final String msg) {
    processingEnv.getMessager().printMessage(Kind.ERROR, "FATAL ERROR: " + msg);
  }
}
