# Parses sdk-platform-java/gapic-generator-parent-bom/pom.xml for auth library version

import sys
import xml.etree.ElementTree as ET
import os.path

ns = {
    "maven": "http://maven.apache.org/POM/4.0.0"
}

# Get the input file from the command line argument
input_file = sys.argv[1]

# Parse the XML file
tree = ET.parse(input_file)
root = tree.getroot()

# Find the google-auth.version property
property_element = root.find("./maven:properties/maven:google.auth.version", ns)
google_auth_version = property_element.text

# Save the auth library artifact and version to `/site/data/javaModulesVersions.yaml`
with open(os.path.join('./site/javadocHelpers/', 'javaModulesVersions.yaml'), 'a') as f:
  f.write(f'google-auth-library: "v{google_auth_version}"\n')

# Save the auth library artifact and version to handwritten_libraries_javadocs_modules.txt
output_file = "site/javadocHelpers/handwritten_libraries_javadocs_modules.txt"

with open(output_file, 'a') as f:
  # Update the line in-place
  updated_line = f'googleapis/google-auth-library-java google-auth-library-java v{google_auth_version} google-auth-library\n'

  # Read the contents of the output file
  with open(output_file, 'r') as f:
    lines = f.readlines()

  # Find the line to update
  for i, line in enumerate(lines):
    if line.startswith("googleapis/google-auth-library-java"):
      lines[i] = updated_line
      break

  # Write the updated contents back to the output file
  with open(output_file, 'w') as f:
    f.writelines(lines)