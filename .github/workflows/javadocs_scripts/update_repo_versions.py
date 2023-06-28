import yaml
import fileinput

# Updates the versions of the sdk-platform-java, google-cloud-java, and handwritten libraries for checkout
def update_repo_versions(file_paths, yaml_file):
  with open(yaml_file, 'r') as yaml_data:
    version_data = yaml.safe_load(yaml_data)

  for file_path in file_paths:
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

file_paths = ['./site/javadocHelpers/repos_for_versionstxt.txt', './site/javadocHelpers/handwritten_libraries_javadocs_modules.txt']
yaml_file = './site/data/javaModulesVersions.yaml'

update_repo_versions(file_paths, yaml_file)
