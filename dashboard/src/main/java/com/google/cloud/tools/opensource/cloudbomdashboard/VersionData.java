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

import java.util.Arrays;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.commons.io.FileUtils;
import org.eclipse.aether.artifact.Artifact;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Date;
import java.util.HashSet;

/**
 * Container class for the conversion of a dashboard into a template
 */
public class VersionData {

  /* Helps to improve performance. No need to repeatedly look up remote resources.
   * Maps POM URL to the version of shared dependencies found within. */
  private static final Map<String, String> pomToDependenciesVersion = new HashMap<>();
  private static final Map<Artifact, String> artifactToTime = new HashMap<>();
  private static final Map<Artifact, String> artifactToLatestVersion = new HashMap<>();

  private final String cloudBomVersion;

  /* Everything associated with the template for this VersionData */
  private final Set<Artifact> artifacts = new HashSet<>();
  private final Set<String> artifactIds = new HashSet<>();

  private static DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
  private static DateFormat outputFormat = new SimpleDateFormat("MM-dd-yyyy");

  public VersionData(String cloudBomVersion) {
    this.cloudBomVersion = cloudBomVersion;
  }

  public void addData(Set<Artifact> artifacts) {
    this.artifacts.addAll(artifacts);
  }

  public Set<Artifact> getArtifacts() {
    return artifacts;
  }

  public String getCloudBomVersion() {
    return cloudBomVersion;
  }

  /**
   * Returns the 'template data' formatting of our data.
   */
  public Map<String, Object> getTemplateData() {
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

    for (Artifact artifact : artifacts) {
      String artifactId = artifact.getArtifactId();
      // Concatenate this with the version of the current cloud BOM, so each entry has a unique
      // key for the mapping in our table.
      String artifactKey = artifactId + ":" + cloudBomVersion;
      String groupId = artifact.getGroupId();
      String version = artifact.getVersion();
      setLatestVersionAndTime(artifact);

      String latestVersion = artifactToLatestVersion.get(artifact);
      String pomFileURL = getPomFileUrl(groupId, artifactId, version);
      String sharedDependencyVersion = sharedDependencyVersion(artifactKey, artifact,
          sharedDependenciesPosition);

      artifactIds.add(artifactId);
      currentVersion.put(artifactKey, version);
      newestVersion.put(artifactKey, latestVersion);
      newestPomUrl.put(artifactKey, pomFileURL);
      sharedDepsVersion.put(artifactKey, sharedDependencyVersion);
      updatedTime.put(artifactKey, artifactToTime.get(artifact));
      metadataUrl.put(artifactKey, getMetadataUrl(artifact));
    }

    templateData.put("currentVersion", currentVersion);
    templateData.put("sharedDependenciesPosition", sharedDependenciesPosition);
    templateData.put("newestVersion", newestVersion);
    templateData.put("newestPomUrl", newestPomUrl);
    templateData.put("sharedDepsVersion", sharedDepsVersion);
    templateData.put("updatedTime", updatedTime);
    templateData.put("metadataUrl", metadataUrl);
    templateData.put("artifacts", artifactIds);
    templateData.put("versions", Arrays.asList(cloudBomVersion));
    templateData.put("staticVersion", cloudBomVersion);
    templateData.put("lastUpdated", LocalDateTime.now());
    return templateData;
  }

  private static void setLatestVersionAndTime(Artifact artifact) {
    if (artifactToLatestVersion.containsKey(artifact) && artifactToTime.containsKey(artifact)) {
      return;
    }
    String metadataPath = getMetadataUrl(artifact);
    try {

      File metadataFile = File.createTempFile("metadata", ".xml");
      metadataFile.deleteOnExit();

      URL url = new URL(metadataPath);
      FileUtils.copyURLToFile(url, metadataFile);

      MetadataXpp3Reader reader = new MetadataXpp3Reader();
      Metadata metadata = reader.read(new FileInputStream(metadataFile));

      if (metadata.getVersioning() == null) {
        artifactToLatestVersion.put(artifact, "");
        artifactToTime.put(artifact, "");
        return;
      }
      if (metadata.getVersioning().getLatest() == null) {
        artifactToLatestVersion.put(artifact, "");
      } else {
        artifactToLatestVersion.put(artifact, metadata.getVersioning().getLatest());
      }

      String lastUpdated = metadata.getVersioning().getLastUpdated();
      if (lastUpdated != null && !lastUpdated.isEmpty()) {
        Date date = dateFormat.parse(lastUpdated);
        lastUpdated = outputFormat.format(date);
      }
      artifactToTime.put(artifact, lastUpdated);
    } catch (XmlPullParserException | ParseException | IOException ignored) {
      artifactToLatestVersion.put(artifact, "");
      artifactToTime.put(artifact, "");
    }
  }

