/*
 * Copyright 2018 Google LLC.
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

import static com.google.common.base.Preconditions.checkArgument;

import com.google.cloud.tools.opensource.dependencies.Bom;
import com.google.cloud.tools.opensource.dependencies.DependencyGraph;
import com.google.cloud.tools.opensource.dependencies.DependencyGraphBuilder;
import com.google.cloud.tools.opensource.dependencies.MavenRepositoryException;
import com.google.cloud.tools.opensource.dependencies.RepositoryUtility;
import com.google.cloud.tools.opensource.dependencies.Update;
import com.google.cloud.tools.opensource.dependencies.VersionComparator;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.Version;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;

public class DashboardMain {

  public static final String basePath = "https://repo1.maven.org/maven2";
  public static final String TEST_NAME_UPPER_BOUND = "Upper Bounds";
  public static final String TEST_NAME_DEPENDENCY_CONVERGENCE = "Dependency Convergence";

  private static final Configuration freemarkerConfiguration = configureFreemarker();

  private static final DependencyGraphBuilder dependencyGraphBuilder = new DependencyGraphBuilder();

  private static final List<String> bomVersions = new ArrayList<>();

  /**
   * Generates a code hygiene dashboard for a BOM. This tool takes a path to pom.xml of the BOM as
   * an argument or Maven coordinates to a BOM.
   *
   * <p>Generated dashboard is at {@code target/$groupId/$artifactId/$version/index.html}, where
   * each value is from BOM coordinates except {@code $version} is "snapshot" if the BOM has
   * snapshot version.
   */
  public static void main(String[] arguments)
      throws IOException, TemplateException, RepositoryException, URISyntaxException,
      ParseException, MavenRepositoryException {
    DashboardArguments dashboardArguments = DashboardArguments.readCommandLine(arguments);

    if (dashboardArguments.hasVersionlessCoordinates()) {
      generateAllVersions(dashboardArguments.getVersionlessCoordinates());
    } else if (dashboardArguments.hasFile()) {
      generate(dashboardArguments.getBomFile());
    } else {
      generate(dashboardArguments.getBomCoordinates());
    }
  }

  private static void generateAllVersions(String versionlessCoordinates)
      throws IOException, TemplateException, RepositoryException, URISyntaxException,
          MavenRepositoryException {
    List<String> elements = Splitter.on(':').splitToList(versionlessCoordinates);
    checkArgument(
        elements.size() == 2,
        "The versionless coordinates should have one colon character: " + versionlessCoordinates);
    String groupId = elements.get(0);
    String artifactId = elements.get(1);

    RepositorySystem repositorySystem = RepositoryUtility.newRepositorySystem();
    ImmutableList<String> versions =
        RepositoryUtility.findVersions(repositorySystem, groupId, artifactId);
    for (String version : versions) {
      if (version.contains("alpha")) continue;
      bomVersions.add(version);
    }
    for (String version : bomVersions) {
      generate(String.format("%s:%s:%s", groupId, artifactId, version));
    }
    //generateVersionIndex(groupId, artifactId, versions);
  }

  @VisibleForTesting
  static Path generate(String bomCoordinates)
      throws IOException, TemplateException, RepositoryException, URISyntaxException {
    Path output = generate(Bom.readBom(bomCoordinates));
    System.out.println("Wrote dashboard for " + bomCoordinates + " to " + output);
    return output;
  }

  @VisibleForTesting
  static Path generate(Path bomFile)
      throws IOException, TemplateException, URISyntaxException, MavenRepositoryException {
    checkArgument(Files.isRegularFile(bomFile), "The input BOM %s is not a regular file", bomFile);
    checkArgument(Files.isReadable(bomFile), "The input BOM %s is not readable", bomFile);
    Path output = generate(Bom.readBom(bomFile));

    System.out.println("Wrote dashboard for " + bomFile + " to " + output);
    return output;
  }

  private static Path generate(Bom bom) throws IOException, TemplateException, URISyntaxException {
    List<Artifact> managedDependencies = new ArrayList<>();
    for (Artifact artifact : bom.getManagedDependencies()) {
      if ("com.google.cloud".equals(artifact.getGroupId())
              && !artifact.getArtifactId().contains("google-cloud-core")) {
        managedDependencies.add(artifact);
      }
    }

    ArtifactCache cache = loadArtifactInfo(managedDependencies);
    Path output = generateHtml(bom, cache);

    return output;
  }

  private static Path outputDirectory(String groupId, String artifactId, String version) {
    String versionPathElement = version.contains("-SNAPSHOT") ? "snapshot" : version;
    return Paths.get("target", groupId, artifactId, versionPathElement);
  }

  private static Path generateHtml(
      Bom bom,
      ArtifactCache cache
      )
      throws IOException, TemplateException, URISyntaxException {

    Artifact bomArtifact = new DefaultArtifact(bom.getCoordinates());

    Path relativePath =
        outputDirectory(
            bomArtifact.getGroupId(), bomArtifact.getArtifactId(), bomArtifact.getVersion());
    Path output = Files.createDirectories(relativePath);

    copyResource(output, "css/dashboard.css");
    copyResource(output, "js/dashboard.js");

    List<ArtifactResults> table = generateReports(cache);
    generateDashboard(output, table, cache, bom);

    return output;
  }

  private static void copyResource(Path output, String resourceName)
      throws IOException, URISyntaxException {
    ClassLoader classLoader = DashboardMain.class.getClassLoader();
    Path input = Paths.get(Objects.requireNonNull(classLoader.getResource(resourceName)).toURI()).toAbsolutePath();
    Path copy = output.resolve(input.getFileName());
    if (!Files.exists(copy)) {
      Files.copy(input, copy);
    }
  }

  @VisibleForTesting
  static Configuration configureFreemarker() {
    Configuration configuration = new Configuration(new Version("2.3.28"));
    configuration.setDefaultEncoding("UTF-8");
    configuration.setClassForTemplateLoading(DashboardMain.class, "/");
    return configuration;
  }

  @VisibleForTesting
  static List<ArtifactResults> generateReports(ArtifactCache cache) {

    Map<Artifact, ArtifactInfo> artifacts = cache.getInfoMap();
    List<ArtifactResults> table = new ArrayList<>();
    for (Entry<Artifact, ArtifactInfo> entry : artifacts.entrySet()) {
      ArtifactInfo info = entry.getValue();
      if (info.getException() != null) {
        ArtifactResults unavailable = new ArtifactResults(entry.getKey());
        unavailable.setExceptionMessage(info.getException().getMessage());
        table.add(unavailable);
      } else {
        Artifact artifact = entry.getKey();
        ArtifactResults results =
            generateArtifactReport(
                artifact,
                entry.getValue());
        table.add(results);
      }
    }
    return table;
  }

  /**
   * This is the only method that queries the Maven repository.
   */
  private static ArtifactCache loadArtifactInfo(List<Artifact> artifacts) {
    Map<Artifact, ArtifactInfo> infoMap = new LinkedHashMap<>();
    List<DependencyGraph> globalDependencies = new ArrayList<>();

    for (Artifact artifact : artifacts) {
      DependencyGraph completeDependencies =
          dependencyGraphBuilder.buildFullDependencyGraph(ImmutableList.of(artifact));
      globalDependencies.add(completeDependencies);

      // picks versions according to Maven rules
      DependencyGraph transitiveDependencies =
          dependencyGraphBuilder.buildMavenDependencyGraph(new Dependency(artifact, "compile"));

      ArtifactInfo info = new ArtifactInfo(completeDependencies, transitiveDependencies);
      infoMap.put(artifact, info);
    }

    ArtifactCache cache = new ArtifactCache();
    cache.setInfoMap(infoMap);
    cache.setGlobalDependencies(globalDependencies);

    return cache;
  }

  private static ArtifactResults generateArtifactReport(
      Artifact artifact,
      ArtifactInfo artifactInfo)
  {
    // includes all versions
    DependencyGraph graph = artifactInfo.getCompleteDependencies();
    List<Update> convergenceIssues = graph.findUpdates();

    // picks versions according to Maven rules
    DependencyGraph transitiveDependencies = artifactInfo.getTransitiveDependencies();

    Map<Artifact, Artifact> upperBoundFailures =
        findUpperBoundsFailures(graph.getHighestVersionMap(), transitiveDependencies);
    ArtifactResults results = new ArtifactResults(artifact);
    results.addResult(TEST_NAME_UPPER_BOUND, upperBoundFailures.size());
    results.addResult(TEST_NAME_DEPENDENCY_CONVERGENCE, convergenceIssues.size());
    return results;
  }

  private static Map<Artifact, Artifact> findUpperBoundsFailures(
      Map<String, String> expectedVersionMap,
      DependencyGraph transitiveDependencies) {

    Map<String, String> actualVersionMap = transitiveDependencies.getHighestVersionMap();

    VersionComparator comparator = new VersionComparator();

    Map<Artifact, Artifact> upperBoundFailures = new LinkedHashMap<>();

    for (String id : expectedVersionMap.keySet()) {
      String expectedVersion = expectedVersionMap.get(id);
      String actualVersion = actualVersionMap.get(id);
      // Check that the actual version is not null because it is
      // possible for dependencies to appear or disappear from the tree
      // depending on which version of another dependency is loaded.
      // In both cases, no action is needed.
      if (actualVersion != null && comparator.compare(actualVersion, expectedVersion) < 0) {
        // Maven did not choose highest version
        DefaultArtifact lower = new DefaultArtifact(id + ":" + actualVersion);
        DefaultArtifact upper = new DefaultArtifact(id + ":" + expectedVersion);
        upperBoundFailures.put(lower, upper);
      }
    }
    return upperBoundFailures;
  }


  @VisibleForTesting
  static void generateDashboard(
          Path output,
          List<ArtifactResults> table,
          ArtifactCache cache,
          Bom bom)
          throws IOException, TemplateException {
    TreeSet<String> artifacts = new TreeSet<>();
    Map<String, String> currentVersion = new HashMap<>();
    Map<String, String> sharedDepsPosition = new HashMap<>();
    Map<String, String> newestVersion = new HashMap<>();
    Map<String, String> newestPomURL = new HashMap<>();
    Map<String, String> sharedDepsVersion = new HashMap<>();
    Map<String, String> updatedTime = new HashMap<>();
    Map<String, String> metadataURL = new HashMap<>();
    Map<Artifact, ArtifactInfo> infoMap = cache.getInfoMap();
    for (Map.Entry<Artifact, ArtifactInfo> info : infoMap.entrySet()) {
      String artifactId = info.getKey().getArtifactId();
      String groupId = info.getKey().getGroupId();
      String version = info.getKey().getVersion();
      artifacts.add(artifactId);
      currentVersion.put(artifactId,version);
      newestVersion.put(artifactId, latestVersion(info.getKey()));
      newestPomURL.put(artifactId, getPomFileURL(groupId, artifactId, newestVersion.get(artifactId)));
      sharedDepsVersion.put(artifactId, sharedDependencyVersion(info.getKey(), sharedDepsPosition));
      updatedTime.put(artifactId, updatedTime(info.getKey()));
      metadataURL.put(artifactId, getMetadataURL(info.getKey()));
    }

    Map<String, Object> templateData = new HashMap<>();
    templateData.put("table", table);
    templateData.put("lastUpdated", LocalDateTime.now());
    templateData.put("currentVersion", currentVersion);
    templateData.put("sharedDepsPosition", sharedDepsPosition);
    templateData.put("newestVersion", newestVersion);
    templateData.put("newestPomURL", newestPomURL);
    templateData.put("sharedDepsVersion", sharedDepsVersion);
    templateData.put("updatedTime", updatedTime);
    templateData.put("metadataURL", metadataURL);
    templateData.put("artifacts", artifacts);
    templateData.put("coordinates", bom.getCoordinates());
    templateData.put("dependencyGraphs", cache.getGlobalDependencies());

    // Accessing static methods from Freemarker template
    // https://freemarker.apache.org/docs/pgui_misc_beanwrapper.html#autoid_60
    DefaultObjectWrapper wrapper = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_28)
        .build();
    TemplateHashModel staticModels = wrapper.getStaticModels();
    templateData.put("dashboardMain", staticModels.get(DashboardMain.class.getName()));
    templateData.put("bomVersions", bomVersions);
    File dashboardFile = output.resolve("index.html").toFile();
    try (Writer out = new OutputStreamWriter(
        new FileOutputStream(dashboardFile), StandardCharsets.UTF_8)) {
      Template dashboard = DashboardMain.freemarkerConfiguration.getTemplate("/templates/index.ftl");
      dashboard.process(templateData, out);
    }
  }

  private static String latestVersion(Artifact artifact) {
    String pomPath = getMetadataURL(artifact);
    try {
      URL url = new URL(pomPath);
      Scanner s = new Scanner(url.openStream());
      while (s.hasNextLine()) {
        String string = s.nextLine();
        if (string.contains("<latest>")) {
          return string.split(">")[1].split("<")[0];
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "";
  }

  private static String updatedTime(Artifact artifact) {
    String groupPath = artifact.getGroupId().replace('.', '/');
    String metadataPath = basePath + "/" + groupPath
            + "/" + artifact.getArtifactId()
            + "/maven-metadata.xml";
    try {
      URL url = new URL(metadataPath);
      Scanner s = new Scanner(url.openStream());
      while (s.hasNextLine()) {
        String string = s.nextLine();
        if (string.contains("<lastUpdated>")) {
          DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
          DateFormat outputFormat = new SimpleDateFormat("MM-dd-yyyy");
          String input =  string.split(">")[1].split("<")[0];
          Date date= dateFormat.parse(input);
          return outputFormat.format(date);
        }
      }
    } catch (IOException | java.text.ParseException e) {
      e.printStackTrace();
    }
    return "";
  }

  /**
   * Returns the number of rows in {@code table} that show unavailable ({@code null} result) or some
   * failures for {@code columnName}.
   */
  public static long countFailures(List<ArtifactResults> table, String columnName) {
    return table.stream()
        .filter(row -> row.getResult(columnName) == null || row.getFailureCount(columnName) > 0)
        .count();
  }

  private static String sharedDependencyVersion(Artifact artifact, Map<String, String> sharedDepsPosition) {
    String groupPath = artifact.getGroupId().replace('.', '/');
    String pomPath = getPomFileURL(artifact.getGroupId(), artifact.getArtifactId(),artifact.getVersion());
    String parentPath = basePath + "/" + groupPath
            + "/" + artifact.getArtifactId() + "-parent"
            + "/" + artifact.getVersion()
            + "/" + artifact.getArtifactId() + "-parent-" + artifact.getVersion() + ".pom";
    String depsBomPath = basePath + "/" + groupPath
            + "/" + artifact.getArtifactId() + "-deps-bom"
            + "/" + artifact.getVersion()
            + "/" + artifact.getArtifactId() + "-deps-bom-" + artifact.getVersion() + ".pom";
    String version = getSharedDepsVersionFromURL(parentPath);
    if (version != null) {
      sharedDepsPosition.put(artifact.getArtifactId(), parentPath);
      return version;
    }
    version = getSharedDepsVersionFromURL(pomPath);
    if (version != null) {
      sharedDepsPosition.put(artifact.getArtifactId(), pomPath);
      return version;
    }
    version = getSharedDepsVersionFromURL(depsBomPath);
    if (version != null) {
      sharedDepsPosition.put(artifact.getArtifactId(), depsBomPath);
      return version;
    }
    sharedDepsPosition.put(artifact.getArtifactId(), "");
    return "";
  }
  private static String getSharedDepsVersionFromURL(String pomURL)  {
    File file = new File("pomFile.xml");
    try {
      URL url = new URL(pomURL);
      FileUtils.copyURLToFile(url, file);
      MavenXpp3Reader read = new MavenXpp3Reader();
      Model model = read.read(new FileReader(file));
      if (model.getDependencyManagement() == null)
        return null;
      for (org.apache.maven.model.Dependency dep : model.getDependencyManagement().getDependencies()) {
        if ("com.google.cloud".equals(dep.getGroupId()) && "google-cloud-shared-dependencies".equals(dep.getArtifactId()))
          return dep.getVersion();
      }

    } catch (XmlPullParserException | IOException ignored){}
    file.deleteOnExit();
    return null;
  }

  private static String getPomFileURL(String groupId, String artifactId, String version) {
    String groupPath = groupId.replace('.', '/');
    return basePath + "/" + groupPath
            + "/" + artifactId
            + "/" + version
            + "/" + artifactId + "-" + version + ".pom";
  }

  private static String getMetadataURL(Artifact artifact) {
    String groupPath = artifact.getGroupId().replace('.', '/');
    return basePath + "/" + groupPath
            + "/" + artifact.getArtifactId()
            + "/maven-metadata.xml";
  }
}

