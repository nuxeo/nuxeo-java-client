# Java Client Library for Nuxeo REST APIs

The Nuxeo Java Client is a Java client library for the Nuxeo Automation and REST API.

This is an on-going project, supported by Nuxeo.

![Build Status](https://qa.nuxeo.org/jenkins/buildStatus/icon?job=nuxeo-java-client-master)

## Sub-Modules Organization

- `nuxeo-java-client`: Nuxeo Java Client Library.
- `nuxeo-java-client-test`: Nuxeo Java Client Suite Test.
- `NuxeoJavaClientSample`: Nuxeo Java Client Android Application Sample And Suite Test.

## Building

`mvn clean install`

## Getting Started

###Server

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

### Client - Library Import

**Import Nuxeo Java Client with:**

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

**Usage:**

TBD

## Goals

Provide a java library to make developing with Nuxeo REST API easier.

## History

The initial `nuxeo-automation-client` is now old :

 - client design was based on Automation API before REST endpoints where available in Nuxeo
 - a lot of features around upload & download are missing
 - marshalling and exception management are sometimes bad  

The `nuxeo-automation-client` was then forked to build a Android version with some caching.

## Constraints

**JVM & Android**

The `nuxeo-java-client` must works on both a standard JVM and Android Dalvik VM.

**Java 6 & Java 7**

Library must work on older Java versions.
The goal is to be able to use `nuxeo-java-client` from application running in Java6 or Java 7.

**Light dependencies** 

The library should be easy to embed so we want to have as few dependencies as possible.

**Cache compliant**

If needed, for example on Android, we should be able to easily add caching logic.

We do not need to implement all the caching features that were inside the Android Client, but we need to design the library so that adding them can be done without breaking the library structure.

**Exception Management**

Client should be able to retrieve the remote Exception easily and access to the trace feature would be ideal.

## Design Principles

**JS like**

Make the API look like the JS one (Fluent, Promises ...)

**Retrolambda & Retrofit**

Share the http lib between JVM and Android.
Allow to use Lambda in the code.

**Jackson & Marshaling**

Jackson + Annotations.

**Caching Interceptors**

TBD

**Error & Logging**

TBD

## Testing

TCK TBD

## Reporting Issues

We are glad to welcome new developers on this initiative, and even simple usage feedback is great.

- Ask your questions on [Nuxeo Answers](http://answers.nuxeo.com)
- Report issues on this GitHub repository (see [issues link](http://github.com/nuxeo/nuxeo-box-api/issues) on the right)
- Contribute: Send pull requests!


# About Nuxeo

Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Netflix, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris. More information is available at [www.nuxeo.com](http://www.nuxeo.com/).
