# Parses `libraries-bom/.flattened-pom.xml` for all dependencies and updates those versions to `libraries-bom-table-generation/helpers/javaModulesVersions.yaml`

import xml.etree.ElementTree as ET
import os.path

namespaces = {
    "m": "http://maven.apache.org/POM/4.0.0"
}

pom_file = 'libraries-bom/.flattened-pom.xml'

# Save the dependency artifact names and versions to `libraries-bom-table-generation/helpers/javaModulesVersions.yaml`
with open(os.path.join('libraries-bom-table-generation/helpers/', 'javaModulesVersions.yaml'), 'w') as f:
  f.write("# This file will be updated with the latest versions of each published Java module for a libraries-bom version\n")
  f.write("\n")
  # Parse the XML file
  tree = ET.parse(pom_file)
  root = tree.getroot()

  # Set to store unique dependencies
  dependencies = set()

  # Iterate over dependencies in the XML
  for dependency in root.findall(".//m:dependencies/m:dependency", namespaces):
    # Find the artifactId and version elements
    artifactId = dependency.find("m:artifactId", namespaces)
    version = dependency.find("m:version", namespaces)

    if artifactId is not None and version is not None:
      # Generate a unique key for each dependency using artifactId and version
      dependency_key = f'{artifactId.text}:{version.text}'

      # Check if the dependency is already encountered
      if dependency_key not in dependencies:
        # Write the artifactId and version to the output file
        f.write(f'{artifactId.text}: "v{version.text}"\n')

        # Add the dependency to the set
        dependencies.add(dependency_key)