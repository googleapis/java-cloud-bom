#!/bin/bash

# This script should download
# discovery docs: git@github.com:googleapis/discovery-artifact-manager.git
# apiary repo git@github.com:googleapis/google-api-java-client-services.git
# from discovery docs for each service
# parse artifact-id ("name") and service name ("rootUrl").

# Run this script from repo root dir
# input: N/A
# output: txt file with comma separated group_id, artifact_id, service_name.

git clone https://github.com/googleapis/discovery-artifact-manager.git

cd ./discovery-artifact-manager/discoveries || exit
output_filename="../../libraries-release-data/artifacts_to_services_apiary.txt"

# install jq to extract info from JSON data
sudo apt-get update
sudo apt-get install -q -y jq

total_files_processed=0
successful_outputs=0
# loop through dicovery json files
for file in *.json; do
    ((total_files_processed++))
    # Use jq to extract the "name" field

    # group_id logic: https://github.com/googleapis/google-api-java-client-services/blob/421c5d6ed56d5eb1257d3fc057d7d6b4fd2f9bb7/generator/src/googleapis/codegen/utilities/maven_utils.py#L50
    # artifact_id logic: https://github.com/googleapis/google-api-java-client-services/blob/421c5d6ed56d5eb1257d3fc057d7d6b4fd2f9bb7/generator/src/googleapis/codegen/utilities/maven_utils.py#L42-L47
    # default_host https://github.com/googleapis/discovery-artifact-manager/blob/9f6638a9950991d4fe67d75bdb539e6d2be20541/google-api-client-generator/src/googleapis/codegen/languages/java/default/templates/___package___/___api_className___.java.tmpl#L44
    artifact_id_suffix=$(jq -r '.name' "$file")
    default_host=$(jq -r '.rootUrl' "$file")
    owner_domain=$(jq -r '.ownerDomain' "$file")

    # Check if jq failed to extract essential fields
    if [ -z "$artifact_id_suffix" ] || [ -z "$default_host" ] || [ -z "$owner_domain" ]; then
      echo "Error: Could not extract 'name', 'rootUrl', or 'ownerDomain' from $file. Skipping."
      continue
    fi
    if [[ "$owner_domain" != 'google.com' ]]; then
      echo "Info: '$owner_domain' in $file is not 'google.com'. Skipping."
      continue
    fi
    group_id="com.google.apis"
    if [[ "$default_host" == "https://www.googleapis.com/" ]]; then
      echo "rootUrl is https://www.googleapis.com/, use name ${service_name} as service_name"
      service_name="$artifact_id_suffix"
    else
      service_name_parts=$(echo "$default_host" | cut -d'/' -f3 | cut -d'.' -f1 2>/dev/null)
      if [ -z "$service_name_parts" ]; then
        echo "Error: Could not extract 'service_name' from 'rootUrl' ('$default_host') in $file. Skipping."
        continue
      fi
      service_name="$service_name_parts"
    fi
    artifact_id="google-api-services-${artifact_id_suffix}"
    echo "${group_id},${artifact_id},${service_name}" >> "$output_filename"
    ((successful_outputs++))

done

echo "Processing complete."
echo "Total files processed: $total_files_processed"
echo "Successful outputs recorded: $successful_outputs"

cd ../..

rm -rf discovery-artifact-manager/
