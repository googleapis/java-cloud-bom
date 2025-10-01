#!/bin/bash

# Clones the repositories we check in the workspace directory
# (/tmp/release-readiness). It's convenient to separate the data
# retrieval part (this script) and the logic (check_readiness.sh)
# separated, so that the maitnainers can iterate the logic without
# needing cloning the repositories.

set -ef

WORK_DIR=/tmp/release-readiness
mkdir -p ${WORK_DIR}

function clone_repository() {
  # e.g., "java-bigtable"
  repo_name=$1
  # e.g., "v2.55.1" or left empty
  revision=$2

  echo "cloning ${repo_name}"

  repo_dir="${WORK_DIR}/${repo_name}"
  if [ ! -d "${repo_dir}" ]; then
    repo_url="https://github.com/googleapis/${repo_name}"
    git clone --filter=blob:none --quiet --no-checkout --depth=1 --no-single-branch \
		"${repo_url}" "${repo_dir}"
    pushd "${repo_dir}" &> /dev/null
    git sparse-checkout init --no-cone 
    git sparse-checkout add "**/pom.xml"
    git sparse-checkout add "/generation_config.yaml"
	echo "${repo_name} successfully cloned"
    popd &> /dev/null
  fi
  pushd "${repo_dir}" &> /dev/null
  if [ -n "${revision}" ]; then
    git fetch origin &> /dev/null
    git checkout "${revision}" &> /dev/null
  else
    # There may be new updates since last run
    git pull --quiet
  fi
  echo "${repo_name} updated"
  popd &> /dev/null
}


# sdk-platform-java release 
# https://github.com/googleapis/sdk-platform-java/releases
sdk_platform_java_version=$1
if [ -z "$sdk_platform_java_version" ]; then
  echo "Specify sdk-platform-java release tag. Example: v2.55.1"
  exit 1
fi

# This requires to checkout the specific release version
clone_repository "sdk-platform-java" "${sdk_platform_java_version}" &

repos=("java-storage-nio" "java-storage" "java-spanner-jdbc" "java-spanner" \
  "java-pubsublite" "java-pubsub" "java-logging-logback" "java-logging" \
  "java-firestore" "java-datastore" "java-bigtable" "java-bigquery" \
  "java-bigquerystorage" "google-cloud-java")
for repo in ${repos[@]}; do
  clone_repository "${repo}" &
done
wait
echo "All repositories cloned."
