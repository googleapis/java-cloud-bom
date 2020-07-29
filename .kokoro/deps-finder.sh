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

#Compare shared-dependencies version in a given dependency to desired version.
#Return 0 for success, 1 for failed to find POM, 2 for shared-deps not present, 3 for incorrect version.

#Options to run:
#0 Arguments: Test the latest commit (with a dependency update) for the latest version of shared-deps.
#1 Argument: Input artifactId:version to test the given dependency for the latest version of shared-deps.
#2 Arguments: Input artifactId:version to test the given dependency for a specific version of shared-dependencies
#(only input a version number for the second argument).

## Get the directory of the build script
scriptDir=$(realpath $(dirname "${BASH_SOURCE[0]}"))

# include common functions
source ${scriptDir}/common.sh

#Returns 1 if we find an updated dependency in the commit message.
function getDependency() {
  currCommitNumber=$(git rev-parse HEAD)

  #Note that this grep serves to check if we're updating a dependency including shared-dependencies.
  currCommitMessage=$(git log -1 $currCommitNumber | grep "deps: update dependency com.google.cloud:google-cloud-")

  if [[ -z $currCommitMessage ]]
  then
    echo "Commit message does not have correct formatting. Assuming no error."
    return 1
  fi

  dependency=${currCommitMessage:28}
  dependency=${dependency/ to v/:}
  dependency=${dependency/*com.google.cloud:}

  if [[ -z $dependency ]]
  then
    return 1
  fi

  echo "$dependency"
  return 0
}

function getLatestSharedDepsVersion() {
  group="com.google.cloud"
  artifact="google-cloud-shared-dependencies"
  latest="$group:$artifact:LATEST"
  pom=$(mvn help:effective-pom -Dartifact=$latest)

  if [[ -z $pom ]]
  then
    echo "ERROR: Failed to find effective POM!"
    return 1
  fi

  sharedDepsLatestVersion="$(echo "$pom" | grep "$group:$artifact")"
  sharedDepsLatestVersion=${sharedDepsLatestVersion/* \'/}
  sharedDepsLatestVersion=${sharedDepsLatestVersion/\' */}
  sharedDepsLatestVersion=${sharedDepsLatestVersion/*:}
  if [[ -z $sharedDepsLatestVersion ]]
  then
    echo "ERROR: Failed to find latest shared config version!"
    return 1
  fi

  echo "$sharedDepsLatestVersion"
  return 0
}

#Pass in dependency to script.
#Dependency should be of the form artifactId:version

function checkDependency() {
  dependency=$1

  #If no dependency was passed straight to the script, check our most recent commit for a dep version update.
  if [[ -z $dependency ]]
  then
    dependency=$(getDependency)
  else
    if [[ -z $(echo $dependency | grep google-cloud-) ]]
    then
      echo "$dependency does not require shared-dependencies criteria. Returning success."
      return 0
    fi
  fi

  #We did not successfully find a valid commit with a dependency. Return success - nothing to check here!
  if ! [[ $? -eq 0 ]]
  then
    echo "Commit message does not have correct formatting. Assuming no error."
    return 0
  fi

  #Should just be the desired version for shared-deps, e.g. "0.8.3"
  desiredDeps=$2

  if [[ -z $desiredDeps ]]
  then
    desiredDeps=$(getLatestSharedDepsVersion)
    if [[ -z $desiredDeps ]]
    then
      echo "Unable to find correct version of shared-dependencies."
      return 1
    fi
  fi

  depVersion=${dependency/*:} #Grab last part of dependency artifactId:version input.
  depVersion=${depVersion/-*} #Remove any alphabetical characters at the end (e.g. -alpha, -beta, etc)

  pomLocation="/pom.xml"

  #The naming of these libraries does not follow the rules. As such, we've added in special cases for them.
  #depAID=dependency artifact ID - this is the ID inside of the Github link for the dependency.
  case $dependency in
    *"google-cloud-bigtable-bom"*)
    depAID="java-bigtable"
    pomLocation="/google-cloud-bigtable-deps-bom/pom.xml"
    ;;
    *"google-cloud-core-bom"*)
    #This is an exceptional case which does not require shared-dependencies! Return true automatically.
    echo "Found exception google-cloud-core-bom!"
    return 0
    ;;
    *"google-cloud-build-bom"*)
    depAID="java-cloudbuild"
    ;;
    *"google-cloud-monitoring-dashboard-bom"*)
    depAID="java-monitoring-dashboards"
    ;;
    *"google-cloud-nio"*)
    depAID="java-storage-nio"
    ;;
    *)
    #General rules for obtaining the Github repo name are:
    #Replace google-cloud with java, (of course) remove the version number, and remove -bom from the end (if there)
    depAID=${dependency/*google-cloud-/java-}
    depAID=${depAID/:*}
    depAID=${depAID/-bom*}
    ;;
  esac

  #Grab raw POM file from Github
  rawFileStart="https://raw.githubusercontent.com/googleapis/"
  gitPom=$rawFileStart$depAID"/v"$depVersion$pomLocation

  #If $3 is nonempty, we're running this script directly.
  if [[ ! -z $3 ]]
  then
    echo "POM File Link Generated: $gitPom"
  fi

  content=$(wget $gitPom -q -O -)

  if [[ -z $content ]]
  then
    echo "Failed to find POM for $dependency!"
    return 1
  fi

  #Grab all information from the dependency's effective POM
  currDepsVersion="$(echo "$content" | sed '1,/google-cloud-shared-dependencies/d')"

  #shared-dependencies is not in our dependencyManagement section!
  if [[ -z $currDepsVersion ]]
  then
    echo "google-cloud-shared-dependencies is not listed for $dependency!"
    return 2
  fi

  currDepsVersion=${currDepsVersion/<\/version>*}
  currDepsVersion=${currDepsVersion/*<version>}

  if [[ $currDepsVersion != $desiredDeps ]]
  then
    echo "$dependency - Version Found: $currDepsVersion"
    return 3
  fi

  echo "$dependency passed java-shared-dependencies check."
  return 0
}

#If we're running the script directly from this file, we likely want to check the current commit, or skip.
if [ "${BASH_SOURCE[0]}" -ef "$0" ]
then
  echo $(checkDependency "$1" "$2" 1)
  exit $?
fi