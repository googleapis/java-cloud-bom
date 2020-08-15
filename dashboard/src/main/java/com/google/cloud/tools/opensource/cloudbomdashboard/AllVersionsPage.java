/*
 * Copyright 2020 Google LLC.
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

package com.google.cloud.tools.opensource.cloudbomdashboard;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.aether.artifact.Artifact;

public class AllVersionsPage {

  /* There can only be one version of our 'All Versions' page */
  public static final String ALL_VERSIONS_NAME = "all-versions";
  private static final AllVersionsPage ALL_VERSIONS_PAGE = new AllVersionsPage();

  private static final Set<String> versions = new HashSet<>();
  private static final Set<Artifact> artifacts = new HashSet<>();

  private AllVersionsPage() {
  }

  public static void addToAllVersions(VersionData versionData) {
    artifacts.addAll(versionData.getArtifacts());
    versions.add(versionData.getCloudBomVersion());
  }

  public static Map<String, Object> getAllVersionsTemplateData() {
    // Freemarker template
    Map<String, Object> templateData = new HashMap<>();

    // Mappings used within Freemarker file
    // All mappings are of the form key=artifactId:cloudBomVersion
    // value=currentVersion of artifact in cloudBomVersion,
    // value=newestVersion of artifact, etc.
    Map<String, String> currentVersion = new HashMap<>();
    Map<String, String> sharedDependenciesPosition = new HashMap<>();
    Map<String, String> newestVersion = new HashMap<>();
    Map<String, String> newestPomUrl = new HashMap<>();
    Map<String, String> sharedDepsVersion = new HashMap<>();
    Map<String, String> updatedTime = new HashMap<>();
    Map<String, String> metadataUrl = new HashMap<>();

    Set<String> artifactIds = new HashSet<>();
    for (String cloudBomVersion : versions) {
      for (Artifact artifact : artifacts) {
        String artifactId = artifact.getArtifactId();
        // Concatenate this with the version of the current cloud BOM, so each entry has a unique
        // key for the mapping in our table.
        String artifactKey = artifactId + ":" + cloudBomVersion;
        String groupId = artifact.getGroupId();
        String version = artifact.getVersion();

        String latestVersion = VersionData.getLatestVersionFromArtifact(artifact);
        String pomFileUrl = VersionData.getPomFileUrl(groupId, artifactId, version);
        String sharedDependencyVersion = VersionData.getDependenciesVersionFromPomUrl(pomFileUrl);

        String time = VersionData.getTimeFromArtifact(artifact);
        String metadataUrlValue = VersionData.getMetadataUrl(artifact);

        artifactIds.add(artifactId);
        currentVersion.put(artifactKey, version);
        newestVersion.put(artifactKey, latestVersion);
        newestPomUrl.put(artifactKey, pomFileUrl);
        sharedDepsVersion.put(artifactKey, sharedDependencyVersion);
        updatedTime.put(artifactKey, time);
        metadataUrl.put(artifactKey, metadataUrlValue);
      }
    }

    templateData.put("currentVersion", currentVersion);
    templateData.put("sharedDependenciesPosition", sharedDependenciesPosition);
    templateData.put("newestVersion", newestVersion);
    templateData.put("newestPomUrl", newestPomUrl);
    templateData.put("sharedDepsVersion", sharedDepsVersion);
    templateData.put("updatedTime", updatedTime);
    templateData.put("metadataUrl", metadataUrl);
    templateData.put("artifacts", artifactIds);
    templateData.put("versions", versions);
    templateData.put("staticVersion", "All Versions");
    templateData.put("lastUpdated", LocalDateTime.now());
    return templateData;
  }
}
