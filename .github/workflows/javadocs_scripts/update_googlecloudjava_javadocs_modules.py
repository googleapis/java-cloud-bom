# This script parses google-cloud-java/versions.txt and updates google-cloud-java_javadocs_modules.txt

import sys
import re
import os.path

def convert_line(line):
  parts = line.split(':')
  service_name = parts[0]
  javadocs_module = re.sub(r'^google-cloud-', '', service_name)

  # Exceptions for maps modules, grafeas, iam
  if service_name.startswith("google-maps"):
    javadocs_module = re.sub(r'^google-', '', service_name)
  elif service_name.startswith("grafeas"):
    javadocs_module = "grafeas"
  elif service_name.startswith("google-iam-admin"):
    javadocs_module = "iam-admin"
  elif service_name.startswith("google-iam-policy"):
    javadocs_module = "iam"

  artifact_name = f"google-cloud-java/java-{javadocs_module}"
  return f"{service_name} {artifact_name}"

def convert_file(input_file, output_file, exclude_modules):
  output_lines = set()

  # Open the input file for reading
  with open(input_file, 'r') as infile:
    # Process each line in the input file
    for line in infile:
      # Skip lines that start with '#' (commented lines) or blank lines
      if line.startswith('#') or len(line.strip()) == 0:
        continue

      # Skip lines that contain any of the excluded packages
      if any(exclude_module in line for exclude_module in exclude_modules):
        continue

      # Convert the line and add it to the set of output lines
      output_lines.add(convert_line(line))

  # Open the output file for writing
  with open(os.path.join('./site/javadocHelpers/', output_file), 'w') as outfile:
    # Write each unique output line to the output file
    for line in output_lines:
      outfile.write(line + '\n')

# Get the input file from the command line argument
input_file = sys.argv[1]

output_file = 'google-cloud-java_javadocs_modules.txt'

# Excludes lines in versions.txt files that contain any of the following strings. Since we do not want to publish separate Javadocs for `google-cloud-<service>`, `grpc-google-<service>`, and `proto-google-<service>` artifacts, the latter two artifacts are excluded.
exclude_modules = ['google-cloud-java', 'grpc-google', 'proto-google']
convert_file(input_file, output_file, exclude_modules)
