# This script parses the versions.txt file within sdk-platform-java and google-cloud-java to update `/site/data/variables.yaml`

import os.path

def convert_line(line):
  # Split the line by ':'
  parts = line.strip().split(':')

  # Construct the output string
  output_line = parts[0] + ': "' + 'v.' + parts[1] + '"'

  return output_line

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
  with open(os.path.join('./site/data/',output_filename), 'a') as outfile:
    # Write each unique output line to the output file
    for line in output_lines:
      outfile.write(line + '\n')


# Test the function
input_files = ['google-cloud-java/versions.txt','sdk-platform-java/versions.txt']
output_file = 'variables.yaml'

# Excludes lines in versions.txt files that contain any of the following strings. Since we do not want to publish separate Javadocs for `google-cloud-<service>`, `grpc-google-<service>`, and `proto-google-<service>` artifacts, the latter two packages are excluded.
exclude_packages = ['gapic-generator-java', 'google-cloud-java', 'grpc-google', 'proto-google', 'google-cloud-bom', 'full-convergence-check', 'java-cloud-bom-tests','google-cloud-core']
convert_file(input_files, output_file, exclude_packages)