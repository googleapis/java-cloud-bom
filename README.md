Google Cloud Bill of Materials
==============================

The `google-cloud-bom` module is a pom that can be used to import consistent versions of google-cloud-java
components plus its dependencies.

To use it in Maven, add the following to your POM:

[//]: # ({x-version-update-start:google-cloud-bom:released})
```xml
  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>com.google.cloud</groupId>
        <artifactId>google-cloud-bom</artifactId>
        <version>0.146.1</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>
```
[//]: # ({x-version-update-end})

Dependency Dashboard
------------

The [google-cloud-bom dashboard](https://storage.googleapis.com/java-cloud-bom-dashboard/com.google.cloud/google-cloud-bom/all-versions/index.html) provides client library consumers with easy access to dependency information pertaining to each client library that goes into the google-cloud-bom.

The dashboard shows the content of **each version** of the BOM which includes all the versions of the artifacts in it and their underlying [google-cloud-shared-dependencies BOM](https://github.com/googleapis/java-shared-dependencies#google-cloud-shared-dependencies) version.

The dashboard also has an [all versions](https://storage.googleapis.com/java-cloud-bom-dashboard/com.google.cloud/google-cloud-bom/all-versions/index.html) page where user can easily search on any artifact or version to see which version of the google-cloud-bom it exists in -- this could be helpful in providing client library consumer advice on which version(s) of google-cloud-bom to import to address their needs.

Contributing
------------

Contributions to this library are always welcome and highly encouraged.

See `google-cloud`'s [CONTRIBUTING] documentation and the [shared documentation](https://github.com/googleapis/google-cloud-common/blob/master/contributing/readme.md#how-to-contribute-to-gcloud) for more information on how to get started.

Please note that this project is released with a Contributor Code of Conduct. By participating in this project you agree to abide by its terms. See [Code of Conduct][code-of-conduct] for more information.

License
-------

Apache 2.0 - See [LICENSE] for more information.


[CONTRIBUTING]:https://github.com/googleapis/google-cloud-java/blob/master/CONTRIBUTING.md
[code-of-conduct]:https://github.com/googleapis/google-cloud-java/blob/master/CODE_OF_CONDUCT.md#contributor-code-of-conduct
[LICENSE]: https://github.com/googleapis/google-cloud-java/blob/master/LICENSE
[TESTING]: https://github.com/googleapis/google-cloud-java/blob/master/TESTING.md
[cloud-platform]: https://cloud.google.com/
