To generate the dashboard from the root directory, run:

```
$ cd dashboard
$ mvn clean install
$ mvn exec:java -Dexec.arguments="-f ../pom.xml"
```

To generate all dashboards (version 0.124.0 and onwards), including the All Versions page, run:

```
$ cd dashboard
$ mvn clean install
$ mvn exec:java -Dexec.arguments="-a com.google.cloud:google-cloud-bom"
```
