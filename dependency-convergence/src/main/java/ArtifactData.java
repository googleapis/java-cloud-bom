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

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Scm;
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
import org.eclipse.aether.artifact.DefaultArtifact;

/**
 * Container class for all artifact data pulled from Maven central.
 */
public class ArtifactData {

  private static final String basePath = "https://repo1.maven.org/maven2";
  private static DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
  private static DateFormat outputFormat = new SimpleDateFormat("MM-dd-yyyy");

  private final Artifact artifact;

  private final String latestVersion, lastUpdated, pomFileUrl, metadataUrl,
      scmGithubUrl, githubPomUrl, sharedDependenciesVersion;

  private ArtifactData(Artifact artifact, String latestVersion,
      String lastUpdated, String pomFileUrl, String metadataUrl, String scmGithubUrl,
      String githubPomUrl, String sharedDependenciesVersion) {
    this.artifact = artifact;
    this.latestVersion = latestVersion;
    this.lastUpdated = lastUpdated;

    this.pomFileUrl = pomFileUrl;
    this.metadataUrl = metadataUrl;

    this.scmGithubUrl = scmGithubUrl;
    this.githubPomUrl = githubPomUrl;

    this.sharedDependenciesVersion = sharedDependenciesVersion;
  }

  public Artifact getArtifact() {
    return artifact;
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

  public boolean hasValidGithubPomUrl() {
    return getGithubPomUrl() != null;
  }

  public String getGithubPomUrl() {
    return githubPomUrl;
  }

  public boolean foundValidScmSection() {
    return getScmGithubUrl() != null;
  }

  public String getScmGithubUrl() {
    return scmGithubUrl;
  }

  public String getSharedDependenciesVersion() {
    return sharedDependenciesVersion;
  }

  public static ArtifactData generateArtifactData(Artifact artifact) {
    String metadataUrl = generateMetadataUrl(artifact);

    LatestMetadata metadata = getLatestVersionAndLastUpdated(metadataUrl);
    String latestVersion = metadata.latestVersion;
    String lastUpdated = metadata.lastUpdated;

    //The artifact given to us may not be present on Maven central yet
    //We should use the latest version we know exists on Maven central for this
    //We only use this to get the URL for Github from the scm section
    Artifact latestArtifact = new DefaultArtifact(artifact.getGroupId(), artifact.getArtifactId(),
        null, latestVersion);

    String pomFileUrl = generatePomFileUrl(latestArtifact);

    String scmGithubUrl = getScmGithubUrl(pomFileUrl);

    String pomLocation;
    if (artifact.getArtifactId().equals("google-cloud-bigtable-bom")) {
      pomLocation = "/google-cloud-bigtable-deps-bom/pom.xml";
    } else {
      pomLocation = "/pom.xml";
    }
    String githubPomUrl = generateGithubPomUrl(scmGithubUrl, artifact.getVersion(), pomLocation);
    String sharedDependenciesVersion = getSharedDependenciesVersionFromUrl(githubPomUrl);
    return new ArtifactData(artifact, latestVersion, lastUpdated, pomFileUrl, metadataUrl,
        scmGithubUrl, githubPomUrl, sharedDependenciesVersion);
  }

  private static String generateGithubPomUrl(String scmGithubUrl, String version,
      String pomLocation) {
    if (scmGithubUrl == null) {
      return null;
    }
    String rawUrl = scmGithubUrl.replace("github.com", "raw.githubusercontent.com");
    String updatedVersion;
    if (version.contains("-")) {
      updatedVersion = version.substring(0, version.indexOf('-'));
    } else {
      updatedVersion = version;
    }
    return rawUrl + "/v" + updatedVersion + pomLocation;
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

  /**
   * Grabs the Github URL from the POM's scm section. If the URL is not found, then this checks the
   * associated parent POM (given that the current POM is not already the parent of our artifact)
   *
   * @param pomUrl the Maven URL of the artifact's POM file
   * @return Artifact's Github URL
   */
  private static String getScmGithubUrl(String pomUrl) {
    try {
      File pomFile = File.createTempFile("pomFile", ".xml");
      pomFile.deleteOnExit();
      URL url = new URL(pomUrl);
      BufferedInputStream input = new BufferedInputStream(url.openStream());
      FileUtils.copyInputStreamToFile(input, pomFile);
      MavenXpp3Reader read = new MavenXpp3Reader();
      Model model = read.read(new FileInputStream(pomFile));
      if (model == null) {
        return null;
      }
      return model.getUrl();
    } catch (XmlPullParserException | IOException ignored) {
    }
    return null;
  }

  private static String generatePomFileUrl(Artifact artifact) {
    String groupPath = artifact.getGroupId().replace('.', '/');
    return basePath + "/" + groupPath
        + "/" + artifact.getArtifactId()
        + "/" + artifact.getVersion()
        + "/" + artifact.getArtifactId() + "-" + artifact.getVersion() + ".pom";
  }

  private static String generateMetadataUrl(Artifact artifact) {
    String groupPath = artifact.getGroupId().replace('.', '/');
    return basePath + "/" + groupPath
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

  private static String getSharedDependenciesVersionFromUrl(String pomUrl) {
    if (pomUrl == null) {
      return null;
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
          return dep.getVersion();
        }
      }
      return "";
    } catch (XmlPullParserException | IOException ignored) {
    }
    return null;
  }
}