  /**
   * @param key                        key to use when inserting the artifact's associated path
   *                                   into
   * @param artifact                   artifact to add into the map
   * @param sharedDependenciesPosition the map receiving the path associated with this artifact
   * @return the version of shared-dependencies if found. Returns the empty string otherwise
   */
  private static String sharedDependencyVersion(String key, Artifact artifact,
      Map<String, String> sharedDependenciesPosition) {
    String groupPath = artifact.getGroupId().replace('.', '/');
    String pomPath = getPomFileUrl(artifact.getGroupId(), artifact.getArtifactId(),
        artifact.getVersion());
    String parentPath = DashboardMain.basePath + "/" + groupPath
        + "/" + artifact.getArtifactId() + "-parent"
        + "/" + artifact.getVersion()
        + "/" + artifact.getArtifactId() + "-parent-" + artifact.getVersion() + ".pom";
    String depsBomPath = DashboardMain.basePath + "/" + groupPath
        + "/" + artifact.getArtifactId() + "-deps-bom"
        + "/" + artifact.getVersion()
        + "/" + artifact.getArtifactId() + "-deps-bom-" + artifact.getVersion() + ".pom";
    String version = getSharedDependenciesVersionFromUrl(parentPath);
    if (version != null) {
      sharedDependenciesPosition.put(key, parentPath);
      return version;
    }
    version = getSharedDependenciesVersionFromUrl(pomPath);
    if (version != null) {
      sharedDependenciesPosition.put(key, pomPath);
      return version;
    }
    version = getSharedDependenciesVersionFromUrl(depsBomPath);
    if (version != null) {
      sharedDependenciesPosition.put(key, depsBomPath);
      return version;
    }
    sharedDependenciesPosition.put(key, "");
    return "";
  }

  private static String getSharedDependenciesVersionFromUrl(String pomUrl) {
    if (pomToDependenciesVersion.containsKey(pomUrl)) {
      return pomToDependenciesVersion.get(pomUrl);
    }
    try {
      File pomFile = File.createTempFile("pomFile", ".xml");
      pomFile.deleteOnExit();
      URL url = new URL(pomUrl);
      BufferedInputStream input = new BufferedInputStream(url.openStream());
      FileUtils.copyInputStreamToFile(input, pomFile);
      MavenXpp3Reader read = new MavenXpp3Reader();
      Model model = read.read(new FileInputStream(pomFile));
      if (model.getDependencyManagement() == null) {
        return null;
      }
      for (Dependency dep : model.getDependencyManagement()
          .getDependencies()) {
        if ("com.google.cloud".equals(dep.getGroupId()) && "google-cloud-shared-dependencies"
            .equals(dep.getArtifactId())) {
          pomToDependenciesVersion.put(pomUrl, dep.getVersion());
          return dep.getVersion();
        }
      }

    } catch (XmlPullParserException | IOException ignored) {
    }
    return null;
  }

  public static String getLatestVersionFromArtifact(Artifact artifact) {
    return artifactToLatestVersion.get(artifact);
  }

  public static String getTimeFromArtifact(Artifact artifact) {
    return artifactToTime.get(artifact);
  }

  public static String getDependenciesVersionFromPomUrl(String pomUrl) {
    return pomToDependenciesVersion.get(pomUrl);
  }

  public static String getPomFileUrl(String groupId, String artifactId, String version) {
    String groupPath = groupId.replace('.', '/');
    return DashboardMain.basePath + "/" + groupPath
        + "/" + artifactId
        + "/" + version
        + "/" + artifactId + "-" + version + ".pom";
  }

  public static String getMetadataUrl(Artifact artifact) {
    String groupPath = artifact.getGroupId().replace('.', '/');
    return DashboardMain.basePath + "/" + groupPath
        + "/" + artifact.getArtifactId()
        + "/maven-metadata.xml";
  }
}
