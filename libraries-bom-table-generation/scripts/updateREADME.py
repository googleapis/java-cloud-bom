import yaml
import re

with open('libraries-bom-table-generation/helpers/libraryTable.yaml', 'r') as yaml_file:
  libraryTable = yaml.load(yaml_file, Loader=yaml.SafeLoader)

# Generate Markdown table
table = "| Artifact ID | Library Type | Google Cloud Library Reference | Google Cloud Product Reference | \n"
table += "| --------- | ------------ | ------------ | ------------ |\n"

for item in libraryTable:
  name_pretty = item['namePretty']
  version = item['version']
  gcp_javadocs = item['gcpJavadocs']
  gcp_product_docs = item['gcpProductDocs']
  gcp_productdocs_hyperlink = f"[{name_pretty}]({gcp_javadocs})" if gcp_javadocs != 'N/A' else name_pretty
  gcp_javadocs_hyperlink = f"[{version}]({gcp_product_docs})" if gcp_product_docs != 'N/A' else version
  table += f"| {item['artifact']} | {item['libraryType']} | {gcp_javadocs_hyperlink} | {gcp_productdocs_hyperlink} |\n"

with open('README.md', 'r') as readme_file:
  readme = readme_file.read()


# Update existing table in README.md
table_start_comment = "<!-- TABLE_START -->"
table_end_comment = "<!-- TABLE_END -->"
table_pattern = re.compile(r'(?s)<!-- TABLE_START -->.*?<!-- TABLE_END -->')
updated_readme = table_pattern.sub(table_start_comment + "\n" + table + table_end_comment, readme)

with open('README.md', 'w') as readme_file:
  readme_file.write(updated_readme)