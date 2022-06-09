# Google Cloud Libraries BOM

The Google Cloud Libraries BOM is a
Bill-of-Materials (BOM) that provides consistent versions of Google Cloud Java
libraries and their core Google libraries (gRPC, Protobuf, etc.).

Here is a code snippet showing the how to use BOM in `<dependencyManagement>`
section. For details, please refer to
[Google Cloud: Using Cloud Client Libraries](https://cloud.google.com/java/docs/bom).

[//]: # ({x-version-update-start:libraries-bom:released})
```xml
  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>com.google.cloud</groupId>
        <artifactId>libraries-bom</artifactId>
        <version>25.4.0</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>
```
[//]: # ({x-version-update-end})

When you use the Libraries BOM, you don't specify individual library
versions and your application runs on a consistent set of the dependencies.

## Libraries in Scope

The content of the Libraries BOM consists of 2 categories: Google Cloud Java
client libraries and core Google dependency libraries.

### Google Cloud Java client libraries

A Google Cloud Java client library is a Java library that communicates with a
corresponding GCP service.
We have ~150 Java client libraries for Google Cloud.

The `google-cloud-bom` module in this repository produces a BOM that lists
the consistent versions of the Java client libraries. This consistency means
that these versions are built and tested with the same set of dependencies.
The Libraries BOM imports `google-cloud-bom`.

### Core Google dependency libraries

Core Google dependency libraries are the Google-developed Java libraries used by
the client libraries.
This includes low-level libraries such as Guava, Protobuf, gRPC, Google Auth
Libraries.
These libraries do not have a specific Google Cloud service
associated to it.

While the users of Google Cloud do not have to directly interact with the core
libraries, the Libraries BOM includes the core Google dependency libraries so
that the users receive a consistent set of the dependencies behind the scene
when they use the client libraries.

### Not in Scope: Framework Integration

The Libraries BOM does not include libraries for framework integration.
Framework integration is a category of Java libraries to connect GCP services
with popular Java frameworks.
For example, [Spring Cloud GCP](
https://cloud.google.com/java/docs/reference/spring),
[Apache Beam (Cloud Dataflow)](
https://cloud.google.com/dataflow/docs/concepts/beam-programming-model),
and [Pub/Sub Lite Kafka Shim](
https://cloud.google.com/pubsub/lite/docs/publish-receive-messages-apache-kafka)
are in this category and thus they are not part of the Libraries BOM.

We chose not to include framework integration in the BOM, because of the
complexity associated in bringing them together.
In general Java frameworks are developed independently by different organization
with their own practice of dependency management.
Bringing in the hundreds of dependencies from the various Java frameworks into
one project (or one BOM) creates challenges in choosing correct versions of
every dependency that work for every framework.

Therefore, rather than trying to include the libraries in one BOM, we advocate
that the framework integration libraries should _use_ the Libraries BOM to get
consistent set of the Google Cloud Java client libraries.

# Dependency Dashboard

The [google-cloud-bom dashboard](https://storage.googleapis.com/java-cloud-bom-dashboard/com.google.cloud/google-cloud-bom/all-versions/index.html) provides client library consumers with easy access to dependency information pertaining to each client library that goes into the google-cloud-bom.

The dashboard shows the content of **each version** of the BOM which includes all the versions of the artifacts in it and their underlying [google-cloud-shared-dependencies BOM](https://github.com/googleapis/java-shared-dependencies#google-cloud-shared-dependencies) version.

The dashboard also has an [all versions](https://storage.googleapis.com/java-cloud-bom-dashboard/com.google.cloud/google-cloud-bom/all-versions/index.html) page where user can easily search on any artifact or version to see which version of the google-cloud-bom it exists in -- this could be helpful in providing client library consumer advice on which version(s) of google-cloud-bom to import to address their needs.

# Contributing

Contributions to this library are always welcome and highly encouraged.

See `google-cloud`'s [CONTRIBUTING] documentation and the [shared documentation](https://github.com/googleapis/google-cloud-common/blob/main/contributing/readme.md#how-to-contribute-to-gcloud) for more information on how to get started.

Please note that this project is released with a Contributor Code of Conduct. By participating in this project you agree to abide by its terms. See [Code of Conduct][code-of-conduct] for more information.

# License

Apache 2.0 - See [LICENSE] for more information.

[CONTRIBUTING]:https://github.com/googleapis/google-cloud-java/blob/main/CONTRIBUTING.md
[code-of-conduct]:https://github.com/googleapis/google-cloud-java/blob/main/CODE_OF_CONDUCT.md#contributor-code-of-conduct
[LICENSE]: https://github.com/googleapis/google-cloud-java/blob/main/LICENSE
[TESTING]: https://github.com/googleapis/google-cloud-java/blob/main/TESTING.md
[cloud-platform]: https://cloud.google.com/
