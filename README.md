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
  <version>0.1-SNAPSHOT</version>
</dependency>
```

Gradle:

```
compile 'org.nuxeo.java.client:nuxeo-java-client:0.1-SNAPSHOT'
```

Ivy:

```
<dependency org="org.nuxeo.java.client" name="nuxeo-java-client" rev="0.1-SNAPSHOT" />

```

SBT:

```
libraryDependencies += "org.nuxeo.java.client" % "nuxeo-java-client" % "0.1-SNAPSHOT"
```

###Usage

**Creating a client**

For a given `url`:

```java
String url = "http://localhost:8080/nuxeo";
```

And given credentials:

```java
import org.nuxeo.java.client.api.NuxeoClient;

NuxeoClient nuxeoClient = new NuxeoClient(url, "Administrator", "Administrator");
```

Options:

```java
// For defining session and transaction timeout
nuxeoClient = nuxeoClient.timeout(60).transactionTimeout(60);
```

```java
// For defining global schemas, global enrichers and global headers in general
nuxeoClient = nuxeoClient.schemas("dublincore", "common").enrichers("acls","preview").header(key1,value1).header(key2, value2);
```

```java
// For defining all schemas
nuxeoClient = nuxeoClient.schemas("*");
```

```java
// To enable cache
nuxeoClient = nuxeoClient.enableDefaultCache();
```

```java
// To logout (shutdown the client, headers etc...)
nuxeoClient = nuxeoClient.logout();
```

**Automation API**

To use the Automation API, `org.nuxeo.java.client.api.NuxeoClient#automation()` is the entry point for all calls:

```java
import org.nuxeo.java.client.api.objects.Document;

// Fetch the root document
Document result = (Document) nuxeoClient.automation().param("value", "/").execute("Repository.GetDocument");
```

```java
import org.nuxeo.java.client.api.objects.Operation;
import org.nuxeo.java.client.api.objects.Documents;

// Execute query
Operation operation = nuxeoClient.automation("Repository.Query").param("query", "SELECT * " + "FROM Document");
Documents result = (Documents) operation.execute();
```

```java
import org.nuxeo.java.client.api.objects.blob.Blob;

// To upload|download blob(s)

Blob fileBlob = new Blob(java.io.File file);
blob = (Blob) nuxeoClient.automation().newRequest("Blob.AttachOnDocument").param("document", "/folder/file").input(fileBlob).execute();

Blobs inputBlobs = new Blobs();
inputBlobs.add(java.io.File file1);
inputBlobs.add(java.io.File file2);
Blobs blobs = (Blob) nuxeoClient.automation().newRequest("Blob.AttachOnDocument").param("xpath", "files:files").param("document", "/folder/file").input(inputBlobs).execute();
        
Blob resultBlob = (Blob) nuxeoClient.automation().input("folder/file").execute("Document.GetBlob");
```

## Testing

The Testing suite or TCK can be found in this project [`nuxeo-java-client-test`](https://github.com/nuxeo/nuxeo-java-client/tree/master/nuxeo-java-client-test).

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

By default, the library fasterXML Jackson is used for objects marshalling in `nuxeo-java-client`.

Several usages:

- POJOS and Annotations.
- Custom Json generators and parsers.

**Caching Interceptors**

#### Goals

If needed, for example on Android, we should be able to easily add caching logic.

##### How?

All caches should be accessible via a generated cache key defined by the request itself:

- headers
- base url
- endpoint used
- parameters
- body
- content type
- ...?

##### How many?

3 caches should be implemented:

- **Raw Response Store** : The server response is simply stored on the device so that it can be reused in case the server is unreachable OR to avoid too many frequent calls.
- **Document Response Store**: Store the unmarshalled response objects (here Documents) and updates.
- **Document Transient Store** bound with deferred calls queue: keeping changes of document.
- **Deferred Calls Queue**: The Create Update Delete operation will be stored locally and replayed when the server is available. Requests pure calls.

- Actions/Events

[Scenarii](https://docs.google.com/a/nuxeo.com/spreadsheets/d/1rlzMyLk_LD4OvdbJ37DBZjD5LiH4i7sb4V2YAYjINcc/edit?usp=sharing)

#####Pending questions: Invalidations

----> What would be a default timeout for each cache?

**Potential rules offline:**

- When listing documents, check the document transient store
- then check the document response store
- then check the server response

#####Synchronisation

- Should we apply those [rules](https://doc.nuxeo.com/display/NXDOC/Android+Connector+and+Caching#AndroidConnectorandCaching-TransientState) ?
- Should we use ETag And/Or If-Modified-Since with HEAD method ?

#####Potential Stores

Depending on client:
- "In memory" - guava for java
- "Database" - SQlite for Android
- Local storage for JS
- On disk for both
- Others?

#####Miscellaneous

- For the dirty properties of objects (like dirty properties of automation client for documents) - out of scope of caching


**Error & Logging**

The `NuxeoClientException` within `nuxeo-java-client` is consuming the default and the extended rest exception response by the server. Here the [documentation](https://doc.nuxeo.com/x/JQI5AQ)

## Reporting Issues

We are glad to welcome new developers on this initiative, and even simple usage feedback is great.

- Ask your questions on [Nuxeo Answers](http://answers.nuxeo.com)
- Report issues on this GitHub repository (see [issues link](http://github.com/nuxeo/nuxeo-box-api/issues) on the right)
- Contribute: Send pull requests!


# About Nuxeo

Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Netflix, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris. More information is available at [www.nuxeo.com](http://www.nuxeo.com/).
