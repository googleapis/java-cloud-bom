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
import java.io.IOException;
import java.io.BufferedInputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Container class for all artifact data pulled from Maven central.
 */
public class ArtifactMavenData {

  private static DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
  private static DateFormat outputFormat = new SimpleDateFormat("MM-dd-yyyy");

  private final Artifact artifact;

  private final String latestVersion, lastUpdated,
      sharedDependenciesVersion, sharedDependenciesPosition,
      pomFileUrl, metadataUrl;

  private ArtifactMavenData(Artifact artifact, String latestVersion,
      String lastUpdated, String sharedDependenciesVersion,
      String sharedDependenciesPosition,
      String pomFileUrl, String metadataUrl) {
    this.artifact = artifact;
    this.latestVersion = latestVersion;
    this.lastUpdated = lastUpdated;
    this.sharedDependenciesVersion = sharedDependenciesVersion;
    this.sharedDependenciesPosition = sharedDependenciesPosition;

    this.pomFileUrl = pomFileUrl;
    this.metadataUrl = metadataUrl;
  }

  public Artifact getArtifact() {
    return artifact;
  }

  public String getSharedDependenciesPosition() {
    return sharedDependenciesPosition;
  }

  public String getPomFileUrl() {
    return pomFileUrl;
  }

  public String getMetadataUrl() {
    return metadataUrl;
  }

  public String getLatestVersion() {
    return latestVersion;
  }

  public String getLastUpdated() {
    return lastUpdated;
  }

  public String getSharedDependenciesVersion() {
    return sharedDependenciesVersion;
  }

  public static ArtifactMavenData generateArtifactMavenData(Artifact artifact) {
    String metadataUrl = generateMetadataUrl(artifact);
    String pomFileUrl = generatePomFileUrl(artifact);

    LatestMetadata metadata = getLatestVersionAndLastUpdated(metadataUrl);

    String latestVersion = metadata.latestVersion;
    String lastUpdated = metadata.lastUpdated;

    SharedDependenciesData data = sharedDependencyPositionAndVersion(pomFileUrl, artifact);

    String sharedDependenciesPosition = data.sharedDependencyPosition;
    String sharedDependenciesVersion = data.sharedDependencyVersion;

    return new ArtifactMavenData(artifact, latestVersion, lastUpdated,
        sharedDependenciesVersion, sharedDependenciesPosition,
        metadataUrl, pomFileUrl);
  }

  private static LatestMetadata getLatestVersionAndLastUpdated(String metadataUrl) {
    try {
      File metadataFile = File.createTempFile("metadata", ".xml");
      metadataFile.deleteOnExit();

      URL url = new URL(metadataUrl);
      FileUtils.copyURLToFile(url, metadataFile);

      MetadataXpp3Reader reader = new MetadataXpp3Reader();
      Metadata metadata = reader.read(new FileInputStream(metadataFile));

      if (metadata.getVersioning() == null) {
        return new LatestMetadata("", "");
      }

      String latestVersion;
      if (metadata.getVersioning().getLatest() == null) {
        latestVersion = "";
      } else {
        latestVersion = metadata.getVersioning().getLatest();
      }

      String lastUpdated = metadata.getVersioning().getLastUpdated();
      if (lastUpdated != null && !lastUpdated.isEmpty()) {
        Date date = dateFormat.parse(lastUpdated);
        lastUpdated = outputFormat.format(date);
      } else {
        lastUpdated = "";
      }
      return new LatestMetadata(latestVersion, lastUpdated);
    } catch (XmlPullParserException | ParseException | IOException ignored) {
      return new LatestMetadata("", "");
    }
  }

  private static SharedDependenciesData sharedDependencyPositionAndVersion(String pomUrl,
      Artifact artifact) {
    String groupPath = artifact.getGroupId().replace('.', '/');
    String parentPath = DashboardMain.basePath + "/" + groupPath
        + "/" + artifact.getArtifactId() + "-parent"
        + "/" + artifact.getVersion()
        + "/" + artifact.getArtifactId() + "-parent-" + artifact.getVersion() + ".pom";
    String depsBomPath = DashboardMain.basePath + "/" + groupPath
        + "/" + artifact.getArtifactId() + "-deps-bom"
        + "/" + artifact.getVersion()
        + "/" + artifact.getArtifactId() + "-deps-bom-" + artifact.getVersion() + ".pom";
    String repository = artifact.getArtifactId().substring(13);
    String releasePath =
        "https://raw.githubusercontent.com/googleapis/java-"
            + repository
            + "/v"
            + artifact.getVersion().replaceAll("[^.\\d]", "")
            + "/pom.xml";
    String version = getSharedDependenciesVersionFromUrl(parentPath);
    if (version != null) {
      return new SharedDependenciesData(parentPath, version);
    }
    version = getSharedDependenciesVersionFromUrl(pomUrl);
    if (version != null) {
      return new SharedDependenciesData(pomUrl, version);
    }
    version = getSharedDependenciesVersionFromUrl(releasePath);
    if (version != null) {
      return new SharedDependenciesData(releasePath, version);
    }
    version = getSharedDependenciesVersionFromUrl(depsBomPath);
    if (version != null) {
      return new SharedDependenciesData(depsBomPath, version);
    }
    return new SharedDependenciesData("", "");
  }

  private static String getSharedDependenciesVersionFromUrl(String pomUrl) {
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
          if (dep.getVersion().startsWith("${")) {
            String sharedVersion = dep.getVersion().substring(1).replaceAll("[{}]", "");
            return model.getProperties().getProperty(sharedVersion);
          }
          return dep.getVersion();
        }
      }

    } catch (XmlPullParserException | IOException ignored) {
    }
    return null;
  }

  private static String generatePomFileUrl(Artifact artifact) {
    String groupPath = artifact.getGroupId().replace('.', '/');
    return DashboardMain.basePath + "/" + groupPath
        + "/" + artifact.getArtifactId()
        + "/" + artifact.getVersion()
        + "/" + artifact.getArtifactId() + "-" + artifact.getVersion() + ".pom";
  }

  private static String generateMetadataUrl(Artifact artifact) {
    String groupPath = artifact.getGroupId().replace('.', '/');
    return DashboardMain.basePath + "/" + groupPath
        + "/" + artifact.getArtifactId()
        + "/maven-metadata.xml";
  }

  private static class LatestMetadata {

    String latestVersion, lastUpdated;

    LatestMetadata(String latestVersion, String lastUpdated) {
      this.latestVersion = latestVersion;
      this.lastUpdated = lastUpdated;
    }
  }

  private static class SharedDependenciesData {

    String sharedDependencyPosition, sharedDependencyVersion;

    SharedDependenciesData(String sharedDependencyPosition, String sharedDependencyVersion) {
      this.sharedDependencyPosition = sharedDependencyPosition;
      this.sharedDependencyVersion = sharedDependencyVersion;
    }
  }
}
