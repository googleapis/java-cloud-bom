# Parses the sdk-platform-java and google-cloud-java versions.txt files to update the modules' versions for `libraries-bom-table-generation/helpers/javaModulesVersions.yaml`

import os.path
import sys

def convert_line(line):
  # Split the line by ':'
  parts = line.strip().split(':')

  # Construct the output string
  output_line = parts[0] + ': "' + 'v' + parts[1] + '"'

  return output_line

def convert_file(input_filename, output_filename):
  output_lines = set()

  # Open the input file for reading
  with open(input_filename, 'r') as infile:
    # Process each line in the input file
    for line in infile:
      # Skip lines that start with '#' (commented lines) or blank lines
      if line.startswith('#') or len(line.strip()) == 0:
        continue

      # Convert the line and add it to the set of output lines
      output_lines.add(convert_line(line))

  # Open the output file for writing
  with open(os.path.join('libraries-bom-table-generation/helpers/',output_filename), 'a') as f:
    for line in output_lines:
      f.write(line + '\n')

input_file = sys.argv[1]
output_file = 'javaModulesVersions.yaml'

convert_file(input_file, output_file)