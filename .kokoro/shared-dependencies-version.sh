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

# Print out Java
java -version

# Get the directory of the build script
scriptDir=$(realpath $(dirname "${BASH_SOURCE[0]}"))

# Returns 0 if this is a release branch
function isReleaseBranch() {
  # Check if we're currently on a non-SNAPSHOT release branch.
  currBranch=$(git rev-parse --abbrev-ref HEAD | grep "release-v")
  snapShotCheck=$(echo ${currBranch} | grep "\-SNAPSHOT")
  # Return 1 (failure) if this is not a release branch
  if [[ -z ${currBranch} ]] || [[ ! -z ${snapShotCheck} ]]; then
    return 1
  fi
  echo ${currBranch}
  return 0
}

# Returns 0 if a library update commit was found, and 1 otherwise.
# Echos commit message (if found) before completion.
function getLibraryUpdateCommit() {
  currentCommitNumber=$(git rev-parse HEAD)

  # Check if we're updating a dependency including shared-dependencies.
  currentCommitMessage=$(git log -1 ${currentCommitNumber} | grep "deps: update dependency com.google.cloud:google-cloud-")

  if [[ -z ${currentCommitMessage} ]]; then
    return 1
  fi

  echo ${currentCommitMessage}
  return 0
}

runRelease=$(isReleaseBranch)
returnValue=$?
# If this is a release branch
if [[ ${returnValue} -eq 0 ]]; then
  # go to the dependency-convergence directory, i.e. our container for the script
  pushd "${scriptDir}/../dependency-convergence"
  mvn clean install -DskipTests
  # Single quotes around double quotes so that the branch name is counted as only one argument
  mvn exec:java -Dexec.args="'${runRelease}'"
  exitCode=$?
  popd
  exit ${exitCode}
fi

libraryUpdate=$(getLibraryUpdateCommit)
returnValue=$?

if [[ ${returnValue} -eq 0 ]]; then
  # go to the dependency-convergence directory, i.e. our container for the script
  pushd "${scriptDir}/../dependency-convergence"
  mvn clean install -DskipTests
  mvn exec:java -e -Dexec.args="'${libraryUpdate}'"
  exitCode=$?
  popd
  exit ${exitCode}
fi

echo "No release or library updates found! Assuming no error."
exit 0
