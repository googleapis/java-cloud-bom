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

def convert_file(input_filenames, output_filename, exclude_packages):
  output_lines = set()

  # Process each input file
  for input_filename in input_filenames:
    # Open the input file for reading
    with open(input_filename, 'r') as infile:
      # Skip the first three lines
      for _ in range(3):
        next(infile, None)

      # Process each remaining line in the input file
      for line in infile:
        # Skip lines that contain any of the excluded packages
        if any(exclude_package in line for exclude_package in exclude_packages):
          continue

        # Convert the line and add it to the set of output lines
        output_lines.add(convert_line(line))

  # Open the output file for writing
  with open(os.path.join('.github/workflows/javadocs_scripts/', output_filename), 'w') as outfile:
    # Write each unique output line to the output file
    for line in output_lines:
      outfile.write(line + '\n')

# Get the input file from the command line argument
input_file = sys.argv[1]

output_file = 'google-cloud-java_javadocs_modules.txt'

# Excludes lines in versions.txt files that contain any of the following strings. Since we do not want to publish separate Javadocs for `google-cloud-<service>`, `grpc-google-<service>`, and `proto-google-<service>` artifacts, the latter two packages are excluded.
exclude_packages = ['gapic-generator-java', 'google-cloud-java', 'grpc-google', 'proto-google', 'google-cloud-bom', 'full-convergence-check', 'java-cloud-bom-tests', 'gax-grpc', ]
convert_file([input_file], output_file, exclude_packages)
