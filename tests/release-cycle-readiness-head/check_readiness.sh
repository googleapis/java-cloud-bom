# Given the repositories are checked out in the WORK_DIR below
# find the shared dependencies BOM version in current release
# and find any missing updates.

set -ef

WORK_DIR=/tmp/release-readiness

cd "${WORK_DIR}/sdk-platform-java"
expected_shared_deps_version=$(mvn -pl java-shared-dependencies help:evaluate -Dexpression=project.version -q -DforceStdout)
echo "Expected google-cloud-shared-dependencies BOM version: ${expected_shared_deps_version}"

repositories=$(find "${WORK_DIR}" -mindepth 1 -maxdepth 1 -type d -not -name "sdk-platform-java")
for repo_folder in $repositories; do
  cd "${repo_folder}"
  repo=$(basename "${repo_folder}")
  if [[ "$repo" == "google-cloud-java" ]]; then
    # In google-cloud-java repository, the parent pom module
    # inherits the property.
    project=google-cloud-pom-parent
  else
    # In normal handwritten libraries, the root project receives the property.
    project=.
  fi
  actual_shared_deps_version=$(mvn -pl ${project} help:evaluate -Dexpression=google-cloud-shared-dependencies.version -q -DforceStdout)
  if [ "${expected_shared_deps_version}" != "${actual_shared_deps_version}" ]; then
    echo "${repo}: Not yet... expected: ${expected_shared_deps_version}, actual: ${actual_shared_deps_version}"
  else
    echo "${repo}: OK (${actual_shared_deps_version})"
  fi
done

