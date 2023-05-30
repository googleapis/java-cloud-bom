# This script is run after `update_libraries_bom_variable.py` and parses `google-cloud-bom/pom.xml` for the gapic-libraries-bom and handwritten library versions and updates those versions to `/site/data/variables.yaml`

import xml.etree.ElementTree as ET
import os.path

namespaces = {
    "m": "http://maven.apache.org/POM/4.0.0"
}

# List the dependencies to exclude from the google-cloud-bom pom.xml file
exclude_deps = []

# Parse the XML file
tree = ET.parse('google-cloud-bom/pom.xml')
root = tree.getroot()

# Save the gapic-libraries-bom and handwritten library versions to `/site/data/variables.yaml`
with open(os.path.join('site/data/','variables.yaml'), 'a') as f:
  # Iterate over dependencies in the XML
  for dependency in root.findall(".//m:dependencies/m:dependency", namespaces):
    # Find the artifactId and version elements
    artifactId = dependency.find("m:artifactId", namespaces)
    version = dependency.find("m:version", namespaces)

    if artifactId is not None and version is not None:
      # Skip this dependency if its name is in the exclude list
      if artifactId.text in exclude_deps:
        continue
      # Write the artifactId and version to the output file
      f.write(f'{artifactId.text}: "{artifactId.text}: v{version.text}"\n')
