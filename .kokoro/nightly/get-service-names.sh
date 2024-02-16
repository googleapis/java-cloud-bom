#!/bin/bash

# This scripts downloads google-cloud-java repo, loop through modules with names starting "java-",
# grabs artifactId from pom.xml file within submodule name starting with "google-", and
# service name from *StubSettings.java file.

# Run this script from repo root dir
# input: N/A
# output: txt file with comma separated artifact_id, service_name.

git clone https://github.com/googleapis/google-cloud-java.git

cd ./google-cloud-java || exit
filename="../artifacts_to_services.txt"

for module in $(find . -mindepth 2 -maxdepth 2 -name pom.xml | sort | xargs dirname); do
    echo "module: ${module}"
    #  Only modules starting with java- contain client library artifacts.
    if [[ ${module} != ./java-* ]]; then
      echo "not a client library, continue..."
      continue
    fi
    # special cases, add manually later.
    if [[ ${module} == ./java-dns ]] || [[ ${module} == ./java-grafeas ]] || [[ ${module} == ./java-notification ]] || [[ ${module} == ./java-alloydb-connectors ]]; then
      continue
    fi
    cd "${module}" || exit
    #  Find submodule with name starting with "google-", this is to exclude proto, grpc and bom folders,
    #  and locate artifact id of client library
    folder=$(find . -mindepth 1 -maxdepth 1 -type d -name "google-*" ! -name "*-bom" )
    echo "folder: ${folder}"
    cd "${folder}" || continue
    artifact_id_string=$(find . -name 'pom.xml' -print -quit | xargs grep -m 1 '<artifactId>' | cut -d '>' -f 2 | cut -d '<' -f 1)
    echo "artifact_id_string: ${artifact_id_string}"
    cd .. # exist from folder ${folder}

    # Find *StubSettings file, get the first line containing '.googleapis.com:443'
    # Extract service name from it
    string=$(find . -name '*StubSettings.java' -print -quit | xargs grep -m 1 '.googleapis.com:443')
    service_name=$(echo "${string}" | grep -o '".*"' | tr -d '"' | cut -d "." -f 1 | cut -d "-" -f 1)
    echo "service name: ${service_name}"
    echo "${artifact_id_string}, ${service_name}" >> "$filename"
    cd .. # exit from ${module}
done

# add handwritten libraries manually.
{
  echo "google-cloud-bigquery, bigquery"
  echo "google-cloud-bigtable, bigtable"
  echo "google-cloud-bigquerystorage, bigquerystorage"
  echo "google-cloud-datastore, datastore"
  echo "google-cloud-firestore, firestore"
  echo "google-cloud-logging, logging"
  echo "google-cloud-pubsub, pubsub"
  echo "google-cloud-pubsublite, pubsublite"
  echo "google-cloud-storage, bigstore"
  echo "google-cloud-storage-control, storage"
  echo "google-cloud-spanner, spanner"
  echo "google-cloud-dns, dns"
} >> "./artifacts_to_services.txt"

cd ..
mv ./google-cloud-java/artifacts_to_services.txt ./libraries-release-data/artifacts_to_services.txt
# clean up
rm -rf google-cloud-java/