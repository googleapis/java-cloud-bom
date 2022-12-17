package com.google.cloud;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.cloud.tools.opensource.dependencies.Bom;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.truth.Truth;
import org.junit.Test;

public class ReleaseNoteGenerationTest {
  // Uses the released BOM, not the one in this repository, to avoid unnecessarily updating the
  // assertions.
  static final String LIBRARIES_BOM_COORDINATES = "com.google.cloud:libraries-bom:26.1.5";

  @Test
  public void testPreviousBom() throws Exception {
    Bom bom = Bom.readBom(LIBRARIES_BOM_COORDINATES);
    Bom previousBom = ReleaseNoteGeneration.previousBom(bom);
    Truth.assertThat(previousBom.getCoordinates())
        .isEqualTo("com.google.cloud:libraries-bom:pom:26.1.4");
  }

  @Test
  public void testCreateVersionLessCoordinatesToKey() throws Exception {
    Bom bom = Bom.readBom(LIBRARIES_BOM_COORDINATES);
    ImmutableMap<String, String> versionLessCoordinatesToKey =
        ReleaseNoteGeneration.createVersionLessCoordinatesToKey(bom);

    // google-cloud-apigee-registry represents the libraries from google-cloud-java
    // google-cloud-bigtable represents the libraries from handwritten repositories
    Truth.assertThat(versionLessCoordinatesToKey)
        .containsAtLeast(
            "com.google.cloud:google-cloud-apigee-registry", "0.6.0",
            "com.google.cloud:google-cloud-bigtable", "2.16.0");
  }

  @Test
  public void testIsMajorVersionBump() {
    assertTrue(ReleaseNoteGeneration.isMajorVersionBump("1.2.3", "2.0.1"));
    assertFalse(ReleaseNoteGeneration.isMajorVersionBump("2.2.3", "2.3.1"));
  }

  @Test
  public void testIsMinorVersionBump() {
    assertTrue(ReleaseNoteGeneration.isMinorVersionBump("1.2.3", "1.3.0"));
    assertFalse(ReleaseNoteGeneration.isMinorVersionBump("1.2.3", "1.2.5"));
    assertFalse(ReleaseNoteGeneration.isMinorVersionBump("1.2.3", "2.2.3"));
  }

  @Test
  public void testIsPatchVersionBump() {
    assertTrue(ReleaseNoteGeneration.isPatchVersionBump("1.2.3", "1.2.5"));
    assertFalse(ReleaseNoteGeneration.isPatchVersionBump("1.2.3", "1.2.3"));
    assertFalse(ReleaseNoteGeneration.isPatchVersionBump("1.2.3", "2.2.3"));
    assertFalse(ReleaseNoteGeneration.isPatchVersionBump("1.2.3", "1.3.3"));
  }

  @Test
  public void testPrintClientLibraryVersionDifference() throws Exception {
    ReleaseNoteGeneration generation = new ReleaseNoteGeneration();
    generation.printClientLibraryVersionDifference(
        ImmutableList.of(
            "com.google.cloud:google-cloud-redis", "com.google.cloud:google-cloud-logging"),
        ImmutableMap.of(
            "com.google.cloud:google-cloud-redis",
            "2.8.0",
            "com.google.cloud:google-cloud-logging",
            "3.12.0"),
        ImmutableMap.of(
            "com.google.cloud:google-cloud-redis",
            "2.9.0",
            "com.google.cloud:google-cloud-logging",
            "3.13.1"));

    String report = generation.report.toString();
    Truth.assertThat(report)
        .contains(
            "- google-cloud-redis:2.9.0 (prev:2.8.0; Release Notes: "
                + "[v2.9.0](https://github.com/googleapis/google-cloud-java/releases/tag/google-cloud-redis-v2.9.0))");
    Truth.assertThat(report)
        .contains(
            "- google-cloud-logging:3.13.1 (prev:3.12.0; Release Notes: "
                + "[v3.12.1](https://github.com/googleapis/java-logging/releases/tag/v3.12.1), "
                + "[v3.13.0](https://github.com/googleapis/java-logging/releases/tag/v3.13.0), "
                + "[v3.13.1](https://github.com/googleapis/java-logging/releases/tag/v3.13.1))");
  }

  @Test
  public void testFetchReleaseNote() throws Exception {
    String storageReleaseNote2_16_0 =
        ReleaseNoteGeneration.fetchReleaseNote("googleapis", "java-storage", "v2.16.0");
    Truth.assertThat(storageReleaseNote2_16_0)
        .contains(
            "* Add {Compose,Rewrite,StartResumableWrite}Request.object_checksums and "
                + "Bucket.RetentionPolicy.retention_duration "
                + "([#1790](https://github.com/googleapis/java-storage/issues/1790)) "
                + "([31c1b18](https://github.com/googleapis/java-storage/commit/31c1b18acc3c118e39eb613a82ee292f3e246b8f))");
  }
}
