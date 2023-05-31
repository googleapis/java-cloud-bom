import os

file_path = '../../../site/data/variables.yaml'

# Path to create directories
base_path = '../../../site/static/'  # Replace with the actual base path

# Read the text file
with open(file_path, 'r') as file:
  lines = file.readlines()

# Create directories
for line in lines:
  line = line.strip()
  if ':' in line:
    directory_name = line.split(':')[0].strip()
    directory_path = os.path.join(base_path, directory_name)
    os.makedirs(directory_path, exist_ok=True)
    print(f"Created directory: {directory_path}")