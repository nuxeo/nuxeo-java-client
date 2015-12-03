# Java Client Library for Nuxeo REST APIs

The Nuxeo Java Client is a Java client library for the Nuxeo Automation and REST API.

This is an on-going project, supported by Nuxeo.

![Build Status](https://qa.nuxeo.org/jenkins/buildStatus/icon?job=nuxeo-java-client-master)

## Sub-Modules Organization

- `nuxeo-java-client`: Nuxeo Java Client Library.
- `nuxeo-java-client-test`: Nuxeo Java Client Suite Test.
- `nuxeo-java-client-android-test`: Nuxeo Java Client Android Application Sample And Suite Test.

## Building

`mvn clean install`

## Getting Started

- [Download a Nuxeo server](http://www.nuxeo.com/en/downloads) (the zip version)

- Unzip it

- Linux/Mac:
    - `NUXEO_HOME/bin/nuxeoctl start`
- Windows:
    - `NUXEO_HOME\bin\nuxeoctl.bat start`

- From your browser, go to `http://localhost:8080/nuxeo`

- Follow Nuxeo Wizard by clicking 'Next' buttons, re-start once completed

- Check Nuxeo correctly re-started `http://localhost:8080/nuxeo`
  - username: Administrator
  - password: Administrator

### Library Import

Import Nuxeo Java Client with:

Maven:

```
<dependency>
  <groupId>org.nuxeo.java.client</groupId>
  <artifactId>nuxeo-java-client</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

Gradle:

```
compile 'org.nuxeo.java.client:nuxeo-java-client:1.0-SNAPSHOT'
```

Ivy:

```
<dependency org="org.nuxeo.java.client" name="nuxeo-java-client" rev="1.0-SNAPSHOT" />

```

SBT:

```
libraryDependencies += "org.nuxeo.java.client" % "nuxeo-java-client" % "1.0-SNAPSHOT"
```

## Reporting Issues

We are glad to welcome new developers on this initiative, and even simple usage feedback is great.

- Ask your questions on [Nuxeo Answers](http://answers.nuxeo.com)
- Report issues on this GitHub repository (see [issues link](http://github.com/nuxeo/nuxeo-box-api/issues) on the right)
- Contribute: Send pull requests!


# About Nuxeo

Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Netflix, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris. More information is available at [www.nuxeo.com](http://www.nuxeo.com/).
