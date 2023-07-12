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

# Manual list of runtime modules
runtime_modules = ['google-http-client',
                   'gax',
                   'api-common',
                   'google-cloud-core',
                   'google-iam-policy'
                   ]


# List of modules to ignore for table creation
modules_to_skip = ['libraries-bom',
                   'gapic-libraries-bom',
                   'github-repo',
                   'gax-httpjson',
                   'google-cloud-shared-dependencies',
                   'first-party-dependencies',
                   'full-convergence-check',
                   'gapic-generator-java',
                   'java-cloud-bom-tests',
                   'gax-grpc',
                   'grpc-google-',
                   'proto-google-',
                   'google-cloud-bom',
                   'google-cloud-java',
                   'google-java-format',
                   'google-api-client',
                   'google-http-client-',
                   'grpc-',
                   'protobuf-',
                   'guava',
                   'protoc',
                   'gson',
                   'google-oauth',
                   'google-auth',
                   'google-cloud-bigtable-emulator',
                   'auto-value-annotations',
                   'gapic-google-cloud-storage-v2',
                   'google-cloud-core-grpc',
                   'google-cloud-core-http',
                   'google-cloud-firestore-admin',
                   'google-cloud-spanner-executor'
                   ]
regex_modules_to_skip = "(" + ")|(".join(modules_to_skip) + ")"

# Prepare the output list
output = []

# Custom entry for auth library as the flattened dependencies need to be aggregated
auth_entry = {
    'artifact': 'google-auth-library',
    'version': javaModuleList.get('google-auth-library-credentials'),
    'libraryType': f"Runtime",
    'gcpJavadocs': libraryReference.get('google-auth-library', "N/A"),
    'gcpProductDocs': productReference.get('google-auth-library', "N/A"),
    'namePretty': nameReference.get('google-auth-library', "N/A")
}

output.append(auth_entry)

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