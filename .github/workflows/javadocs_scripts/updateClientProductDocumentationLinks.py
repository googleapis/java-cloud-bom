import os
import json

# Initial empty dictionaries
product_documentation_dict = {}
client_documentation_dict = {}
api_namepretty_dict = {}

# Traverse the entire directory tree
for root, dirs, files in os.walk('.'):
  for file in files:
    if file.endswith('.repo-metadata.json'):
      with open(os.path.join(root, file), 'r') as f:
        data = json.load(f)

        # Ensure the necessary keys are in the loaded json data
        if "distribution_name" in data and "product_documentation" in data:
          # Split at the colon and take the part after the colon
          distribution_name = data['distribution_name'].split(':')[-1].strip()
          product_documentation_dict[distribution_name] = data['product_documentation']

        if "distribution_name" in data and "client_documentation" in data:
          # Split at the colon and take the part after the colon
          distribution_name = data['distribution_name'].split(':')[-1].strip()
          client_documentation_dict[distribution_name] = data['client_documentation']

        if "distribution_name" in data and "name_pretty" in data:
        # Split at the colon and take the part after the colon
          distribution_name = data['distribution_name'].split(':')[-1].strip()
          api_namepretty_dict[distribution_name] = data['name_pretty']

# Write to yaml files manually
with open(os.path.join('site/data/','variableProductReferenceLinks.yaml'), 'w') as f:
  for k, v in product_documentation_dict.items():
    f.write(f"{k}: \"{v}\"\n")

with open(os.path.join('site/data/','variableLibraryReferenceLinks.yaml'), 'w') as f:
  for k, v in client_documentation_dict.items():
    f.write(f"{k}: \"{v}\"\n")

with open(os.path.join('site/data/','name_pretty.yaml'), 'w') as f:
  for k, v in api_namepretty_dict.items():
    f.write(f"{k}: \"{v}\"\n")
