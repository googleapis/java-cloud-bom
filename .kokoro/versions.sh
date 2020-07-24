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

# Print out Java
java -version
echo $JOB_TYPE

export MAVEN_OPTS="-Xmx1024m "

#mvn clean install -DskipTests=true

#mvn -B dependency:analyze -DfailOnWarning=true

function versionsCheck() {
#Grab current commit message
currCommitNumber=$(git rev-parse HEAD)
currCommitMessage=$(git log -1 $currCommitNumber | grep "deps: update dependency com.google.cloud:google-cloud-")

if [[ -z $currCommitMessage ]]
then
  msg "Commit message does not have correct formatting. Assuming no error."
  return 0
fi

exclusion=$"google-cloud-core-bom"
exclusionCheck=$(echo "$currCommitMessage" | grep $exclusion)
if [[ ! -z $exclusionCheck ]]
then
  msg "The google-cloud-core-bom library does not require shared-dependencies!"
  return 0
fi

#Grab which dependency was updated
depFixed=${currCommitMessage:28}
depFixed=${depFixed/ to v/:}
depFixed=${depFixed/ *}

echo "Checking dependency: ""$depFixed"

version=${depFixed/*:}
version=${version/-*} #Remove any alphabetical characters at the end

artifactIdEnd=${depFixed/*google-cloud-}
artifactIdEnd=${artifactIdEnd/:*}
artifactIdEnd=${artifactIdEnd/-bom*}

rawFileStart="https://raw.githubusercontent.com/googleapis/java-"

gitPom=$rawFileStart$artifactIdEnd"/v"$version"/pom.xml"

content=$(wget $gitPom -q -O -)

echo "POM link found: ""$gitPom"
effectivePom=$content

#Grab all of the parent's information from the dependency's effective POM
parentDef="$(echo "$effectivePom" | sed '1,/<parent>/d' | sed -n '/<\/parent>/q;p')"

#There is no parent defined
if [[ -z $parentDef ]]
then
  msg "No parent has been defined for the given dependency!"
  return 1
fi

parentGroupId=${parentDef/*<groupId>}
parentGroupId=${parentGroupId/<\/groupId>*}

parentArtifactId=${parentDef/*<artifactId>}
parentArtifactId=${parentArtifactId/<\/artifactId>*}

parentVersion=${parentDef/*<version>}
parentVersion=${parentVersion/<\/version>*}

#Now check against the latest version
parentGroupAndArtifact=$parentGroupId":"$parentArtifactId

echo "Parent found: ""$parentGroupAndArtifact" #com.google.cloud:google-cloud-shared-dependencies
echo "Parent version found: ""$parentVersion"

latestParent=$parentGroupAndArtifact":LATEST"

latestParentEffectivePom=$(mvn help:effective-pom -Dartifact=$latestParent)

#Get the latest parent version from the effective POM
latestParent="$(echo "$latestParentEffectivePom" | grep $parentGroupAndArtifact)"
latestParent=${latestParent/* \'/}
latestParent=${latestParent/\' */}
latestParent=${latestParent/*:}

echo "Latest parent version: ""$latestParent"

#If the current parent version is not the same as the latest parent version, return failure!
if [[ $parentVersion != $latestParent ]]
then
  msg "Please double check commit with ID ""$currCommitNumber"
  msg "This commit updates to dependency version ""$depFixed"
  return 1
fi
return 0
}

# Allow failures to continue running the script
set +e

dir=$(dirname "./pom.xml")
versionsCheck "$dir"

if [[ $? == 0 ]]
then
  msg "Check passed."
  exit 0
else
  msg "Error found. See log statements above."
  exit 1
fi
