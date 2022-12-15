#!/bin/bash

set -ef
# Upon release, GitHub Actions receive GITHUB_REF which has the target ref
# of the release "refs/tags/<tag_name>".
# https://docs.github.com/en/actions/using-workflows/events-that-trigger-workflows#release

echo "GITHUB_REF: ${GITHUB_REF}"

echo "LIBRARIES_BOM_VERSION: ${LIBRARIES_BOM_VERSION}"

if [ -z "${LIBRARIES_BOM_VERSION}" ]; then
  echo "Couldn't retrieve LIBRARIES_BOM_VERSION. Please specify the parameter (e.g., 26.1.5)"
  exit 1
fi

# This writes release_note.md
cd release-note-generation
mvn compile
mvn exec:java -Dexec.args="com.google.cloud:libraries-bom:${LIBRARIES_BOM_VERSION}"

echo "Release note content:"
cat release_note.md
echo "-----------------------"

