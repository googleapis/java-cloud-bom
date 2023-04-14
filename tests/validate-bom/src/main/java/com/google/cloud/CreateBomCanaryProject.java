package com.google.cloud;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.cloud.tools.opensource.dependencies.Bom;
import com.google.cloud.tools.opensource.dependencies.MavenRepositoryException;
import com.google.common.base.Verify;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.eclipse.aether.artifact.Artifact;

/**
 * Creates a Maven project that uses the specified BOM at the specified directory. This class reads
 * the following system properties:
 *
 * <ul>
 *   <li>outputPath: the path to the directory to create the Maven project
 *   <li>bomPath: the path to the BOM
 * </ul>
 */
public class CreateBomCanaryProject {

  public static void main(String[] arguments) throws Exception {
    String outputPathProperty = System.getProperty("outputPath");
    checkNotNull(outputPathProperty, "System property outputPath should not be null");
    Path outputProjectDirectory = Paths.get(outputPathProperty);
    String bomPathProperty = System.getProperty("bomPath");
    checkNotNull(bomPathProperty, "System property bomPath should not be null");
    Path bomPath = Paths.get(bomPathProperty);

    Bom bom;
    try {
      bom = Bom.readBom(bomPath);
    } catch (MavenRepositoryException exception) {
      throw new IOException(
          "Could not read the BOM: "
              + bomPath
              + ". Please ensure all artifacts in the BOM are available in Maven Central or local"
              + " Maven repository.",
          exception);
    }

    String pomTemplate = readPomTemplate();

    String dependencyManagementSection = calculateDependencyManagementSection(bom);
    String dependenciesSection = calculateDependenciesSection(bom);

    String replacedContent =
        pomTemplate
            .replace("<!-- DEPENDENCY_MANAGEMENT -->", dependencyManagementSection)
            .replace("<!-- DEPENDENCIES -->", dependenciesSection);

    Path pomToWrite = outputProjectDirectory.resolve("pom.xml");
    Files.write(pomToWrite, replacedContent.getBytes());
    System.out.println("Wrote " + pomToWrite);
  }

  /** Returns the pom.xml template content. */
  private static String readPomTemplate() throws IOException {
    try (InputStream inputStream =
        CreateBomCanaryProject.class.getClassLoader().getResourceAsStream("template.pom.xml")) {
      Verify.verifyNotNull(inputStream);
      return new String(inputStream.readAllBytes());
    }
  }

  /** Returns the dependencyManagement section to import {@code bom}. */
  private static String calculateDependencyManagementSection(Bom bom) {
    String[] coordinatesElements = bom.getCoordinates().split(":");
    Verify.verify(coordinatesElements.length == 3);
    String groupId = coordinatesElements[0];
    String artifactId = coordinatesElements[1];
    String version = coordinatesElements[2];

    StringBuilder builder = new StringBuilder();
    builder.append("  <dependencyManagement>\n");
    builder.append("    <dependencies>\n");
    builder.append("      <dependency>\n");
    builder.append("        <groupId>").append(groupId).append("</groupId>\n");
    builder.append("        <artifactId>").append(artifactId).append("</artifactId>\n");
    builder.append("        <version>").append(version).append("</version>\n");
    builder.append("        <type>pom</type>\n");
    builder.append("        <scope>import</scope>\n");
    builder.append("      </dependency>\n");
    builder.append("    </dependencies>\n");
    builder.append("  </dependencyManagement>\n");
    return builder.toString();
  }

  /** Returns the "dependencies" section that would declare all artifacts appear in {@code bom}. */
  private static String calculateDependenciesSection(Bom bom) {
    StringBuilder builder = new StringBuilder();
    builder.append("  <dependencies>\n");

    for (Artifact managedDependency : bom.getManagedDependencies()) {
      Map<String, String> properties = managedDependency.getProperties();
      String classifier = managedDependency.getClassifier();
      if ("tests".equals(classifier)) {
        // Tests classifier artifacts are not for customers
        continue;
      }
      String type = properties.get("type");
      if ("pom".equals(type)) {
        // Some artifacts have :pom" type, such as io.grpc:protoc-gen-grpc-java
        // and com.google.api-client:google-api-client-assembly. We are only interested
        // in "jar" artifacts.
        continue;
      }

      builder.append("    <dependency>\n");
      builder
          .append("      <groupId>")
          .append(managedDependency.getGroupId())
          .append("</groupId>\n");
      builder
          .append("      <artifactId>")
          .append(managedDependency.getArtifactId())
          .append("</artifactId>\n");
      builder.append("    </dependency>\n");
    }
    builder.append("  </dependencies>\n");

    return builder.toString();
  }
}
