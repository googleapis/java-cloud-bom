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
- Core Google dependency libraries, such as gRPC, Protobuf, and Guava.

This is the table of modules included in the latest libraries-bom release:

<!-- TABLE_START -->
| Artifact ID | Library Type | Google Cloud Library Reference | Google Cloud Product Reference | 
| --------- | ------------ | ------------ | ------------ |
| google-cloud-bigquery | Product | [v2.29.0](https://cloud.google.com/bigquery) | [Cloud BigQuery](https://cloud.google.com/java/docs/reference/google-cloud-bigquery/latest/history) |
| google-cloud-bigquerystorage-bom | Product | [v2.39.1](https://cloud.google.com/bigquery/docs/reference/storage/) | [BigQuery Storage](https://cloud.google.com/java/docs/reference/google-cloud-bigquerystorage/latest/history) |
| google-cloud-bigtable-bom | Product | [v2.24.1](https://cloud.google.com/bigtable) | [Cloud Bigtable](https://cloud.google.com/java/docs/reference/google-cloud-bigtable/latest/history) |
| google-cloud-datastore-bom | Product | [v2.16.0](https://cloud.google.com/datastore) | [Cloud Datastore](https://cloud.google.com/java/docs/reference/google-cloud-datastore/latest/history) |
| google-cloud-firestore-bom | Product | [v3.13.2](https://cloud.google.com/firestore) | [Cloud Firestore](https://cloud.google.com/java/docs/reference/google-cloud-firestore/latest/history) |
| google-cloud-logging-bom | Product | [v3.15.5](https://cloud.google.com/logging/docs) | [Cloud Logging](https://cloud.google.com/java/docs/reference/google-cloud-logging/latest/history) |
| google-cloud-logging-logback | Product | [v0.130.17-alpha](https://cloud.google.com/logging/docs/setup/java#logback_appender_for) | [Cloud Logging Logback Appender](https://cloud.google.com/java/docs/reference/google-cloud-logging-logback/latest/history) |
| google-cloud-nio | Product | [v0.126.18](https://cloud.google.com/storage/docs) | [NIO Filesystem Provider for Google Cloud Storage](https://cloud.google.com/java/docs/reference/google-cloud-nio/latest/history) |
| google-cloud-pubsub-bom | Product | [v1.123.17](https://cloud.google.com/pubsub/docs/) | [Cloud Pub/Sub](https://cloud.google.com/java/docs/reference/google-cloud-pubsub/latest/history) |
| google-cloud-pubsublite-bom | Product | [v1.12.10](https://cloud.google.com/pubsub/lite/docs) | [Cloud Pub/Sub Lite](https://cloud.google.com/java/docs/reference/google-cloud-pubsublite/latest/history) |
| google-cloud-spanner-bom | Product | [v6.43.1](https://cloud.google.com/spanner/docs/) | [Cloud Spanner](https://cloud.google.com/java/docs/reference/google-cloud-spanner/latest/history) |
| google-cloud-spanner-jdbc | Product | [v2.11.2](https://cloud.google.com/spanner/docs/use-oss-jdbc) | [Google Cloud Spanner JDBC](https://cloud.google.com/java/docs/reference/google-cloud-spanner-jdbc/latest/history) |
| google-cloud-storage-bom | Product | [v2.22.5](https://cloud.google.com/storage) | [Cloud Storage](https://cloud.google.com/java/docs/reference/google-cloud-storage/latest/history) |
<!-- TABLE_END -->

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

