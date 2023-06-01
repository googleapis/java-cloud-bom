# This script parses versions.txt to update the version of libraries-bom within `/site/data/variables.yaml`
# This script kicks off the version update process.

import os.path

def convert_line(line):
  # Split the line by ':'
  parts = line.strip().split(':')

  # Construct the output string
  output_line = parts[0] + ': "' + parts[0] + ': v' + parts[1] + '"'

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
  with open(os.path.join('./site/data/',output_filename), 'w') as outfile:
    # Write the two comment lines at the top of the file
    outfile.write("# This file will be updated with the latest versions and release dates of the various modules\n")
    outfile.write("\n")

    # Write each unique output line to the output file
    for line in output_lines:
      outfile.write(line + '\n')


# Test the function
input_files = ['./versions.txt']
output_file = 'variables.yaml'

# Excludes lines in versions.txt files that contain any of the following strings. Since we do not want to publish separate Javadocs for `google-cloud-<service>`, `grpc-google-<service>`, and `proto-google-<service>` artifacts, the latter two packages are excluded.
exclude_packages = ['gapic-generator-java', 'google-cloud-java', 'grpc-google-cloud', 'proto-google-cloud', 'google-cloud-bom', 'full-convergence-check', 'java-cloud-bom-tests', 'gax-httpjson', 'google-cloud-shared-dependencies']
convert_file(input_files, output_file, exclude_packages)