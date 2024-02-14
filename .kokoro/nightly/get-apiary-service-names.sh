#!/bin/bash

# This script should download
# discovery docs: git@github.com:googleapis/discovery-artifact-manager.git
# apiary repo git@github.com:googleapis/google-api-java-client-services.git
# from discovery docs for each service
# parse artifact-id ("name") and service name ("rootUrl").
# Run this script from repo root dir

git clone https://github.com/googleapis/discovery-artifact-manager.git

cd ./discovery-artifact-manager/discoveries || exit
output_filename="../../libraries-release-data/artifacts_to_services_apiary.txt"


# loop through dicovery jsons
for file in *.json; do
    # Use jq to extract the "name" field (assumes you have jq installed)

    # group_id logic: https://github.com/googleapis/google-api-java-client-services/blob/421c5d6ed56d5eb1257d3fc057d7d6b4fd2f9bb7/generator/src/googleapis/codegen/utilities/maven_utils.py#L50
    # artifact_id logic: https://github.com/googleapis/google-api-java-client-services/blob/421c5d6ed56d5eb1257d3fc057d7d6b4fd2f9bb7/generator/src/googleapis/codegen/utilities/maven_utils.py#L42-L47
    # default_host https://github.com/googleapis/discovery-artifact-manager/blob/9f6638a9950991d4fe67d75bdb539e6d2be20541/google-api-client-generator/src/googleapis/codegen/languages/java/default/templates/___package___/___api_className___.java.tmpl#L44
    artifact_id_suffix=$(jq -r '.name' "$file")
    default_host=$(jq -r '.rootUrl' "$file")
    owner_domain=$(jq -r '.ownerDomain' "$file")

    if [[ "$default_host" =~ ^https:// ]] && [ -n "$artifact_id_suffix" ] && [ -n "$owner_domain" ]; then
      if [[ "$owner_domain" != 'google.com' ]]; then
        echo "$owner_domain =============="
        continue
      fi
      group_id="com.google.apis"
      service_name=$(echo "$default_host" | cut -d'/' -f3 | cut -d'.' -f1)
      artifact_id="google-api-services-${artifact_id_suffix}"
      echo "${group_id},${artifact_id},${service_name}" >> "$output_filename"
    else
        echo "$default_host: Not a valid URL or No 'name' field found in $file"
    fi

done

cd ../..

#rm -rf discovery-artifact-manager/
