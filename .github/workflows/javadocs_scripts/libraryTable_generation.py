# This script recreates the table of modules for the site

import yaml

# Load the yaml files
with open('site/data/variables.yaml', 'r') as f:
  data = yaml.safe_load(f)

with open('site/data/variableLibraryReferenceLinks.yaml', 'r') as f:
  libraryReference = yaml.safe_load(f)

with open('site/data/variableProductReferenceLinks.yaml', 'r') as f:
  productReference = yaml.safe_load(f)

with open('site/data/name_pretty.yaml', 'r') as f:
  nameReference = yaml.safe_load(f)


# List of modules to ignore
variables_to_skip = ['libraries-bom', 'gapic-libraries-bom','github-repo','gax-httpjson', 'google-cloud-shared-dependencies', 'first-party-dependencies']

# List of runtime modules
runtime_modules = ['google-cloud-core', 'gax', 'google-iam-policy', 'api-common']

# List of modules with special artifact names
variable_name_exceptions = ['google-cloud-bigquerystorage-bom', 'google-cloud-bigtable-bom','google-cloud-datastore-bom', 'google-cloud-firestore-bom','google-cloud-logging-bom', 'google-cloud-pubsub-bom', 'google-cloud-pubsublite-bom', 'google-cloud-spanner-bom', 'google-cloud-storage-bom']

# Prepare the output list
output = []

# For each key-value pair in the input yaml file
for variable, value in data.items():
  # If the variable is in the list of variables to skip, continue to next iteration
  if variable in variables_to_skip:
    continue

  if variable in runtime_modules:
    # Create a new dictionary for this entry
    # @TODO: Update standardJavadocs link once this is pushed to main
    new_entry = {
        'artifact': variable,
        'version': value,
        'libraryType': f"Runtime",
        'standardJavadocs': f"https://alicejli.github.io/java-cloud-bom/{variable}",
        'GCPJavadocs': libraryReference.get(variable, "N/A"),
        'GCPProductDocs': productReference.get(variable, "N/A"),
        'name_pretty': nameReference.get(variable, "N/A")
    }
  else:
    if variable in variable_name_exceptions:
      variableLookup = variable[:-4]
      new_entry = {
          'artifact': variable,
          'version': value,
          'libraryType': f"Product",
          'standardJavadocs': f"https://alicejli.github.io/java-cloud-bom/{variable}",
          'GCPJavadocs': libraryReference.get(variableLookup, "N/A"),
          'GCPProductDocs': productReference.get(variableLookup, "N/A"),
          'name_pretty': nameReference.get(variableLookup, "N/A")
      }
    else:
      new_entry = {
          'artifact': variable,
          'version': value,
          'libraryType': f"Product",
          'standardJavadocs': f"https://alicejli.github.io/java-cloud-bom/{variable}",
          'GCPJavadocs': libraryReference.get(variable, "N/A"),
          'GCPProductDocs': productReference.get(variable, "N/A"),
          'name_pretty': nameReference.get(variable, "N/A")
      }

  # Add the new dictionary to the output list
  output.append(new_entry)

  # Alphabetize the output list by the 'artifact' key
  output = sorted(output, key=lambda k: k['artifact'])

# Write the output list to a new yaml file
with open('site/data/libraryTable.yaml', 'w') as f:
  yaml.dump(output, f)