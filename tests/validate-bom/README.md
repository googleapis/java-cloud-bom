# Validate Maven BOM GitHub Action

This composite action validates a BOM specified as argument.

This action performs the following steps:

- It reads the BOM and gets all artifacts.
  - It may filter out "testlib" artifacts if they cause problems in subsequent steps
- It creates a canary Maven project (a directory with a pom.xml file) with the artifacts as the dependencies.
  The canary project uses the BOM and declares the artifacts in the BOM as dependencies.
- It runs `mvn install` in the canary project.
  If the BOM is valid, it should fetch dependencies (the artifacts in the BOM) without an error.

## Usage

Before running the composite action the caller needs to make the BOM and its
listing artifacts available in Maven Central or local Maven repository.

To use Validate Maven BOM GitHub Actions, define the following job in your
GitHub Actions workflow file:

```
  validate-bom:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    - uses: actions/setup-java@v3
      with:
        java-version: 11
        distribution: temurin
        cache: maven
    - name: Install Maven artifacts locally
      run: |
        mvn install -B -ntp -DskipTests
    - uses: googleapis/java-cloud-bom/tests/validate-bom@main
      with:
        path: <path_to_bom_pom.xml>
```

If there's an error in building the canary project, you see errors in the log:

```
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  14.253 s
[INFO] Finished at: 2023-04-14T20:41:59Z
[INFO] ------------------------------------------------------------------------
Error:  Failed to execute goal on project bom-validation-canary-project: Could n
ot resolve dependencies for project com.google.cloud:bom-validation-canary-proje
ct:jar:0.0.1-SNAPSHOT: The following artifacts could not be resolved: com.google
.analytics.api.grpc:grpc-google-analytics-admin-v1alpha:jar:0.24.0 ...
```

If there's no error, the check passes:

```
[INFO] Installing /tmp/bom-validation/pom.xml to /home/runner/.m2/repository/...
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  5.147 s
[INFO] Finished at: 2023-04-14T20:35:58Z
[INFO] ------------------------------------------------------------------------
```

# Disclaimer

This is not an official Google product.
This is intended for Google-internal usages only.
