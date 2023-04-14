# Validate BOM GitHub Action

This composite action validates a BOM specified as argument.

This action performs the following steps:

- It reads the BOM and get all artifacts.
  - It may filter out "testlib" artifacts if they cause problems in subsequent steps
- It creates a Maven project (a directory with a pom.xml file) with the artifacts as the dependencies. The project uses the BOM
- It runs mvn install in the project to confirm the project is built.

## Usage

Before running the composite action the caller needs to make the BOM and its
contents available in Maven Central or local Maven repository.

In your GitHub Actions workflow file, define a job:

```
  validate-bom:
    ...
    steps:
      run: |
        # Make the BOM and artifacts available
        mvn install -DskipTests
      uses: googleapis/java-cloud-bom/tests/validate-bom@main
      with:
        path: <path_to_bom_pom.xml>
```
