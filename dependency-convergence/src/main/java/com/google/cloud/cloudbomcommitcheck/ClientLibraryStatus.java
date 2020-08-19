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

import com.google.common.base.Preconditions;

/**
 *  Classifies our artifact data into the four possible outcomes for output
 *  (1) Client library POM not found
 *  (2) Client library does not have google-cloud-shared-dependencies
 *  (3) Client library has old google-cloud-shared-dependencies version
 *  (4) Client library has newest google-cloud-shared-dependencies version
 */
public enum ClientLibraryStatus {
  /**
   * Library with the most recent version of shared dependencies
   */
  SUCCESSFUL(
      "SUCCESS - The following %d libraries had the latest version of google-cloud-shared-dependencies: "),

  /**
   * Library where the POM could not be found
   */
  UNFOUND_POM("FAIL - The following %d libraries had unfindable POM files: "),

  /**
   * Library that does not use google-cloud-shared-dependencies
   */
  NO_SHARED_DEPENDENCIES(
      "FAIL - The following %d libraries did not contain any version of google-cloud-shared-dependencies: "),

  /**
   * Library with an old version of google-cloud-shared-dependencies
   */
  OLD_SHARED_DEPENDENCIES(
      "FAIL - The following %d libraries had outdated versions of google-cloud-shared-dependencies: ");

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

  public static ClientLibraryStatus getLibraryStatus(ArtifactData artifactData,
      String latestSharedDependencies) {
    Preconditions.checkNotNull(artifactData);
    Preconditions.checkNotNull(latestSharedDependencies);

    String artifactDependenciesVersion = artifactData.getSharedDependenciesVersion();
    if (artifactDependenciesVersion == null) {
      return UNFOUND_POM;
    }
    if (artifactDependenciesVersion.isEmpty()) {
      return NO_SHARED_DEPENDENCIES;
    }
    if (latestSharedDependencies.equals(artifactDependenciesVersion)) {
      return SUCCESSFUL;
    }
    return OLD_SHARED_DEPENDENCIES;
  }
}