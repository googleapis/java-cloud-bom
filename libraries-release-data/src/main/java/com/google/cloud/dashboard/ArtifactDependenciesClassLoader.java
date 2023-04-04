/*
 * Copyright 2023 Google LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.dashboard;

import com.google.cloud.tools.opensource.classpath.ClassPathBuilder;
import com.google.cloud.tools.opensource.classpath.ClassPathResult;
import com.google.cloud.tools.opensource.classpath.DependencyMediation;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.version.InvalidVersionSpecificationException;

/** A ClassLoader for the artifact and its dependencies. */
class ArtifactDependenciesClassLoader extends ClassLoader {

  private URLClassLoader urlClassLoader;

  ArtifactDependenciesClassLoader(Artifact artifact) {
    try {
      ClassPathBuilder builder = new ClassPathBuilder();
      ClassPathResult result =
          builder.resolve(ImmutableList.of(artifact), false, DependencyMediation.MAVEN);
      File[] files =
          result.getClassPath().stream()
              .map(entry -> entry.getArtifact().getFile())
              .toArray(File[]::new);
      URL[] urls = new URL[files.length];
      for (int i = 0; i < files.length; i++) {
        try {
          urls[i] = files[i].toURI().toURL();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      urlClassLoader = new URLClassLoader(urls, this);
    } catch (InvalidVersionSpecificationException e) {
      throw new RuntimeException(e);
    }
  }

  String findStubSettingsClassName() throws IOException {
    ClassPath classPath = ClassPath.from(urlClassLoader);
    for (ClassInfo classInfo : classPath.getAllClasses()) {
      String name = classInfo.getName();
      if (name.endsWith("StubSettings")) {
        return name;
      }
    }
    return null;
  }

  private static byte[] readBytesFromStream(InputStream inputStream) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    int bytesRead;
    byte[] data = new byte[1024];
    while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
      buffer.write(data, 0, bytesRead);
    }
    buffer.flush();
    return buffer.toByteArray();
  }

  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    Class<?> clazz = null;
    try {
      URL url = urlClassLoader.getResource(name.replace('.', '/') + ".class");
      if (url != null) {
        byte[] bytes =
            readBytesFromStream(
                urlClassLoader.getResourceAsStream(name.replace('.', '/') + ".class"));
        clazz = defineClass(name, bytes, 0, bytes.length);
      }
    } catch (Exception e) {
      throw new ClassNotFoundException("Couldn't find the class", e);
    }
    if (clazz == null) {
      clazz = super.findClass(name);
    }
    return clazz;
  }

  @Override
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    synchronized (getClassLoadingLock(name)) {
      Class<?> clazz = findLoadedClass(name);
      if (clazz == null) {
        try {
          clazz = getParent().loadClass(name);
        } catch (ClassNotFoundException e) {
        }
        if (clazz == null) {
          clazz = findClass(name);
        }
      }
      if (resolve) {
        resolveClass(clazz);
      }
      return clazz;
    }
  }
}
