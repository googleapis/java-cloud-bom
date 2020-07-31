#!/bin/bash

#Helper functions for the deps-finder and release-versions scripts.

function isReleaseBranch() {
  #Check if we're currently on a non-SNAPSHOT release branch. If we are, we want to run the check.
  currBranch=$(git rev-parse --abbrev-ref HEAD | grep "release-v")
  snapShotCheck=$(echo ${currBranch} | grep "\-SNAPSHOT")
  if [[ -z ${currBranch} ]] || [[ ! -z ${snapShotCheck} ]]; then
    return 1
  fi
  return 0
}

#  Returns 1 if we find an updated client library in the commit message.
#Echos client library (if found) before completion.
function getLibraryUpdate() {
  currentCommitNumber=$(git rev-parse HEAD)

  # Note that this grep serves to check if we're updating a dependency including shared-dependencies.
  currentCommitMessage=$(git log -1 ${currentCommitNumber} | grep "deps: update dependency com.google.cloud:google-cloud-")

  if [[ -z ${currentCommitMessage} ]]; then
    echo "Commit message does not have correct formatting. Assuming no error."
    return 1
  fi

  #  Use regex to grab the dependency group and artifact IDs

  #Find group and artifact by format
  groupAndArtifact=$(echo ${currentCommitMessage} | grep -oP 'com.google.cloud:google-cloud-\S+')

  # Grab Artifact
  artifact=${groupAndArtifact/*:/}

  # Find version by format v[Number][Everything else before whitespace]
  version=$(echo ${currentCommitMessage} | grep -oP 'v[0-9]([^\s]+)')
  version=${version/*v/}

  artifactAndVersion=${artifact}$":"${version}

  # If we failed to find either the artifact or the version, return failure.
  if [[ -z ${artifact} ]] || [[ -z ${version} ]]; then
    return 1
  fi

  echo "$artifactAndVersion"
  return 0
}

# Gets the latest version of java-shared-dependencies
function getLatestSharedDepsVersion() {
  group="com.google.cloud"
  artifact="google-cloud-shared-dependencies"
  latest="${group}:${artifact}:LATEST"
  pom=$(mvn help:effective-pom -Dartifact=${latest})

  if [[ -z $pom ]]; then
    echo "ERROR: Failed to find effective POM of java-shared-dependencies!"
    return 1
  fi

  sharedDepsLatestVersion=$(echo "${pom}" | grep -oP 'com.google.cloud:google-cloud-shared-dependencies:pom:[0-9.a-z]*')
  sharedDepsLatestVersion=${sharedDepsLatestVersion/*:/}

  if [[ -z ${sharedDepsLatestVersion} ]]; then
    echo "ERROR: Failed to find latest shared config version!"
    return 1
  fi

  echo "${sharedDepsLatestVersion}"
  return 0
}
