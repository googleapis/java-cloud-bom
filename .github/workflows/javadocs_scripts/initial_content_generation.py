# This script seeds the initial modules for the libraries-bom.md file
import yaml
from jinja2 import Environment, FileSystemLoader

# Load the yaml file
with open('site/data/variables.yaml', 'r') as f:
  data = yaml.safe_load(f)

# Prepare the markdown template
markdown_template = """
### [{{key}}]({% raw %}{{< variable "github-repo" >}}{% endraw %}/{{key}}/)
{% raw %}{{< variable {% endraw %}"{{key}}" >{% raw %}}}{% endraw %}

[Google Cloud Docs](https://cloud.google.com/java/docs/reference/{{key}}/latest/overview)

"""

# Set up Jinja environment
env = Environment(loader=FileSystemLoader('.'))
template = env.from_string(markdown_template)

# Initialize the final output as an empty string
output = ''

# For each key-value pair in the YAML file, render the template and add it to the output
for key, value in data.items():
  output += template.render(key=key, value=value)

# Write to markdown file
with open('output.md', 'w') as f:
  f.write(output)
