# This script recreates the table of modules for the site

import yaml
import re

# Load the yaml files
with open('libraries-bom-table-generation/helpers/javaModulesVersions.yaml', 'r') as f:
  javaModuleList = yaml.safe_load(f)

with open('libraries-bom-table-generation/helpers/javaModulesLibraryReferenceLinks.yaml', 'r') as f:
  libraryReference = yaml.safe_load(f)

with open('libraries-bom-table-generation/helpers/javaModulesProductReferenceLinks.yaml', 'r') as f:
  productReference = yaml.safe_load(f)

with open('libraries-bom-table-generation/helpers/javaModulesNamePretty.yaml', 'r') as f:
  nameReference = yaml.safe_load(f)

# Get list of runtime_modules from sdk-platform-java_javadocs_modules.txt
def get_runtime_modules(filename):
  runtime_modules = []

  # Open the text file
  with open(filename, 'r') as f:
    # Iterate over each line in the file
    for line in f:
      # Split the line into two parts and keep only the first part
      module = line.split()[0]

      # Append the module to the list
      runtime_modules.append(module)

  # Return the list of modules
  return runtime_modules

# Usage
filename = 'libraries-bom-table-generation/helpers/sdk-platform-java_javadocs_modules.txt'
runtime_modules = get_runtime_modules(filename)

# List of modules to ignore
modules_to_skip = ['libraries-bom', 'gapic-libraries-bom','github-repo','gax-httpjson', 'google-cloud-shared-dependencies', 'first-party-dependencies', 'full-convergence-check', 'gapic-generator-java', 'java-cloud-bom-tests', 'gax-grpc', 'grpc-google-', 'proto-google-', 'google-cloud-bom', 'google-cloud-java', 'google-java-format']
regex_modules_to_skip = "(" + ")|(".join(modules_to_skip) + ")"

# List of modules with special artifact names
modules_name_exceptions = ['google-cloud-bigquerystorage-bom', 'google-cloud-bigtable-bom','google-cloud-datastore-bom', 'google-cloud-firestore-bom','google-cloud-logging-bom', 'google-cloud-pubsub-bom', 'google-cloud-pubsublite-bom', 'google-cloud-spanner-bom', 'google-cloud-storage-bom']

# Prepare the output list
output = []

# For each key-value pair in the input yaml file
for module, version in javaModuleList.items():
  # If the module is in the list of modules to skip, continue to next iteration
  if re.match(regex_modules_to_skip, module):
    continue

  if module in runtime_modules:
    # Create a new dictionary for this entry

    new_entry = {
        'artifact': module,
        'version': version,
        'libraryType': f"Runtime",
        'gcpJavadocs': libraryReference.get(module, "N/A"),
        'gcpProductDocs': productReference.get(module, "N/A"),
        'namePretty': nameReference.get(module, "N/A")
    }
  else:
    if module in modules_name_exceptions:
      moduleLookup = module[:-4]
      new_entry = {
          'artifact': module,
          'version': version,
          'libraryType': f"Product",
          'gcpJavadocs': libraryReference.get(moduleLookup, "N/A"),
          'gcpProductDocs': productReference.get(moduleLookup, "N/A"),
          'namePretty': nameReference.get(moduleLookup, "N/A")
      }
    else:
      new_entry = {
          'artifact': module,
          'version': version,
          'libraryType': f"Product",
          'gcpJavadocs': libraryReference.get(module, "N/A"),
          'gcpProductDocs': productReference.get(module, "N/A"),
          'namePretty': nameReference.get(module, "N/A")
      }

  # Add the new dictionary to the output list
  output.append(new_entry)

# Alphabetize the output list by the 'artifact' key
output = sorted(output, key=lambda k: k['artifact'])

# Write the output list to a new yaml file
with open('libraries-bom-table-generation/helpers/libraryTable.yaml', 'w') as f:
  yaml.dump(output, f)