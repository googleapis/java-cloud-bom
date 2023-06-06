# This script seeds the initial modules for the libraries-bom.md file

import yaml
from jinja2 import Environment, FileSystemLoader

# Load the yaml file
with open('site/data/variables.yaml', 'r') as f:
  data = yaml.safe_load(f)

# Template for modules that have a Product page
markdown_template = """
### {{key}}
- {% raw %}{{< variable {% endraw %}"{{key}}" >{% raw %}}}{% endraw %}([Javadocs]({% raw %}{{< variable "github-repo" >{% endraw %}{% raw %}}}{% endraw %}/{{key}}/), [Google Cloud Java Library Reference]({% raw %}{{< variableLibraryReferenceLinks{% endraw %} "{{key}}" >{% raw %}}}{% endraw %}))
- [Google Cloud Product Reference]({% raw %}{{< variableProductReferenceLinks {% endraw %}"{{key}}" >{% raw %}}}{% endraw %})
"""

# Template for modules that do not have a Product page
markdown_template_no_product = """
### {{key}}
- {% raw %}{{< variable {% endraw %}"{{key}}" >{% raw %}}}{% endraw %}([Javadocs]({% raw %}{{< variable "github-repo" >{% endraw %}{% raw %}}}{% endraw %}/{{key}}/), [Google Cloud Java Library Reference]({% raw %}{{< variableLibraryReferenceLinks{% endraw %} "{{key}}" >{% raw %}}}{% endraw %}))
"""

env = Environment(loader=FileSystemLoader('.'))
template_with_product = env.from_string(markdown_template)
template_without_product = env.from_string(markdown_template_no_product)

# Initialize the final output as an empty string
output = ''

# List of modules to ignore
modules_to_ignore = ['libraries-bom', 'gapic-libraries-bom','github-repo','gax-httpjson', 'google-cloud-shared-dependencies', 'first-party-dependencies']

# List of modules that do not have a Product page
modules_without_product = ['gax', 'google-iam-policy', 'google-cloud-core', 'api-common']

# For each key-value pair in the sorted keys of the YAML file, render the template and add it to the output
for key in sorted(data.keys()):
  if key not in modules_to_ignore and key in modules_without_product:
    output += template_without_product.render(key=key, value=data[key])
  if key not in modules_to_ignore and key not in modules_without_product:
    output += template_with_product.render(key=key, value=data[key])

# Write to markdown file
with open('output.md', 'w') as f:
  f.write(output)

