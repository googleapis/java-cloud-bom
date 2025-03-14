# Given the repositories are checked out in the WORK_DIR below
# find the shared dependencies BOM version in current release
# and find any missing updates.

set -ef

WORK_DIR=/tmp/release-readiness

cd "${WORK_DIR}/sdk-platform-java"
expected_shared_deps_version=$(mvn -pl java-shared-dependencies help:evaluate -Dexpression=project.version -q -DforceStdout)
expected_generator_version=$(mvn -pl gapic-generator-java help:evaluate -Dexpression=project.version -q -DforceStdout)
echo "Expected google-cloud-shared-dependencies BOM version: ${expected_shared_deps_version}"
echo "Expected GAPIC generator Java version: ${expected_generator_version}"

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
  if [[ "${expected_shared_deps_version}" != "${actual_shared_deps_version}" ]]; then
    shared_deps_status="Not yet(${actual_shared_deps_version})"
  else
    shared_deps_status="OK"
  fi

  if [ -r "generation_config.yaml" ]; then
    actual_generator_version=$(perl -nle 'print $1 if m/gapic_generator_version:\s*(.+)/' generation_config.yaml)
    if [[ "${expected_generator_version}" != "${actual_generator_version}" ]]; then
        generator_status="Not yet(${actual_generator_version})"
    else
        generator_status="OK"
    fi
  else
    generator_status="N/A"
  fi
  echo "${repo} | ${shared_deps_status} | ${generator_status}"
done



