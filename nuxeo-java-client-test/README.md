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
mvn clean install -Dnuxeo.server.tomcat.version=10.10
```

You can run a Nuxeo with the configuration defined in the pom with (in order to run tests from your IDE):

```bash
mvn ant-assembly:build@start-tomcat
mvn ant-assembly:build@stop-tomcat
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

By default, the command above will try to reach the Nuxeo Server on `http://localhost:8080/nuxeo`, you can change this parameter:

```bash
mvn verify -DskipPrePostIntegration -Dnuxeo.server.url=https://nightly.nuxeo.com/nuxeo
```

You can deploy the same stack as the CI does by building a Nuxeo Docker image and using helmfile to deploy the stack 
(you must be at the root of the project):

```bash
# build the docker image
docker build -t nuxeo/nuxeo-java-client-ftests:2021 --build-arg NUXEO_VERSION=2021 . -f ci/docker/nuxeo/Dockerfile
# deploy the stack
helmfile --namespace njc-test --file ci/helm/helmfile.yaml --helm-binary /usr/local/bin/helm3 --environment functional-tests-2021 sync
# in another terminal forward the traffic from your localhost to the cluster
kubectl --namespace njc-test port-forward svc/nuxeo 8080:80
# run functional tests
mvn -pl :nuxeo-java-client-test -DskipPrePostIntegration verify
# shutdown the stack
helmfile --namespace njc-test --file ci/helm/helmfile.yaml --helm-binary /usr/local/bin/helm3 --environment functional-tests-2021 destroy
```
