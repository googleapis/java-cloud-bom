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

package com.google.cloud.cloudbomcommitcheck;

import com.google.cloud.tools.opensource.dependencies.Bom;
import com.google.cloud.tools.opensource.dependencies.MavenRepositoryException;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
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

  private final Map<ArtifactData, ClientLibraryStatus> clientLibraries;
  private final String commitMessage;

  public static void main(String[] args) throws ParseException, MavenRepositoryException {
    if (args.length < 1) {
      System.out.println("Please pass a commit message to run this script.");
      System.exit(1);
      return;
    }
    RecentCommitCheck commitCheck = new RecentCommitCheck(args[0]);
    int exitCode = commitCheck.execute();
    commitCheck.printOutput(exitCode);
    System.exit(exitCode);
  }

  public RecentCommitCheck(String commitMessage) {
    this.commitMessage = commitMessage;
    this.clientLibraries = new HashMap<>();
  }

  public Map<ArtifactData, ClientLibraryStatus> getCurrentClientLibraries() {
    return new HashMap<>(clientLibraries);
  }

  /**
   * @return 2 for unable to find latest version of google-cloud-shared-dependencies
   */
  public int execute() throws ParseException, MavenRepositoryException {
    if (commitMessage == null || (!commitMessage.contains(updateDependency)
        && !commitMessage.contains(newRelease)) || commitMessage.contains(snapshot)) {
      System.out.println("Commit message does not update dependencies. Returning success.");
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

    // If it's expected to run this twice, clear old dependency data
    clientLibraries.clear();

    if (commitMessage.contains(updateDependency)) {
      String dependencyStart = commitMessage.substring(commitMessage.indexOf("com.google.cloud:"));
      // Should be of the form ["groupId:artifactId", "to", "vX.X.X"]
      String[] items = dependencyStart.split(" ");
      if (items.length != 3) {
        System.out.println("Commit message does not match basic dependency update formatting");
        return 0;
      }

      String[] groupAndArtifact = items[0].split(":");
      if (groupAndArtifact.length != 2) {
        System.out.println(
            "Dependency update found in commit message does not contain valid group ID /artifact ID");
        return 0;
      }
      String groupId = groupAndArtifact[0];
      if (!"com.google.cloud".equals(groupId)) {
        System.out.println(
            "Dependency update found in commit message does not contain group ID com.google.cloud");
        return 0;
      }
      String artifactId = groupAndArtifact[1];
      String version = items[2];

      if (!version.startsWith("v")) {
        System.out
            .println("Dependency update found in commit message does not contain a valid version");
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

  private void printOutput(int exitCode) {
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
        System.out.println("Invalid dependencies found");
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