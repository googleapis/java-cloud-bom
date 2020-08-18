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

import com.google.cloud.tools.opensource.dependencies.Bom;
import com.google.cloud.tools.opensource.dependencies.MavenRepositoryException;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;

public class DependencyUpdateTest {

  private static final String updateDependency = "deps: update dependency com.google.cloud:google-cloud-";
  private static final String newRelease = "release-v";

  private static final String basePath = "https://repo1.maven.org/maven2";
  /**
   * These are the only four possibilities for any given client library A successful client library
   * has the latest version of google-cloud-shared-dependencies. An unsuccessful library may not
   * list google-cloud-shared-dependencies, have a version less than the latest, or have a POM that
   * can't be found.
   */
  private static final List<ArtifactData> successfulClientLibraries = new ArrayList<>();
  private static final List<ArtifactData> librariesWithoutSharedDeps = new ArrayList<>();
  private static final List<ArtifactData> librariesWithBadSharedDepsVersion = new ArrayList<>();
  private static final List<ArtifactData> unfindableClientLibraries = new ArrayList<>();

  private static final List[] librariesClassified = new List[]{
      successfulClientLibraries, librariesWithoutSharedDeps,
      librariesWithBadSharedDepsVersion, unfindableClientLibraries
  };

  private static final String[] outputStatements = {
      "SUCCESS - The following %s libraries had the latest version of google-cloud-shared-dependencies: ",
      "FAIL - The following %s libraries did not contain any version of google-cloud-shared-dependencies: ",
      "FAIL - The following %s libraries had outdated versions of google-cloud-shared-dependencies: ",
      "FAIL - The following %s libraries had unfindable POM files: "
  };

  public static void main(String[] args) throws ParseException, MavenRepositoryException {
    String latestCommitMessage = getLatestCommitMessage();
    if (latestCommitMessage == null) {
      System.out.println("Commit message does not update dependencies! Returning success!");
      System.exit(0);
      return;
    }

    DefaultArtifact latestSharedDependencies = new DefaultArtifact("com.google.cloud",
        "google-cloud-shared-dependencies", null, null);
    ArtifactData sharedDependenciesData = ArtifactData
        .generateArtifactData(latestSharedDependencies);
    String latestSharedDependenciesVersion = sharedDependenciesData.getLatestVersion();
    System.out.println("The latest version of google-cloud-shared-dependencies is "
        + latestSharedDependenciesVersion);

    if (latestCommitMessage.contains(updateDependency)) {
      String dependencyStart = latestCommitMessage
          .substring(latestCommitMessage.indexOf("com.google.cloud:"));
      //Should be of the form ["groupId:artifactId", "to", "vX.X.X"]
      String[] items = dependencyStart.split(" ");
      //We already know the groupId
      String groupId = "com.google.cloud";
      String artifactId = items[0].split("")[1];
      String version = items[2].substring(1);

      Artifact dependencyArtifact = new DefaultArtifact(groupId, artifactId, null, version);
      String sharedDependenciesVersion = ArtifactData.generateArtifactData(dependencyArtifact)
          .getSharedDependenciesVersion();
      if (sharedDependenciesVersion == null) {
        System.out.println("Update " + dependencyStart + " failed due to an unfindable POM!");
        System.exit(1);
      } else if (sharedDependenciesVersion.isEmpty()) {
        System.out.println("Update " + dependencyStart
            + " failed due to lacking google-cloud-shared-dependencies");
        System.exit(1);
      } else if (sharedDependenciesVersion.equals(latestSharedDependenciesVersion)) {
        System.out.println("Update " + dependencyStart
            + " succeeded with google-cloud-shared dependencies version "
            + sharedDependenciesVersion);
        System.exit(0);
      } else {
        System.out.println(
            "Update " + dependencyStart + " failed with google-cloud-shared dependencies version "
                + sharedDependenciesVersion);
        System.exit(0);
      }
      return;
    }
    //A release PR was found
    Arguments arguments = Arguments.readCommandLine("-f ../pom.xml");
    List<Artifact> managedDependencies = generate(arguments.getBomFile());
    for (Artifact artifact : managedDependencies) {
      ArtifactData data = ArtifactData.generateArtifactData(artifact);
      //Discovers if the POM was unfound, lacked shared-dependencies, had an old version,
      //or the latest version, and places it into its respective list.
      classify(data, latestSharedDependenciesVersion);
    }

    //Prints all data from each list
    for (int i = 0; i < librariesClassified.length; i++) {
      List<ArtifactData> clientLibraryList = (List<ArtifactData>) librariesClassified[i];
      if (clientLibraryList.size() <= 0) {
        continue;
      }
      String output = outputStatements[i];
      System.out.println(String.format(output, clientLibraryList.size()));
      for (ArtifactData artifactData : clientLibraryList) {
        Artifact artifact = artifactData.getArtifact();
        System.out.print(artifact.getArtifactId() + ":" + artifact.getVersion());
        String sharedDependenciesVersion = artifactData.getSharedDependenciesVersion();
        if (sharedDependenciesVersion == null || sharedDependenciesVersion.isEmpty()) {
          sharedDependenciesVersion = "";
        } else {
          sharedDependenciesVersion = " Version Found: " + sharedDependenciesVersion;
        }
        System.out.println(sharedDependenciesVersion);
      }
      System.out.println(
          "------------------------------------------------------------------------------------");
    }

    if (managedDependencies.size() > successfulClientLibraries.size()) {
      System.out.println("Total dependencies checked: " + managedDependencies.size());
      System.exit(1);
      return;
    }
    System.out.println("Total dependencies checked: " + managedDependencies.size());
    System.out.println("All found libraries were successful!");
    System.exit(0);
  }

