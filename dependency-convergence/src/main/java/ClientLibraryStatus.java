/**
 * Classifies our artifact data into the four possible outcomes for output - (1) Client library
 * POM not found (2) Client library does not have shared-dependencies (3) Client library has old
 * shared-dependencies version (4) Client library has newest shared-dependencies version
 */
public enum ClientLibraryStatus {
  /**
   * Library with the most recent version of shared dependencies
   */
  SUCCESSFUL("SUCCESS - The following %d libraries had the latest version of google-cloud-shared-dependencies: "),

  /**
   * Library where the POM could not be found
   */
  UNFOUND_POM("FAIL - The following %d libraries had unfindable POM files: "),

  /**
   * Library that does not use google-cloud-shared-dependencies
   */
  NO_SHARED_DEPENDENCIES("FAIL - The following %d libraries did not contain any version of google-cloud-shared-dependencies: "),

  /**
   * Library with an old version of google-cloud-shared-dependencies
   */
  OLD_SHARED_DEPENDENCIES("FAIL - The following %d libraries had outdated versions of google-cloud-shared-dependencies: ");

  private final String outputFormatter;

  ClientLibraryStatus(String outputFormatter) {
    this.outputFormatter = outputFormatter;
  }

  /**
   * @return output formatter detailing the number of libraries matching this status
   */
  public String getOutputFormatter() {
    return outputFormatter;
  }

  public static ClientLibraryStatus getLibraryStatus(ArtifactData artifactData, String latestSharedDependencies) {
    if (artifactData == null || latestSharedDependencies == null) {
      return null;
    }
    String artifactDependenciesVersion = artifactData.getSharedDependenciesVersion();
    if (artifactDependenciesVersion == null) {
      return UNFOUND_POM;
    }
    if(artifactDependenciesVersion.isEmpty()) {
      return NO_SHARED_DEPENDENCIES;
    }
    if (latestSharedDependencies.equals(artifactDependenciesVersion)) {
      return SUCCESSFUL;
    }
    return OLD_SHARED_DEPENDENCIES;
  }
}