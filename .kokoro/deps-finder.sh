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

# Get the directory of the build script
scriptDir=$(realpath $(dirname "${BASH_SOURCE[0]}"))

# include common functions
source ${scriptDir}/common.sh
source ${scriptDir}/shared-deps-helper.sh

# Compare shared-dependencies version in a given dependency to desired version.
# Return 0 for success, 1 for failed to find POM, 2 for shared-deps not present,
# 3 for incorrect version of java-shared-dependencies.

libraryMessage=$(checkLibrary "$1" "$2" 1)
exitCode=$?
echo "${libraryMessage}"
exit ${exitCode}
