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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.cli.ParseException;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;

public class DependencyUpdateTest {

  private static final String updateDependency = "deps: update dependency com.google.cloud:google-cloud-";
  private static final String newRelease = "release-v";
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
      System.out.println("Commit message does not update dependencies. Returning success");
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
      // Should be of the form ["groupId:artifactId", "to", "vX.X.X"]
      String[] items = dependencyStart.split(" ");
      // We already know the groupId
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
    // A release PR was found
    Arguments arguments = Arguments.readCommandLine("-f ../pom.xml");
    List<Artifact> managedDependencies = generate(arguments.getBomFile());
    for (Artifact artifact : managedDependencies) {
      ArtifactData data = ArtifactData.generateArtifactData(artifact);
      // Discovers if the POM was unfound, lacked shared-dependencies, had an old version,
      // or the latest version, and places it into its respective list.
      classify(data, latestSharedDependenciesVersion);
    }

    // Prints all data from each list
    for (int i = 0; i < librariesClassified.length; i++) {
      List<ArtifactData> clientLibraryList = (List<ArtifactData>) librariesClassified[i];
      if (clientLibraryList.isEmpty()) {
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
      InputStream commitHashInputStream = Runtime.getRuntime().exec("git rev-parse HEAD")
          .getInputStream();
      BufferedReader commitHashReader = new BufferedReader(
          new InputStreamReader(commitHashInputStream));
      String commitHash = commitHashReader.readLine();
      if (commitHash == null || commitHash.isEmpty()) {
        return null;
      }

      InputStream commitMessageInputStream = Runtime.getRuntime().exec("git log -1 " + commitHash)
          .getInputStream();
      BufferedReader commitMessageReader = new BufferedReader(
          new InputStreamReader(commitMessageInputStream));

      String commitMessage;
      do {
        commitMessage = commitMessageReader.readLine();
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
}