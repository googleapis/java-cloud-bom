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
import org.apache.commons.cli.ParseException;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;

public class RecentCommitCheck {

  /**
   * Identifiers for commits with new releases, or updated dependency versions
   */
  private static final String updateDependency = "deps: update dependency com.google.cloud:google-cloud-";
  private static final String newRelease = "chore: release";
  private static final String snapshot = "SNAPSHOT";

  private static final Map<ArtifactData, ClientLibraryStatus> clientLibraries = new HashMap<>();

  public static void main(String[] args) throws ParseException, MavenRepositoryException {
    String latestCommitMessage = getLatestCommitMessage();
    int exitCode = execute(latestCommitMessage);
    printOutput(exitCode);
    System.exit(exitCode);
  }

  public static Map<ArtifactData, ClientLibraryStatus> getCurrentClientLibraries() {
    return new HashMap<>(clientLibraries);
  }

  /**
   * @param commitMessage most recent commit message from Git
   * @return program exit code - 0 for success, 1 for invalid dependencies, 2 for unable to find
   * latest version of google-cloud-shared-dependencies
   */
  public static int execute(String commitMessage) throws ParseException, MavenRepositoryException {
    if (commitMessage == null || (!commitMessage.contains(updateDependency)
      && !commitMessage.contains(newRelease)) || commitMessage.contains(snapshot)) {
      System.out.println("Commit message does not update dependencies. Returning success");
      return 0;
    }

    DefaultArtifact latestSharedDependencies = new DefaultArtifact("com.google.cloud",
        "google-cloud-shared-dependencies", null, null);
    ArtifactData sharedDependenciesData = ArtifactData
        .generateArtifactData(latestSharedDependencies);
    String latestSharedDependenciesVersion = sharedDependenciesData.getLatestVersion();
    if (latestSharedDependenciesVersion == null || latestSharedDependenciesVersion.isEmpty()) {
      return 2;
    }

    if (commitMessage.contains(updateDependency)) {
      String dependencyStart = commitMessage.substring(commitMessage.indexOf("com.google.cloud:"));
      // Should be of the form ["groupId:artifactId", "to", "vX.X.X"]
      String[] items = dependencyStart.split(" ");
      if (items.length != 3) {
        System.out.println("Commit message does not update dependencies. Returning success");
        return 0;
      }
      // We already know the groupId
      String[] groupAndArtifact = items[0].split(":");
      if (groupAndArtifact.length != 2) {
        System.out.println("Commit message does not update dependencies. Returning success");
        return 0;
      }
      String groupId = groupAndArtifact[0];
      String artifactId = groupAndArtifact[1];
      String version = items[2];

      if(!version.startsWith("v")) {
        System.out.println("Commit message does not update dependencies. Returning success");
        return 0;
      }

      version = version.substring(1);

      Artifact dependencyArtifact = new DefaultArtifact(groupId, artifactId, null, version);
      ArtifactData data = ArtifactData.generateArtifactData(dependencyArtifact);
      ClientLibraryStatus status = ClientLibraryStatus
          .getLibraryStatus(data, latestSharedDependenciesVersion);
      clientLibraries.put(data, status);
      if (status == ClientLibraryStatus.SUCCESSFUL) {
        return 0;
      }
      return 1;
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

    long successfulCount = clientLibraries.keySet().stream()
        .filter(c -> clientLibraries.get(c) == ClientLibraryStatus.SUCCESSFUL).count();
    if (managedDependencies.size() > successfulCount) {
      return 1;
    }
    return 0;
  }

  private static void printOutput(int exitCode) {
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

    switch (exitCode) {
      case 0:
        System.out.println("All found libraries were successful");
        break;
      case 1:
        System.out.println("Invalid dependencies found!");
        break;
      case 2:
        System.out.println("Failed to find latest version of google-cloud-shared-dependencies");
        break;
      default:
        System.out.println("Unknown exit code found");
        break;
    }
    System.out.println("Total dependencies checked: " + clientLibraries.size());
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