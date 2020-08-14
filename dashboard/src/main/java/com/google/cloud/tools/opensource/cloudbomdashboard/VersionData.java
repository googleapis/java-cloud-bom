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

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashSet;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.commons.io.FileUtils;
import org.eclipse.aether.artifact.Artifact;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.Date;

/**
 * Container class for the conversion of a dashboard into a template
 */
public class VersionData {

  /* There can only be one version of our 'All Versions' page */
  public static final String ALL_VERSIONS_NAME = "all-versions";
  private static final VersionData ALL_VERSIONS_DATA = new VersionData();

  /* Helps to improve performance. No need to repeatedly look up remote resources. */
  private static final Map<String, String> pomToDependenciesVersion = new HashMap<>();
  private static final Map<Artifact, String> artifactToTime = new HashMap<>();
  private static final Map<Artifact, String> artifactToLatestVersion = new HashMap<>();

  private final Set<String> versions = new HashSet<>();

  /* Everything associated with the template for this VersionData */
  private Set<Artifact> artifacts = new HashSet<>();
  private Set<String> artifactIds = new HashSet<>();

  private static DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
  private static DateFormat outputFormat = new SimpleDateFormat("MM-dd-yyyy");

  public VersionData(String cloudBomVersion) {
    versions.add(cloudBomVersion);
  }

  private VersionData() {
  }

  public void addData(Set<Artifact> artifacts) {
    this.artifacts.addAll(artifacts);
  }

  public static void addArtifactsToAllVersions(Set<Artifact> artifacts) {
    ALL_VERSIONS_DATA.addData(artifacts);
  }

  public static void addVersionToAllVersions(String version) {
    ALL_VERSIONS_DATA.versions.add(version);
  }

  /**
   * Creates new mapping of template data for use in creating the All Versions page.
   */
  public static Map<String, Object> getAllVersionsTemplate() {
    return ALL_VERSIONS_DATA.getTemplateData();
  }

  /**
   * Returns the 'template data' formatting of our data.
   */
  public Map<String, Object> getTemplateData() {
    //Freemarker template
    Map<String, Object> templateData = new HashMap<>();

    //Mappings used within Freemarker file
    Map<String, String> currentVersion = new HashMap<>();
    Map<String, String> sharedDependenciesPosition = new HashMap<>();
    Map<String, String> newestVersion = new HashMap<>();
    Map<String, String> newestPomUrl = new HashMap<>();
    Map<String, String> sharedDepsVersion = new HashMap<>();
    Map<String, String> updatedTime = new HashMap<>();
    Map<String, String> metadataUrl = new HashMap<>();

    for (String cloudBomVersion : versions) {
      for (Artifact artifact : artifacts) {
        String artifactId = artifact.getArtifactId();
        //Concatenate this with the version of the current cloud BOM, so each entry has a unique
        //key for the mapping in our table.
        String artifactKey = artifactId + ":" + cloudBomVersion;
        String groupId = artifact.getGroupId();
        String version = artifact.getVersion();

        String latestVersion = latestVersion(artifact);
        String pomFileURL = getPomFileURL(groupId, artifactId, version);
        String sharedDependencyVersion = sharedDependencyVersion(artifactKey, artifact,
            sharedDependenciesPosition);

        artifactIds.add(artifactId);
        currentVersion.put(artifactKey, version);
        newestVersion.put(artifactKey, latestVersion);
        newestPomUrl.put(artifactKey, pomFileURL);
        sharedDepsVersion.put(artifactKey, sharedDependencyVersion);
        updatedTime.put(artifactKey, updatedTime(artifact));
        metadataUrl.put(artifactKey, getMetadataUrl(artifact));
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
    templateData.put("lastUpdated", LocalDateTime.now());
    //Our all-versions dashboard page is a special case
    if (versions.size() > 1) {
      templateData.put("staticVersion", "All Versions");
    } else if (versions.size() == 1) {
      String onlyElement = versions.stream().findAny().get();
      templateData.put("staticVersion", onlyElement);
    }
    return templateData;
  }

  private static String latestVersion(Artifact artifact) {
    if (artifactToLatestVersion.containsKey(artifact)) {
      return artifactToLatestVersion.get(artifact);
    }
    String metadataPath = getMetadataUrl(artifact);
    File metadataFile = new File("metadata.xml");
    try {
      URL url = new URL(metadataPath);
      FileUtils.copyURLToFile(url, metadataFile);
      String latest = XmlGrabber.grabMetadataValue(metadataFile, "latest");
      artifactToLatestVersion.put(artifact, latest);
      return latest;
    } catch (IOException ignored) {
      artifactToLatestVersion.put(artifact, "");
      return "";
    }
  }

  private static String updatedTime(Artifact artifact) {
    if (artifactToTime.containsKey(artifact)) {
      return artifactToTime.get(artifact);
    }
    String metadataPath = getMetadataUrl(artifact);
    File metadataFile = new File("metadata.xml");
    try {
      URL url = new URL(metadataPath);
      BufferedInputStream input = new BufferedInputStream(url.openStream());
      FileUtils.copyInputStreamToFile(input, metadataFile);
      String lastUpdated = XmlGrabber.grabMetadataValue(metadataFile, "lastUpdated");
      if (!lastUpdated.isEmpty()) {
        Date date = dateFormat.parse(lastUpdated);
        lastUpdated = outputFormat.format(date);
      }
      artifactToTime.put(artifact, lastUpdated);
      return lastUpdated;
    } catch (IOException | ParseException ignored) {
      artifactToTime.put(artifact, "");
    }
    return "";
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
    String pomPath = getPomFileURL(artifact.getGroupId(), artifact.getArtifactId(),
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
    File pomFile = new File("pomFile.xml");
    pomFile.deleteOnExit();
    try {
      URL url = new URL(pomUrl);
      BufferedInputStream input = new BufferedInputStream(url.openStream());
      FileUtils.copyInputStreamToFile(input, pomFile);
      MavenXpp3Reader read = new MavenXpp3Reader();
      Model model = read.read(new FileReader(pomFile));
      if (model.getDependencyManagement() == null) {
        return null;
      }
      for (org.apache.maven.model.Dependency dep : model.getDependencyManagement()
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

  private static String getPomFileURL(String groupId, String artifactId, String version) {
    String groupPath = groupId.replace('.', '/');
    return DashboardMain.basePath + "/" + groupPath
        + "/" + artifactId
        + "/" + version
        + "/" + artifactId + "-" + version + ".pom";
  }

  private static String getMetadataUrl(Artifact artifact) {
    String groupPath = artifact.getGroupId().replace('.', '/');
    return DashboardMain.basePath + "/" + groupPath
        + "/" + artifact.getArtifactId()
        + "/maven-metadata.xml";
  }
}