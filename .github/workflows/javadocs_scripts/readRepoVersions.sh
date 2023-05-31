#!/bin/bash

while read -r repo directory tag
do
  echo "Processing line:"
  echo "REPO=$repo"
  echo "DIRECTORY=$directory"
  echo "TAG=$tag"
done < repoVersions.txt