  private static String getLatestCommitMessage() {
    try {
      InputStream inputStream = Runtime.getRuntime().exec("git rev-parse HEAD").getInputStream();
      BufferedReader stdInput = new BufferedReader(new InputStreamReader(inputStream));
      String commitHash = stdInput.readLine();
      if (commitHash == null || commitHash.isEmpty()) {
        return null;
      }

      inputStream = Runtime.getRuntime().exec("git log -1 " + commitHash).getInputStream();
      stdInput = new BufferedReader(new InputStreamReader(inputStream));

      String commitMessage;
      do {
        commitMessage = stdInput.readLine();
      } while (commitMessage != null && !commitMessage.contains(updateDependency)
          && !commitMessage.contains(newRelease));
      return commitMessage;
    } catch (IOException ignored) {
    }
    return null;
  }

  /**
   * Classifies our artifact data into the four possible outcomes for output - (1) Client library
   * POM not found (2) Client library does not have shared-dependencies (3) Client library has old
   * shared-dependencies version (4) Client library has newest shared-dependencies version
   */
  private static void classify(ArtifactData artifactData, String latestSharedDependenciesVersion) {
    String artifactDependenciesVersion = artifactData.getSharedDependenciesVersion();
    if (artifactDependenciesVersion == null) {
      unfindableClientLibraries.add(artifactData);
      return;
    }
    if (artifactDependenciesVersion.isEmpty()) {
      librariesWithoutSharedDeps.add(artifactData);
    } else if (artifactDependenciesVersion.equals(latestSharedDependenciesVersion)) {
      successfulClientLibraries.add(artifactData);
    } else {
      librariesWithBadSharedDepsVersion.add(artifactData);
    }
  }

  @VisibleForTesting
  static List<Artifact> generate(Path bomFile) throws MavenRepositoryException {
    Preconditions.checkArgument(Files.isRegularFile(bomFile, new LinkOption[0]),
        "The input BOM %s is not a regular file", bomFile);
    Preconditions
        .checkArgument(Files.isReadable(bomFile), "The input BOM %s is not readable", bomFile);
    return generate(Bom.readBom(bomFile));
  }

  private static List<Artifact> generate(Bom bom) {
    List<Artifact> managedDependencies = new ArrayList(bom.getManagedDependencies());
    managedDependencies.removeIf((a) -> {
      return a.getArtifactId().contains("google-cloud-core")
          || a.getArtifactId().contains("bigtable-emulator")
          || !"com.google.cloud".equals(a.getGroupId());
    });
    return managedDependencies;
  }

