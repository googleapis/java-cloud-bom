#!/bin/bash
# Copyright 2020 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# Helper functions for the deps-finder and release-versions scripts.
function isReleaseBranch() {
  # Check if we're currently on a non-SNAPSHOT release branch.
  # If we are, we want to run the check.
  currBranch=$(git rev-parse --abbrev-ref HEAD | grep "release-v")
  snapShotCheck=$(echo ${currBranch} | grep "\-SNAPSHOT")
  if [[ -z ${currBranch} ]] || [[ ! -z ${snapShotCheck} ]]; then
    return 1
  fi
  return 0
}

# Returns 1 if we find an updated client library in the commit message.
# Echos client library (if found) before completion.
function getLibraryUpdate() {
  currentCommitNumber=$(git rev-parse HEAD)

  # Check if we're updating a dependency including shared-dependencies.
  currentCommitMessage=$(git log -1 ${currentCommitNumber} | grep "deps: update dependency com.google.cloud:google-cloud-")

  if [[ -z ${currentCommitMessage} ]]; then
    echo "Commit message does not have correct formatting. Assuming no error."
    return 1
  fi

  #  Use regex to grab the dependency group and artifact IDs

  # Find group and artifact by format
  groupAndArtifact=$(echo ${currentCommitMessage} | grep -oP 'com.google.cloud:google-cloud-\S+')

  # Grab Artifact
  artifact=${groupAndArtifact/*:/}

  # Find version by format v[Number][Everything else before whitespace]
  version=$(echo ${currentCommitMessage} | grep -oP 'v[0-9]([^\s]+)')
  version=${version/*v/}

  artifactAndVersion=${artifact}$":"${version}

  # If we failed to find either the artifact or the version, return failure.
  if [[ -z ${artifact} ]] || [[ -z ${version} ]]; then
    return 1
  fi

  echo "${artifactAndVersion}"
  return 0
}

# Gets the latest version of java-shared-dependencies
function getLatestSharedDepsVersion() {
  group="com.google.cloud"
  artifact="google-cloud-shared-dependencies"
  latest="${group}:${artifact}:LATEST"
  pom=$(mvn help:effective-pom -Dartifact=${latest})

  if [[ -z $pom ]]; then
    echo "ERROR: Failed to find effective POM of java-shared-dependencies!"
    return 1
  fi

  sharedDepsLatestVersion=$(echo "${pom}" | grep -oP 'com.google.cloud:google-cloud-shared-dependencies:pom:[0-9.a-z]*')
  sharedDepsLatestVersion=${sharedDepsLatestVersion/*:/}

  if [[ -z ${sharedDepsLatestVersion} ]]; then
    echo "ERROR: Failed to find latest shared config version!"
    return 1
  fi

  echo "${sharedDepsLatestVersion}"
  return 0
}

# Options to run:
# 0 Arguments: Test the latest commit (from a dependency update) for the latest version of shared-deps.
# 1 Argument: Input artifactId:version to test the given dependency for the latest version of shared-deps.
# 2 Arguments: Input artifactId:version to test the given dependency for a specific version of shared-dependencies
# (only input a version number for the second argument e.g. "0.8.3").
function checkLibrary() {
  # Should be of the format artifactId:version (see above for arguments)
  clientLibrary=$1

  # Should just be the desired version for shared-deps, e.g. "0.8.3" (see above for arguments)
  latestSharedDeps=$2

  # If no dependency was passed straight to the script, check our most recent commit for a dep version update.
  if [[ -z ${clientLibrary} ]]; then
    clientLibrary=$(getLibraryUpdate)
    # We did not successfully find a valid commit with a dependency. Return success - nothing to check here!
    if ! [[ $? -eq 0 ]]; then
      echo "Commit message does not have correct formatting. Assuming no error."
      return 0
    fi
  else
    # Check if dependency contains 'google-cloud-'. If it does not, it's already valid!
    if [[ -z $(echo ${clientLibrary} | grep "google-cloud-") ]]; then
      echo "${clientLibrary} does not require shared-dependencies criteria. Returning success."
      return 0
    fi
  fi

  if [[ -z ${latestSharedDeps} ]]; then
    latestSharedDeps=$(getLatestSharedDepsVersion)
    if [[ -z ${latestSharedDeps} ]]; then
      echo "Unable to find correct version of shared-dependencies."
      return 1
    fi
  fi

  # Grab last part of dependency artifactId:version input.
  # Remove any alphabetical characters at the end (e.g. -alpha, -beta, etc)
  depVersion=${clientLibrary/*:/}
  depVersion=${depVersion/-*/}

  pomLocation="/pom.xml"

  # The naming of these libraries does not follow the rules. As such, we've added in special cases for them.
  # clientRepoName => the ID inside of the Github link for the dependency.
  case ${clientLibrary} in
  *"google-cloud-bigtable-bom"*)
    clientRepoName="java-bigtable"
    pomLocation="/google-cloud-bigtable-deps-bom/pom.xml"
    ;;
  *"google-cloud-core-bom"*)
    # This is an exceptional case which does not require shared-dependencies! Return true automatically.
    echo "Found exception google-cloud-core-bom!"
    return 0
    ;;
  *"google-cloud-build-bom"*)
    clientRepoName="java-cloudbuild"
    ;;
  *"google-cloud-monitoring-dashboard-bom"*)
    clientRepoName="java-monitoring-dashboards"
    ;;
  *"google-cloud-nio"*)
    clientRepoName="java-storage-nio"
    ;;
  *)
    # General rules for obtaining the Github repo name are:
    # Replace google-cloud with java, remove the version number, and remove -bom from the end (if there)
    clientRepoName=${clientLibrary/*google-cloud-/java-}
    clientRepoName=${clientRepoName/:*/}
    clientRepoName=${clientRepoName/-bom*/}
    ;;
  esac
  # Grab raw POM file from Github
  rawFileStart="https://raw.githubusercontent.com/googleapis/"
  gitPom=${rawFileStart}${clientRepoName}"/v"${depVersion}${pomLocation}

  # If $3 is nonempty, we're running this script directly.
  if [[ ! -z $3 ]]; then
    echo "POM File Link Generated: ${gitPom}"
  fi

  content=$(wget ${gitPom} -q -O -)

  if [[ -z $content ]]; then
    echo "Failed to find POM for ${clientLibrary}!"
    return 1
  fi

  # Grab all information from the client library's effective POM
  currDepsVersion="$(echo "${content}" | sed '1,/google-cloud-shared-dependencies/d')"

  # shared-dependencies is not in our dependencyManagement section!
  if [[ -z $currDepsVersion ]]; then
    echo "google-cloud-shared-dependencies is not listed for ${clientLibrary}!"
    return 2
  fi

  currDepsVersion=${currDepsVersion/<\/version>*/}
  currDepsVersion=${currDepsVersion/*<version>/}

  if [[ ${currDepsVersion} != ${latestSharedDeps} ]]; then
    echo "${clientLibrary} - Version Found: ${currDepsVersion}"
    return 3
  fi

  echo "${clientLibrary} passed java-shared-dependencies check."
  return 0
}
