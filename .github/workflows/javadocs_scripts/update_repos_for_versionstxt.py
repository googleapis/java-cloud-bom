import yaml
import fileinput

# This script updates the versions of the sdk-platform-java and google-cloud-java for checkout
def update_repos_for_versionstxt(file_path, yaml_file):
  with open(yaml_file, 'r') as yaml_data:
    version_data = yaml.safe_load(yaml_data)

  for line in fileinput.input(file_path, inplace=True):
    parts = line.strip().split(' ')
    if len(parts) == 4:
      repo, directory, tag, artifact_id = parts
      if artifact_id in version_data:
        version = version_data[artifact_id]
        # exception for sdk-platform-java to use google-cloud-shared-dependencies tag
        if artifact_id == "first-party-dependencies":
          version = 'google-cloud-shared-dependencies/' + version
        updated_line = f"{repo} {directory} {version} {artifact_id}\n"
        print(updated_line, end='')
      else:
        print(line, end='')
    else:
      print(line, end='')

# Example usage
file_path = '.github/workflows/javadocs_scripts/repos_for_versionstxt.txt'
yaml_file = './site/data/variables.yaml'

update_repos_for_versionstxt(file_path, yaml_file)