  private static String sharedDependencyVersion(boolean useParentPom, Artifact artifact) {
    String groupId = artifact.getGroupId();
    String artifactId = artifact.getArtifactId();
    String version = getLatestVersion(groupId, artifactId);
    String pomURL = useParentPom ? getParentPomFileURL(groupId, artifactId, version) :
        getPomFileURL(groupId, artifactId, version);
    String pomLocation = "/pom.xml";
    File file = new File("pomFile.xml");
    String repoURL = null;
    try {
      URL url = new URL(pomURL);
      FileUtils.copyURLToFile(url, file);
      MavenXpp3Reader read = new MavenXpp3Reader();
      Model model = read.read(new FileReader(file));
      if (model.getScm() == null || model.getScm().getUrl() == null) {
        System.out.println("Unable to find scm section for: " + artifact);
        if (model.getDependencyManagement() == null) {
          return "";
        }
        Iterator<Dependency> iter = model.getDependencyManagement().getDependencies().iterator();
        while (iter.hasNext()) {
          Dependency dep = iter.next();
          if ("com.google.cloud".equals(dep.getGroupId()) && "google-cloud-shared-dependencies"
              .equals(dep.getArtifactId())) {
            return dep.getVersion();
          }
        }
        if (useParentPom) {
          return sharedDependencyVersion(false, artifact);
        }
        return "";
      }

      repoURL = model.getScm().getUrl();
      String gitPomURL = repoURL.replace("github.com", "raw.githubusercontent.com");
      gitPomURL += ("/v" + version + pomLocation);
      ;

      url = new URL(gitPomURL);
      FileUtils.copyURLToFile(url, file);

      read = new MavenXpp3Reader();
      model = read.read(new FileReader(file));

      if (model.getDependencyManagement() == null) {
        if (useParentPom) {
          return sharedDependencyVersion(false, artifact);
        }
        return "";
      }

      Iterator<Dependency> iter = model.getDependencyManagement().getDependencies().iterator();
      while (iter.hasNext()) {
        Dependency dep = iter.next();
        if ("com.google.cloud".equals(dep.getGroupId()) && "google-cloud-shared-dependencies"
            .equals(dep.getArtifactId())) {
          return dep.getVersion();
        }
      }
    } catch (XmlPullParserException | IOException ignored) {
      System.out.println("Artifact: " + artifactId + ". Original repo URL: " + repoURL);
      System.out.println("Secondary Repo URL: " + pomURL);
    }
    if (useParentPom) {
      return sharedDependencyVersion(false, artifact);
    }
    return null;
  }

  private static String getLatestVersion(String groupId, String artifactId) {
    String pomPath = getMetaDataURL(groupId, artifactId);

    try {
      URL url = new URL(pomPath);
      URLConnection conn = url.openConnection();
      conn.setConnectTimeout(2000);
      conn.setReadTimeout(2000);
      Scanner s = new Scanner(conn.getInputStream());

      while (s.hasNextLine()) {
        String string = s.nextLine();
        if (string.contains("<latest>")) {
          String version = string.split(">")[1].split("<")[0];
          return version;
        }
      }
    } catch (IOException var8) {
      var8.printStackTrace();
    }

    return null;
  }

  private static String getMetaDataURL(String groupId, String artifactId) {
    String groupPath = groupId.replace('.', '/');
    return basePath + "/" + groupPath
        + "/" + artifactId
        + "/maven-metadata.xml";
  }

  private static String getParentPomFileURL(String groupId, String artifactId, String version) {
    artifactId += "-parent";
    String groupPath = groupId.replace('.', '/');
    return basePath + "/" + groupPath
        + "/" + artifactId
        + "/" + version
        + "/" + artifactId + "-" + version + ".pom";
  }

  private static String getPomFileURL(String groupId, String artifactId, String version) {
    String groupPath = groupId.replace('.', '/');
    return basePath + "/" + groupPath
        + "/" + artifactId
        + "/" + version
        + "/" + artifactId + "-" + version + ".pom";
  }
}