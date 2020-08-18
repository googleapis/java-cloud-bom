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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
  private static final Map<ArtifactData, ClientLibraryStatus> clientLibraries = new HashMap<>();

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
    if (latestSharedDependenciesVersion == null || latestSharedDependenciesVersion.isEmpty()) {
      System.out.println("Failed to find latest version of google-cloud-shared-dependencies");
      System.exit(1);
      return;
    }
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
      ArtifactData data = ArtifactData.generateArtifactData(dependencyArtifact);
      ClientLibraryStatus status = ClientLibraryStatus.getLibraryStatus(data, latestSharedDependenciesVersion);
      System.out.println(String.format(status.getOutputFormatter(), 1));
      if (status == ClientLibraryStatus.SUCCESSFUL) {
        System.exit(0);
      } else {
        System.exit(1);
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
      clientLibraries
          .put(data, ClientLibraryStatus.getLibraryStatus(data, latestSharedDependenciesVersion));
    }

    for (ClientLibraryStatus status : ClientLibraryStatus.values()) {
      // Grab all artifacts with this status
      Set<ArtifactData> statusArtifacts = clientLibraries.keySet().stream()
          .filter(c -> clientLibraries.get(c) == status).collect(Collectors.toSet());
      if (!statusArtifacts.isEmpty()) {
        System.out.println(
            "------------------------------------------------------------------------------------");
        System.out.println(String.format(status.getOutputFormatter(), statusArtifacts.size()));
        for (ArtifactData artifactData : statusArtifacts) {
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
    }

    long successfulCount = clientLibraries.keySet().stream()
        .filter(c -> clientLibraries.get(c) == ClientLibraryStatus.SUCCESSFUL).count();
    if (managedDependencies.size() > successfulCount) {
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