# This script seeds the initial modules for the libraries-bom.md file

import yaml
from jinja2 import Environment, FileSystemLoader

# Load the yaml file
with open('site/data/variables.yaml', 'r') as f:
  data = yaml.safe_load(f)

# Prepare the markdown template
markdown_template = """
### {{key}}
- {% raw %}{{< variable {% endraw %}"{{key}}" >{% raw %}}}{% endraw %}([Javadocs]({% raw %}{{< variable "github-repo" >{% endraw %}{% raw %}}}{% endraw %}/{{key}}/), [Google Cloud Java Library Reference]({% raw %}{{< variableLibraryReferenceLinks{% endraw %} "{{key}}" >{% raw %}}}{% endraw %}))
- [Google Cloud Product Reference]({% raw %}{{< variableProductReferenceLinks {% endraw %}"{{key}}" >{% raw %}}}{% endraw %})
"""

# Set up Jinja environment
env = Environment(loader=FileSystemLoader('.'))
template = env.from_string(markdown_template)

# Initialize the final output as an empty string
output = ''

# List of keys to ignore
keys_to_ignore = ['libraries-bom', 'gapic-libraries-bom','github-repo','gax-httpjson', 'google-cloud-shared-dependencies', 'first-party-dependencies']

# For each key-value pair in the YAML file, render the template and add it to the output
for key, value in data.items():
  if key not in keys_to_ignore:
    output += template.render(key=key, value=value)

# Write to markdown file
with open('output.md', 'w') as f:
  f.write(output)

