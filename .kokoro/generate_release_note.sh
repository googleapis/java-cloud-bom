#!/bin/bash

set -ef
# Upon release, GitHub Actions receive GITHUB_REF which has the target ref
# of the release "refs/tags/<tag_name>".
# https://docs.github.com/en/actions/using-workflows/events-that-trigger-workflows#release

echo "GITHUB_REF: ${GITHUB_REF}"

# This writes release_note.md
cd release-note-generation
mvn compile
mvn exec:java -Dexec.args="com.google.cloud:libraries-bom:26.1.5"

echo "Release note content:"
cat release_note.md
echo "-----------------------"

