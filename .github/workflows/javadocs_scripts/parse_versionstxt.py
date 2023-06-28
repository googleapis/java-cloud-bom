# This script kicks off the version update process by parsing the java-cloud-bom/versions.txt to update the version of libraries-bom within `/site/data/javaModulesVersions.yaml`
# This script is later also used to parse the sdk-platform-java and google-cloud-java versions.txt files to update the modules' versions within `/site/data/javaModulesVersions.yaml`

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

  # If "libraries-bom" is in the output_lines then create new file. Otherwise, append to existing file
  if any("libraries-bom" in line for line in output_lines):
    mode = 'w'
  else:
    mode = 'a'

  # Open the output file for writing
  with open(os.path.join('./site/javadocHelpers/',output_filename), mode) as outfile:
    # Write the two comment lines at the top of the file
    if(mode == 'w'):
      outfile.write("# This file will be updated with the latest versions of each published Java module\n")
      outfile.write("\n")

    # Write each unique output line to the output file
    for line in output_lines:
      outfile.write(line + '\n')

input_file = sys.argv[1]
output_file = 'javaModulesVersions.yaml'

convert_file(input_file, output_file)