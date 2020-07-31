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

# Compare shared-dependencies version in a given dependency to desired version.
# Return 0 for success, 1 for failed to find POM, 2 for shared-deps not present, 3 for incorrect version.

# Options to run:
# 0 Arguments: Test the latest commit (with a dependency update) for the latest version of shared-deps.
# 1 Argument: Input artifactId:version to test the given dependency for the latest version of shared-deps.
# 2 Arguments: Input artifactId:version to test the given dependency for a specific version of shared-dependencies
# (only input a version number for the second argument).

# Get the directory of the build script
scriptDir=$(realpath $(dirname "${BASH_SOURCE[0]}"))

# include common functions
source ${scriptDir}/common.sh
source ${scriptDir}/shared-deps-helper.sh

# Pass in dependency to script.
# Dependency should be of the form artifactId:version

function checkLibrary() {
  clientLibrary=$1 # Should be of the format artifactId:version

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

  # Should just be the desired version for shared-deps, e.g. "0.8.3"
  latestSharedDeps=$2

  if [[ -z ${latestSharedDeps} ]]; then
    latestSharedDeps=$(getLatestSharedDepsVersion)
    if [[ -z ${latestSharedDeps} ]]; then
      echo "Unable to find correct version of shared-dependencies."
      return 1
    fi
  fi

  depVersion=${clientLibrary/*:/} # Grab last part of dependency artifactId:version input.
  depVersion=${depVersion/-*/}    # Remove any alphabetical characters at the end (e.g. -alpha, -beta, etc)

  pomLocation="/pom.xml"

  # The naming of these libraries does not follow the rules. As such, we've added in special cases for them.
  # depArtifactId => the ID inside of the Github link for the dependency.
  case ${clientLibrary} in
  *"google-cloud-bigtable-bom"*)
    depArtifactId="java-bigtable"
    pomLocation="/google-cloud-bigtable-deps-bom/pom.xml"
    ;;
  *"google-cloud-core-bom"*)
    # This is an exceptional case which does not require shared-dependencies! Return true automatically.
    echo "Found exception google-cloud-core-bom!"
    return 0
    ;;
  *"google-cloud-build-bom"*)
    depArtifactId="java-cloudbuild"
    ;;
  *"google-cloud-monitoring-dashboard-bom"*)
    depArtifactId="java-monitoring-dashboards"
    ;;
  *"google-cloud-nio"*)
    depArtifactId="java-storage-nio"
    ;;
  *)
    # General rules for obtaining the Github repo name are:
    # Replace google-cloud with java, remove the version number, and remove -bom from the end (if there)
    depArtifactId=${clientLibrary/*google-cloud-/java-}
    depArtifactId=${depArtifactId/:*/}
    depArtifactId=${depArtifactId/-bom*/}
    ;;
  esac
  # Grab raw POM file from Github
  rawFileStart="https://raw.githubusercontent.com/googleapis/"
  gitPom=${rawFileStart}${depArtifactId}"/v"${depVersion}${pomLocation}

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

# If we're running the script directly from this file, we likely want to check the current commit, or skip.
if [ "${BASH_SOURCE[0]}" -ef "$0" ]; then
  echo $(checkLibrary "$1" "$2" 1)
  exit $?
fi
