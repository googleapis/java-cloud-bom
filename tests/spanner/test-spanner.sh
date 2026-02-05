#!/bin/bash
set -e

SPANNER_VERSION=$(awk '/<artifactId>google-cloud-spanner-bom<\/artifactId>/{f=1;next} /<version>/{if(f){print;exit}}' google-cloud-bom/pom.xml | sed -n 's/.*<version>\(.*\)<\/version>.*/\1/p')
FIRST_PARTY_DEPENDENCIES_VERSION=$(awk '/<artifactId>first-party-dependencies<\/artifactId>/{f=1;next} /<version>/{if(f){print;exit}}' libraries-bom/pom.xml | sed -n 's/.*<version>\(.*\)<\/version>.*/\1/p')

echo "google-cloud-spanner-bom version: $SPANNER_VERSION"
echo "first-party-dependencies version: $FIRST_PARTY_DEPENDENCIES_VERSION"

# Download spanner source code
rm -rf java-spanner
git clone --depth 1 --branch v${SPANNER_VERSION} -c advice.detachedHead=false https://github.com/googleapis/java-spanner.git

# Update the version of sdk-platform-java-config in all pom.xml files
for pom_file in $(find java-spanner -name "pom.xml"); do
  if grep -q "<artifactId>sdk-platform-java-config</artifactId>" "$pom_file"; then
    awk -v version="${FIRST_PARTY_DEPENDENCIES_VERSION}" '
      /<artifactId>sdk-platform-java-config<\/artifactId>/ {
        print
        getline # Read the next line (which should be the version)
        print "    <version>" version "</version>"
        next
      }
      { print }
    ' "$pom_file" > "$pom_file.tmp" && mv "$pom_file.tmp" "$pom_file"
    echo "Updated: $pom_file"
  fi
done

# Show the diffs
echo "Changes after version updates:"
(cd java-spanner && git add .)
(cd java-spanner && git --no-pager diff --staged)

# Clean up backup files
find java-spanner -name "pom.xml.bak" -delete

(cd java-spanner && JOB_TYPE=test .kokoro/build.sh)