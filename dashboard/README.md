To generate the dashboard from the root directory run:

```
$ cd dashboard
$ mvn clean install
$ mvn exec:java -Dexec.arguments="-f ../pom.xml"
```
