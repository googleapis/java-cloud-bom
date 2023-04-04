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

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;

class ArtifactToServiceName {
  public static void main(String[] argument) throws Exception {}

  static String fetchServiceNameFromArtifact(String coordinates) throws Exception {
    Artifact artifact = new DefaultArtifact(coordinates);
    System.out.println("Artifact: " + artifact);
    ArtifactDependenciesClassLoader classLoader = new ArtifactDependenciesClassLoader(artifact);
    String stubSettingClassName = classLoader.findStubSettingsClassName();
    System.out.println("StubSetting Class: " + stubSettingClassName);
    Class<?> clazz = classLoader.findClass(stubSettingClassName);
    String defaultEndpoint = (String) clazz.getMethod("getDefaultEndpoint").invoke(null);
    System.out.println("Default endpoint: " + defaultEndpoint);
    String serviceName = defaultEndpoint.split("\\.")[0];
    System.out.println("Service name: " + serviceName);
    return serviceName;
  }
}
