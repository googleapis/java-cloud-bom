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

set -eo pipefail

## Get the directory of the build script
scriptDir=$(realpath $(dirname "${BASH_SOURCE[0]}"))
## cd to the parent directory, i.e. the root of the git repo
cd ${scriptDir}/..

# include common functions
source ${scriptDir}/common.sh
source ${scriptDir}/shared-deps-helper.sh

successfulClientLibraries=()
pomNotFoundLibraries=()
librariesWithoutSharedDeps=()
librariesWithBadSharedDepsVersion=()

# Print out Java
java -version

export MAVEN_OPTS="-Xmx1024m "

function releaseVersionsCheck() {
  latestSharedDeps=$(getLatestSharedDepsVersion)
  if [[ -z ${latestSharedDeps} ]]; then
    echo "Unable to find correct version of shared-dependencies."
    exit 1
  fi
  msg "Desired java-shared-dependencies version:  ${latestSharedDeps}"
  depsManaged="$(sed '1,/dependencyManagement/d' pom.xml | sed -n '/dependencyManagement/q;p' | sed '/<!--/d')"

  if [[ -z ${depsManaged} ]]; then
    msg "Unable to find dependency management section!"
    return 1
  fi

  failedLibrariesCount=0
  totalLibrariesCount=0

  # Iterate through all managed dependencies.
  while true; do
    currentLibrary=$(echo "${depsManaged}" | sed '1,/<dependency>/d' | sed -n '/<\/dependency>/q;p')
    if [[ -z ${currentLibrary} ]]; then
      break
    fi
    # Remove this client library from our list of dependencies
    depsManaged=${depsManaged/*$currentLibrary/}
    # Check if we're looking at a valid dependency (contains google-cloud)
    if [[ ! -z $(echo ${currentLibrary} | grep "google-cloud") ]]; then
      artifactId=${currentLibrary/*<artifactId>/}
      artifactId=${artifactId/<\/artifactId>*/}

      version=${currentLibrary/*<version>/}
      version=${version/<\/version>*/}

      libraryArtifactAndVersion=${artifactId}":"${version}
      # Check if the dependency has the correct java-shared-dependencies version.
      sharedDepsCheckOutput=$(checkLibrary ${libraryArtifactAndVersion} ${latestSharedDeps})

      case $? in
      "0")
        successfulClientLibraries+=(${libraryArtifactAndVersion})
        ;;
      "1")
        pomNotFoundLibraries+=(${libraryArtifactAndVersion})
        failedLibrariesCount=$((${failedLibrariesCount} + 1))
        ;;
      "2")
        librariesWithoutSharedDeps+=(${libraryArtifactAndVersion})
        failedLibrariesCount=$((${failedLibrariesCount} + 1))
        ;;
      *)
        librariesWithBadSharedDepsVersion+=("${sharedDepsCheckOutput}")
        failedLibrariesCount=$((${failedLibrariesCount} + 1))
        ;;
      esac
      totalLibrariesCount=$((${totalLibrariesCount} + 1))
    fi
  done
  return ${failedLibrariesCount}
}

# Allow failures to continue running the script
set +e

# Allow the script to run normally if we're passed any argument
if [[ -z $1 ]]; then
  # Otherwise if we're on a release branch
  isReleaseBranch
  if [[ ! $? -eq 0 ]]; then
    msg "This is either a SNAPSHOT release or not a release! Returning success!"
    exit 0
  fi
fi

releaseVersionsCheck

# We fail if the number of failing client libraries is > 0.
if [[ $? -eq 0 ]]; then
  msg "Check passed. All libraries have the correct version of java-shared-dependencies!"
  exit 0
else
  if [[ ! -z ${successfulClientLibraries} ]]; then
    echo "------------------------------------------------------------------------------"
    msg "Successful Client Libraries: "
    printf '%s\n' "${successfulClientLibraries[@]}"
  fi
  if [[ ! -z ${pomNotFoundLibraries} ]]; then
    echo "------------------------------------------------------------------------------"
    msg "Client Libraries with Unfindable POMs: "
    printf '%s\n' "${pomNotFoundLibraries[@]}"
  fi
  if [[ ! -z ${librariesWithoutSharedDeps} ]]; then
    echo "------------------------------------------------------------------------------"
    msg "Client Libraries Missing java-shared-dependencies:"
    printf '%s\n' "${librariesWithoutSharedDeps[@]}"
  fi
  if [[ ! -z ${librariesWithBadSharedDepsVersion} ]]; then
    echo "------------------------------------------------------------------------------"
    msg "Client Libraries with an Incorrect Version of java-shared-dependencies:"
    printf '%s\n' "${librariesWithBadSharedDepsVersion[@]}"
  fi
  msg "Errors found. See log statements above."
  msg "Number of incorrect dependencies found: ${failedLibrariesCount}. Number of correct dependencies: $((${totalLibrariesCount} - ${failedLibrariesCount}))."
  exit 1
fi
