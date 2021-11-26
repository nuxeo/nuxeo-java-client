# nuxeo-java-client-test

This Maven module is responsible to run nuxeo-java-client functional tests against a Nuxeo Server.

It is made of two parts:
- a marketplace package that installs the configuration needed by the tests
- the functional tests

## Usages

When running the non parameterized Maven command, this module will build the package, start a Nuxeo Server with the
package installed, run the functional tests, and stop the Nuxeo Server:

```bash
mvn clean install
```

You can specify the Nuxeo Server version you want to test the client against:

```bash
mvn clean install -Dnuxeo.server.tomcat=10.10
```

You can build the marketplace package, then install it on a standalone Nuxeo Server, and run the functional tests:

```bash
# build the package
mvn clean compile
# install the package on your Nuxeo Server (replace ~/ws by the location of the repository on your computer)
nuxeoctl mp-install ~/ws/nuxeo-java-client/nuxeo-java-client-test/target/nuxeo-java-client-test-*.zip
# run the functional tests
mvn verify -DskipPrePostIntegration
```

By default, the above command will try to reach the Nuxeo Server on `http://localhost:8080/nuxeo`, you can change this parameter:

```bash
mvn verify -DskipPrePostIntegration -Dnuxeo.server.url=https://nightly.nuxeo.com/nuxeo
```
