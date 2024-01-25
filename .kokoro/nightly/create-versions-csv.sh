#!/bin/bash

# Output:
# The script generates cloud_java_client_library_release_dates.csv that holds the data defined below.
# It has artifact_id,service_name,version, and release_date columns.
# this csv file will be uploaded to (project) cloud-java-metrics.(dataset) client_library_versions. (table) cloud_java_client_library_release_dates
# using bq load command

# Fail on any error.
set -e
# Display commands being run.
set -x

cd github/java-cloud-bom

# prepare list of artifact id and service name match
.kokoro/nightly/get-service-names.sh

mvn -B clean install

cd libraries-release-data

mvn compile

list=$(mvn -B exec:java -Dexec.mainClass="com.google.cloud.dashboard.GenerateLibrariesList")


final_list=$(echo $list | grep -o 'com.google.cloud[^,]*' | tr '\n' ',' | sed 's/,$//')


echo ${final_list} | tr ',' '\n' > unfiltered-libraries.txt
sed -i '/libraries-release-data/d' unfiltered-libraries.txt
sort unfiltered-libraries.txt | uniq > libraries.txt
rm -f unfiltered-libraries.txt

service_file="artifacts_to_services.txt"

cat libraries.txt | while read line; do

  group_id=${line%:*}
  artifact_id=${line#*:}
  new_group_id="${group_id//.//}"
  # Check if artifactId contains "emulator"
  if [[ $artifact_id =~ .*emulator.* ]]; then
      echo "artifactId contains 'emulator': $artifactId"
      continue
  fi
  service_name=$(grep "^${artifact_id}," "$service_file" | cut -d ',' -f 2)
  if [[ -n $service_name ]]; then
      echo "Service Name found: $service_name"
  else
      echo "No matching service name found for artifactId: $artifact_id"
  fi

  URL=https://repo1.maven.org/maven2/$new_group_id/$artifact_id

  ../.kokoro/nightly/fetch-library-data.sh $URL $artifact_id $service_name

done

rm -f libraries.txt

sed 's/ \+/,/g' cloud_java_client_library_release_dates_tsv.txt > cloud_java_client_library_release_dates.csv
sed -i '1s/^/version,release_date,artifact_id,service_name\n/' cloud_java_client_library_release_dates.csv

# remove where service match not found
sed -i '/,$/d' cloud_java_client_library_release_dates.csv

echo "Inserting client_library_versions.cloud_java_client_library_release_dates. First 10 lines:"
head  cloud_java_client_library_release_dates.csv
echo "===================="

bq load --skip_leading_rows=1 --project_id=cloud-java-metrics --source_format=CSV --null_marker="-" \
client_library_versions.cloud_java_client_library_release_dates \
cloud_java_client_library_release_dates.csv


rm -f cloud_java_client_library_release_dates_tsv.txt
rm -f cloud_java_client_library_release_dates.csv
rm -f artifacts_to_services.txt
