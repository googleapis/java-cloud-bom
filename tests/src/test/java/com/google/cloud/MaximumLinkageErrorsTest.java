/*
 * Copyright 2019 Google LLC.
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

package com.google.cloud;

import com.google.cloud.tools.opensource.classpath.ClassPathBuilder;
import com.google.cloud.tools.opensource.classpath.ClassPathEntry;
import com.google.cloud.tools.opensource.classpath.ClassPathResult;
import com.google.cloud.tools.opensource.classpath.DependencyMediation;
import com.google.cloud.tools.opensource.classpath.LinkageChecker;
import com.google.cloud.tools.opensource.classpath.LinkageProblem;
import com.google.cloud.tools.opensource.dependencies.Bom;
import com.google.cloud.tools.opensource.dependencies.MavenRepositoryException;
import com.google.cloud.tools.opensource.dependencies.RepositoryUtility;
import com.google.cloud.tools.opensource.dependencies.UnresolvableArtifactProblem;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.version.InvalidVersionSpecificationException;
import org.junit.Assert;
import org.junit.Test;

public class MaximumLinkageErrorsTest {

  @Test
  public void testForNewLinkageErrors()
      throws IOException, MavenRepositoryException, RepositoryException {
    // Not using RepositoryUtility.findLatestCoordinates, which may return a snapshot version
    String version = findLatestNonSnapshotVersion();
    String baselineCoordinates = "com.google.cloud:libraries-bom:" + version;
    Bom baseline = Bom.readBom(baselineCoordinates);

    Path bomFile = Paths.get("../libraries-bom/pom.xml");
    Bom bom = Bom.readBom(bomFile);

    ImmutableSet<LinkageProblem> oldProblems = createLinkageChecker(baseline).findLinkageProblems();
    LinkageChecker checker = createLinkageChecker(bom);
    ImmutableSet<LinkageProblem> currentProblems = checker.findLinkageProblems();

    // This only tests for newly missing methods, not new invocations of
    // previously missing methods.
    Set<LinkageProblem> newProblems = Sets.difference(currentProblems, oldProblems);

    // Appengine-api-1.0-sdk is known to contain linkage errors because it shades dependencies
    // https://github.com/GoogleCloudPlatform/cloud-opensource-java/issues/441
    newProblems =
        newProblems.stream()
            .filter(problem -> !hasLinkageProblemFromArtifactId(problem, "appengine-api-1.0-sdk"))
            .collect(Collectors.toSet());

    // Check that no new linkage errors have been introduced since the baseline
    StringBuilder message = new StringBuilder("Baseline BOM: " + baselineCoordinates + "\n");
    if (!newProblems.isEmpty()) {
      message.append("Newly introduced problems:\n");
      message.append(LinkageProblem.formatLinkageProblems(newProblems, null));
      Assert.fail(message.toString());
    }
  }

  private LinkageChecker createLinkageChecker(Bom bom)
      throws InvalidVersionSpecificationException, IOException {
    ImmutableList<Artifact> managedDependencies = bom.getManagedDependencies();
    ClassPathBuilder classPathBuilder = new ClassPathBuilder();

    // full: false to avoid fetching optional dependencies.
    ClassPathResult classPathResult =
        classPathBuilder.resolve(managedDependencies, false, DependencyMediation.MAVEN);
    ImmutableList<ClassPathEntry> classpath = classPathResult.getClassPath();
    ImmutableList<UnresolvableArtifactProblem> artifactProblems =
        classPathResult.getArtifactProblems();
    if (!artifactProblems.isEmpty()) {
      throw new IOException("Could not resolve artifacts: " + artifactProblems);
    }
    List<ClassPathEntry> artifactsInBom = classpath.subList(0, managedDependencies.size());
    ImmutableSet<ClassPathEntry> entryPoints = ImmutableSet.copyOf(artifactsInBom);
    return LinkageChecker.create(classpath, entryPoints, null);
  }

  private boolean hasLinkageProblemFromArtifactId(LinkageProblem problem, String artifactId) {
    ClassPathEntry sourceClassPathEntry = problem.getSourceClass().getClassPathEntry();
    Artifact sourceArtifact = sourceClassPathEntry.getArtifact();
    return artifactId.equals(sourceArtifact.getArtifactId());
  }

  private String findLatestNonSnapshotVersion() throws MavenRepositoryException {
    ImmutableList<String> versions =
        RepositoryUtility.findVersions(
            RepositoryUtility.newRepositorySystem(), "com.google.cloud", "libraries-bom");
    ImmutableList<String> versionsLatestFirst = versions.reverse();
    Optional<String> highestNonsnapshotVersion =
        versionsLatestFirst.stream().filter(version -> !version.contains("SNAPSHOT")).findFirst();
    if (!highestNonsnapshotVersion.isPresent()) {
      Assert.fail("Could not find non-snapshot version of the BOM");
    }
    return highestNonsnapshotVersion.get();
  }
}
