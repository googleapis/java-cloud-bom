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
source ${scriptDir}/deps-finder.sh

succeeded=()
pomNotFound=()
noSharedDeps=()
incorrectVersion=()

# Print out Java
java -version

export MAVEN_OPTS="-Xmx1024m "

#$1 = input of the form groupId:artifactId:version

function releaseVersionsCheck() {
  latestSharedDeps=$(getLatestSharedDepsVersion)
  msg "Desired java-shared-dependencies version: " $latestSharedDeps
  depsManaged="$(sed '1,/dependencyManagement/d' pom.xml | sed -n '/dependencyManagement/q;p' | sed '/<!--/d')"
  ct=0
  totalCt=0

  #Iterate through all managed dependencies.
  while [[ ! -z $depsManaged ]]
  do
    currDep=$(echo "$depsManaged" | sed '1,/<dependency>/d' | sed -n '/<\/dependency>/q;p')
    depsManaged=${depsManaged/*$currDep} #Remove old dependency
    depsManaged=$(echo "$depsManaged" | sed 2d)
    #Check if we're looking at a valid dependency (starts with google-cloud and is nonempty)
    if [[ ! -z $currDep ]] && [[ ! -z $(echo $currDep | grep "google-cloud") ]]
    then
      artifactId=${currDep/*<artifactId>}
      artifactId=${artifactId/<\/artifactId>*}

      version=${currDep/*<version>}
      version=${version/<\/version>*}

      catted=$artifactId":"$version

      #Check if the dependency has the correct java-shared-dependencies version.
      pvOutput=$(checkDependency $catted $latestSharedDeps)
      case $? in
      "0")
        succeeded+=($catted)
      ;;
      "1")
        pomNotFound+=($catted)
        ct=$(($ct+1))
      ;;
      "2")
        noSharedDeps+=($catted)
        ct=$(($ct+1))
      ;;
      *)
        incorrectVersion+=("$pvOutput")
        ct=$(($ct+1))
      ;;
      esac
      totalCt=$(($totalCt+1))
  fi
done
return $ct
}

# Allow failures to continue running the script
set +e

#Check if we're currently on a non-SNAPSHOT release branch. If we are, we want to run the check.
currBranch=$(git rev-parse --abbrev-ref HEAD | grep "release-v")
snapShotCheck=$(echo $currBranch | grep "\-SNAPSHOT")
if [[ -z $currBranch ]] || [[ ! -z $snapShotCheck ]]
then
  msg "This is either a SNAPSHOT release or not a release! Returning success!"
  exit 0
fi

dir=$(dirname "./pom.xml")
releaseVersionsCheck

if [[ $? == 0 ]]
then
  msg "Check passed."
  exit 0
else #We fail if the number of failing client libraries is > 0.
  if [[ ! -z $succeeded ]]
  then
    echo "------------------------------------------------------------------------------"
    msg "Successful Client Libraries: "
    printf '%s\n' "${succeeded[@]}"
  fi
  if [[ ! -z $pomNotFound ]]
  then
    echo "------------------------------------------------------------------------------"
    msg "Client Libraries with Unfindable POMs: "
    printf '%s\n' "${pomNotFound[@]}"
  fi
  if [[ ! -z $noSharedDeps ]]
  then
    echo "------------------------------------------------------------------------------"
    msg "Client Libraries Missing java-shared-dependencies:"
    printf '%s\n' "${noSharedDeps[@]}"
  fi
  if [[ ! -z $incorrectVersion ]]
  then
    echo "------------------------------------------------------------------------------"
    msg "Client Libraries with an Incorrect Version of java-shared-dependencies:"
    printf '%s\n' "${incorrectVersion[@]}"
  fi
  msg "Errors found. See log statements above."
  msg "Number of incorrect dependencies found: $ct. Number of correct dependencies: $(($totalCt-$ct))."
  exit 1
fi