import os
import json

#
product_documentation_dict = {}
client_documentation_dict = {}
api_namepretty_dict = {}

# Traverse the entire directory tree for google-cloud-java
for root, dirs, files in os.walk(''):
  for file in files:
    if file.endswith('.repo-metadata.json'):
      with open(os.path.join(root, file), 'r') as f:
        data = json.load(f)

        # Only update if the link or name has changed
        if "distribution_name" in data and "product_documentation" in data:
          distribution_name = data['distribution_name'].split(':')[-1].strip()
          if distribution_name not in product_documentation_dict or product_documentation_dict[distribution_name] != data['product_documentation']:
            product_documentation_dict[distribution_name] = data['product_documentation']

        if "distribution_name" in data and "client_documentation" in data:
          distribution_name = data['distribution_name'].split(':')[-1].strip()
          if distribution_name not in client_documentation_dict or client_documentation_dict[distribution_name] != data['client_documentation']:
            client_documentation_dict[distribution_name] = data['client_documentation']

        if "distribution_name" in data and "name_pretty" in data:
          distribution_name = data['distribution_name'].split(':')[-1].strip()
          if distribution_name not in api_namepretty_dict or api_namepretty_dict[distribution_name] != data['name_pretty']:
            api_namepretty_dict[distribution_name] = data['name_pretty']

# File path and data dictionary mapping
file_data_mapping = {
    'libraries-bom-table-generation/helpers/javaModulesProductReferenceLinks.yaml': product_documentation_dict,
    'libraries-bom-table-generation/helpers/javaModulesLibraryReferenceLinks.yaml': client_documentation_dict,
    'libraries-bom-table-generation/helpers/javaModulesNamePretty.yaml': api_namepretty_dict
}

# Update existing files with new data
for file_path, data_dict in file_data_mapping.items():
  updated_lines = []
  if os.path.exists(file_path):
    with open(file_path, 'r') as f:
      existing_lines = f.readlines()

    for line in existing_lines:
      key = line.split(':')[0].strip()
      if key in data_dict and line.strip() != f"{key}: \"{data_dict[key]}\"":
        updated_lines.append(f"{key}: \"{data_dict[key]}\"\n")
      else:
        updated_lines.append(line)
    else:
      updated_lines = [f"{k}: \"{v}\"\n" for k, v in data_dict.items()]

  with open(file_path, 'w') as f:
    f.writelines(updated_lines)