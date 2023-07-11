# Parses `google-cloud-bom/pom.xml` for the gapic-libraries-bom and handwritten library versions and updates those versions to `libraries-bom-table-generation/helpers/javaModulesVersions.yaml`

import xml.etree.ElementTree as ET
import os.path

namespaces = {
    "m": "http://maven.apache.org/POM/4.0.0"
}

# List of pom.xml files to process
pom_files = ['google-cloud-bom/pom.xml', 'libraries-bom/pom.xml']

# Save the dependency artifact names and versions to `/site/data/javaModulesVersions.yaml`
with open(os.path.join('libraries-bom-table-generation/helpers/', 'javaModulesVersions.yaml'), 'w') as f:
  f.write("# This file will be updated with the latest versions of each published Java module for a libraries-bom version\n")
  f.write("\n")
  for pom_file in pom_files:
    # Parse the XML file
    tree = ET.parse(pom_file)
    root = tree.getroot()

    # Iterate over dependencies in the XML
    for dependency in root.findall(".//m:dependencies/m:dependency", namespaces):
      # Find the artifactId and version elements
      artifactId = dependency.find("m:artifactId", namespaces)
      version = dependency.find("m:version", namespaces)

      if artifactId is not None and version is not None:
        # Write the artifactId and version to the output file
        f.write(f'{artifactId.text}: "v{version.text}"\n')