# Google Cloud Libraries BOM

The Google Cloud Libraries BOM is a Bill-of-Materials (BOM) that provides
consistent versions of Google Cloud Java libraries and their core Google
libraries (gRPC, Protobuf, etc.).
Here is a code snippet showing how to use BOM in the `<dependencyManagement>`
section. For details, please refer to 
[Google Cloud: Using Cloud Client Libraries](https://cloud.google.com/java/docs/bom).

To use it in Maven, add the following to your POM:

<!--- {x-version-update-start:libraries-bom:released} -->
 
```xml
 <dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>com.google.cloud</groupId>
      <artifactId>libraries-bom</artifactId>
      <version>26.18.0</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```
<!--- {x-version-update-end} -->

[![Maven][maven-version-image]][maven-version-link]

When you use the Libraries BOM, you don't specify individual library versions
and your application runs on a consistent set of the dependencies.

## Libraries in Scope

The content of the Libraries BOM consists of 2 categories:
- Google Cloud Java client libraries (Maven coordinates `com.google.cloud:google-cloud-XXX`, where XXX is a GCP service name) and
- core Google dependency libraries, such as gRPC, Protobuf, and Guava.

## Dependency Dashboard

The [google-cloud-bom dashboard](https://storage.googleapis.com/java-cloud-bom-dashboard/com.google.cloud/google-cloud-bom/all-versions/index.html) provides client library consumers with easy access to dependency information pertaining to each client library that goes into the google-cloud-bom.

The dashboard shows the content of **each version** of the BOM which includes all the versions of the artifacts in it and their underlying [google-cloud-shared-dependencies BOM](https://github.com/googleapis/java-shared-dependencies#google-cloud-shared-dependencies) version.

The dashboard also has an [all versions](https://storage.googleapis.com/java-cloud-bom-dashboard/com.google.cloud/google-cloud-bom/all-versions/index.html) page where user can easily search on any artifact or version to see which version of the google-cloud-bom it exists in -- this could be helpful in providing client library consumer advice on which version(s) of google-cloud-bom to import to address their needs.

## Contributing

Contributions to this library are always welcome and highly encouraged.

See `google-cloud`'s [CONTRIBUTING] documentation and the [shared documentation](https://github.com/googleapis/google-cloud-common/blob/main/contributing/readme.md#how-to-contribute-to-gcloud) for more information on how to get started.

Please note that this project is released with a Contributor Code of Conduct. By participating in this project you agree to abide by its terms. See [Code of Conduct][code-of-conduct] for more information.

## License

Apache 2.0 - See [LICENSE] for more information.


[CONTRIBUTING]:https://github.com/googleapis/google-cloud-java/blob/main/CONTRIBUTING.md
[code-of-conduct]:https://github.com/googleapis/google-cloud-java/blob/main/CODE_OF_CONDUCT.md#contributor-code-of-conduct
[LICENSE]: https://github.com/googleapis/google-cloud-java/blob/main/LICENSE
[TESTING]: https://github.com/googleapis/google-cloud-java/blob/main/TESTING.md
[cloud-platform]: https://cloud.google.com/
[maven-version-image]: https://img.shields.io/maven-central/v/com.google.cloud/libraries-bom.svg
[maven-version-link]: https://search.maven.org/artifact/com.google.cloud/libraries-bom

