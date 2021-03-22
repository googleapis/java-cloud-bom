#!/bin/bash
# Copyright 2021 Google LLC
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

output="$scriptDir/dashboard/target/tmp/output.txt"
## Move into the dashboard directory
cd dashboard/

echo -e "\n******************** BUILDING THE DASHBOARD ********************"

mvn --fail-at-end clean install
INSTALL_RETURN_CODE=$?
RETURN_CODE=${INSTALL_RETURN_CODE}

LINE_COUNT=0

case ${JOB_TYPE} in
dependency-convergence-check)
    mvn exec:java -Dexec.args="-f ../pom.xml --report"
    CONVERGE_RETURN_CODE=$?
    if [[ $INSTALL_RETURN_CODE -eq 0 ]]
    then
      while IFS= read -r line; do
        msg "$line"
        LINE_COUNT=$LINECOUNT+1
      done < "$output"
      RETURN_CODE=${CONVERGE_RETURN_CODE}
    fi
    ;;
esac


if [[ $RETURN_CODE -ne  0  ||  $LINE_COUNT -gt 1 ]]
then
  RETURN_CODE=1
fi

echo "exiting with ${RETURN_CODE}"
exit ${RETURN_CODE}
