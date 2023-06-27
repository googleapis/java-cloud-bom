# Run after parse_pom.py to update the config.toml file with the libraries-bom version

import yaml
import toml

def main():
  # Load the javaModulesVersions.yaml file
  with open('./site/data/javaModulesVersions.yaml') as file:
    variables = yaml.full_load(file)

  # Load the config.toml file
  with open('./site/config.toml', 'r') as file:
    config = toml.load(file)

  # Update `libraries_bom_version`
  config['params']['libraries_bom_version'] = variables['libraries-bom']

# Write the updated data back to the config.toml file
  with open('./site/config.toml', 'w') as file:
    toml.dump(config, file)

if __name__ == "__main__":
  main()
