#!/bin/bash

# Output:
# The script generates cloud_java_client_library_release_dates.csv that holds the data defined below.
# It has artifact_id,service_name,version, and release_date columns.
# this csv file will be uploaded to (project) cloud-java-metrics.(dataset) client_library_versions. (table) cloud_java_client_library_release_dates
# using bq load command

# Fail on any error.
set -e
# Display commands being run.
#set -x

cd github/java-cloud-bom/libraries-release-data

# M2_HOME is not used since Maven 3.5.0 https://maven.apache.org/docs/3.5.0/release-notes.html
mvn -B clean install

list=$(mvn -B exec:java -Dexec.mainClass="com.google.cloud.dashboard.GenerateLibrariesList")


final_list=$(echo $list | grep -o 'com.google.cloud[^,]*' | tr '\n' ',' | sed 's/,$//')


echo ${final_list} | tr ',' '\n' > unfiltered-libraries.txt
sed -i '/libraries-release-data/d' unfiltered-libraries.txt
sort unfiltered-libraries.txt | uniq > libraries.txt
rm -f unfiltered-libraries.txt


cd github/java-cloud-bom


cat libraries-release-data/libraries.txt | while read line; do

  group_id=${line%:*}
  artifact_id=${line#*:}
  new_group_id="${group_id//.//}"
  service_name=${artifact_id#*-cloud-}

  if [[ "${artifact_id}" == *storage* ]]; then
    service_name=bigstore
  fi

  URL=https://repo1.maven.org/maven2/$new_group_id/$artifact_id

  .kokoro/nightly/fetch-library-data.sh $URL $artifact_id $service_name

done

rm -f libraries-release-data/libraries.txt

sed 's/ \+/,/g' cloud_java_client_library_release_dates_tsv.txt > ./cloud_java_client_library_release_dates.csv
sed -i '1s/^/version,release_date,artifact_id,service_name\n/' ./cloud_java_client_library_release_dates.csv

echo "Inserting client_library_versions.cloud_java_client_library_release_dates. First 10 lines:"
head  ./cloud_java_client_library_release_dates.csv
echo "===================="

bq load --autodetect --project_id=cloud-java-metrics --source_format=CSV \
client_library_versions.cloud_java_client_library_release_dates \
cloud_java_client_library_release_dates.csv


rm -f cloud_java_client_library_release_dates_tsv.txt
rm -f cloud_java_client_library_release_dates.csv
