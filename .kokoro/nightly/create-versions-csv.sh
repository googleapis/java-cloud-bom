#!/bin/bash

# Output:
# The script generates cloud_java_client_library_release_dates.csv that holds the data defined below.
# It has artifact_id,service_name,version, and release_date columns.
# this csv file will be uploaded to (project) cloud-java-metrics.(dataset) client_library_versions. (table) cloud_java_client_library_release_dates
# using bq load command

# Display commands being run.
set -x

cd github

git clone https://github.com/googleapis/google-cloud-java.git

cd google-cloud-java

for module in $(find . -mindepth 2 -maxdepth 2 -name pom.xml | sort | xargs dirname); do

  cd ${module}

  string=$(find . -name '*StubSettings.java' -print | xargs grep -m 1 '.googleapis.com:443')

  service_name_raw=$(echo ${string} | grep -o '".*"' | tr -d '"' | cut -d "." -f 1 | cut -d "-" -f 1)

  artifact_id=$(grep -m 1 "<artifactId>" pom.xml | sed -n 's/.*<artifactId>\(.*\)<\/artifactId>.*/\1/p' | sed 's/-parent$//')

  cd ..

  echo "${artifact_id}:${service_name_raw}">>service_names.txt

  done

cd ../java-cloud-bom

cat ../google-cloud-java/service_names.txt

#
mvn -B clean install

cd libraries-release-data

mvn compile

list=$(mvn -B exec:java -Dexec.mainClass="com.google.cloud.dashboard.GenerateLibrariesList")


final_list=$(echo $list | grep -o 'com.google.cloud[^,]*' | tr '\n' ',' | sed 's/,$//')


echo ${final_list} | tr ',' '\n' > unfiltered-libraries.txt
sed -i '/libraries-release-data/d' unfiltered-libraries.txt
sort unfiltered-libraries.txt | uniq > libraries.txt
rm -f unfiltered-libraries.txt


cat libraries.txt | while read line; do

  group_id=${line%:*}
  artifact_id=${line#*:}
  new_group_id="${group_id//.//}"
  service_name=$(cat ../../google-cloud-java/service_names.txt | grep ${artifact_id} | cut -d ":" -f 2)

  echo "the service_name is ${service_name}"

  if [[ "${artifact_id}" == *storage* ]]; then
    service_name=bigstore
  fi

  URL=https://repo1.maven.org/maven2/$new_group_id/$artifact_id

  ../.kokoro/nightly/fetch-library-data.sh $URL $artifact_id $service_name

done

rm -f libraries.txt

sed 's/ \+/,/g' cloud_java_client_library_release_dates_tsv.txt > cloud_java_client_library_release_dates.csv
sed -i '1s/^/version,release_date,artifact_id,service_name\n/' cloud_java_client_library_release_dates.csv

cat cloud_java_client_library_release_dates.csv

echo "Inserting client_library_versions.cloud_java_client_library_release_dates. First 10 lines:"
head  cloud_java_client_library_release_dates.csv
echo "===================="

#bq load --autodetect --project_id=cloud-java-metrics --source_format=CSV \
#client_library_versions.cloud_java_client_library_release_dates \
#cloud_java_client_library_release_dates.csv


rm -f cloud_java_client_library_release_dates_tsv.txt
rm -f cloud_java_client_library_release_dates.csv
