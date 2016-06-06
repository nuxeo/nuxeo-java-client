# Java Client Library 2.0 for the Nuxeo Platform REST APIs

The Nuxeo Java Client is a Java client library (can be used for Android) for the Nuxeo Automation and REST API.

This is supported by Nuxeo and compatible with Nuxeo LTS 2015 and latest FastTracks. 

Here is the [Documentation Website](http://nuxeo.github.io/nuxeo-java-client).

[![Build Status](https://qa.nuxeo.org/jenkins/buildStatus/icon?job=master/nuxeo-java-client-master)](https://qa.nuxeo.org/jenkins/job/master/job/nuxeo-java-client-master)


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

### Library Import

#### Compatible with Nuxeo Platform 7.10 - LTS 2015

Here is the [Nuxeo Client Library 1.0](http://maven.nuxeo.com/nexus/service/local/repositories/public-releases/content/org/nuxeo/client/nuxeo-java-client/1.0/nuxeo-java-client-1.0.jar).

#### Compatible with Nuxeo Platform 8.x - FastTracks

Here is the [Nuxeo Client Library 2.0](http://maven.nuxeo.com/nexus/service/local/repositories/public-releases/content/org/nuxeo/client/nuxeo-java-client/2.0/nuxeo-java-client-2.0.jar).

**Import Nuxeo Java Client with:**

Maven:

```
<dependency>
  <groupId>org.nuxeo.client</groupId>
  <artifactId>nuxeo-java-client</artifactId>
  <version>1.0</version> <!-- (or <version>2.0</version>) -->
</dependency>
...
<repository>
  <id>public-releases</id>
  <url>
    http://maven.nuxeo.com/nexus/content/repositories/public-releases/
  </url>
</repository>
```

Gradle:

```
compile 'org.nuxeo.client:nuxeo-java-client:1.0' /** (or 2.0) */
```

Ivy:

```
<dependency org="org.nuxeo.client" name="nuxeo-java-client" rev="1.0" /** (or 2.0) */ />

```

SBT:

```
libraryDependencies += "org.nuxeo.client" % "nuxeo-java-client" % "1.0" /** (or 2.0) */
```

### Sub-Modules Organization

- `nuxeo-java-client`: Nuxeo Java Client Library.
- `nuxeo-java-client-test`: Nuxeo Java Client Suite Test.
- `NuxeoJavaClientSample`: Nuxeo Java Client Android Application Sample And Suite Test.

###Usage

####Creating a Client

For a given `url`:

```java
String url = "http://localhost:8080/nuxeo";
```

And given credentials:

```java
import org.nuxeo.client.api.NuxeoClient;

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

####APIs

General rule: 

- when using `fetch` methods, `NuxeoClient` is making remote calls. 
- when using `get` methods, objects are retrieved from memory.

####Automation API

To use the Automation API, `org.nuxeo.client.api.NuxeoClient#automation()` is the entry point for all calls:

```java
import org.nuxeo.client.api.objects.Document;

// Fetch the root document
Document result = nuxeoClient.automation().param("value", "/").execute("Repository.GetDocument");
```

```java
import org.nuxeo.client.api.objects.Operation;
import org.nuxeo.client.api.objects.Documents;

// Execute query
Operation operation = nuxeoClient.automation("Repository.Query").param("query", "SELECT * " + "FROM Document");
Documents result = operation.execute();
```

```java
import org.nuxeo.client.api.objects.blob.Blob;

// To upload|download blob(s)

Blob fileBlob = new Blob(io.File file);
blob = nuxeoClient.automation().newRequest("Blob.AttachOnDocument").param("document", "/folder/file").input(fileBlob).execute();

Blobs inputBlobs = new Blobs();
inputBlobs.add(io.File file1);
inputBlobs.add(io.File file2);
Blobs blobs = nuxeoClient.automation().newRequest("Blob.AttachOnDocument").param("xpath", "files:files").param("document", "/folder/file").input(inputBlobs).execute();
        
Blob resultBlob = nuxeoClient.automation().input("folder/file").execute("Document.GetBlob");
```

####Repository API

```java
import org.nuxeo.client.api.objects.Document;

// Fetch the root document
Document root = nuxeoClient.repository().fetchDocumentRoot();
```

```java
// Fetch document in a specific repository
root = nuxeoClient.repository().repositoryName("other_repo").fetchDocumentRoot();
```

```java
// Fetch document by path
Document folder = nuxeoClient.repository().fetchDocumentByPath("/folder_2");
```

```java
// Create a document
Document folder = nuxeoClient.repository().fetchDocumentByPath("/folder_1");
Document document = new Document("file", "File");
document.set("dc:title", "new title");
document = nuxeoClient.repository().createDocumentByPath("/folder_1", document);
```

```java
// Update a document
Document document = nuxeoClient.repository().fetchDocumentByPath("/folder_1/note_0");
Document documentUpdated = new Document("test update", "Note");
documentUpdated.setId(document.getId());
documentUpdated.set("dc:title", "note updated");
documentUpdated.setTitle("note updated");
documentUpdated.set("dc:nature", "test");
documentUpdated = nuxeoClient.repository().updateDocument(documentUpdated);
```

```java
// Delete a document
Document documentToDelete = nuxeoClient.repository().fetchDocumentByPath("/folder_1/note_1");
nuxeoClient.repository().deleteDocument(documentToDelete);
```

```java
// Fetch children
Document folder = nuxeoClient.repository().fetchDocumentByPath("/folder_2");
Documents children = folder.fetchChildren();
```

```java
// Fetch blob
Document file = nuxeoClient.repository().fetchDocumentByPath("/folder_2/file");
Blob blob = file.fetchBlob();
```

```java
import org.nuxeo.client.api.objects.audit.Audit;
// Fetch the document Audit
Document root = nuxeoClient.repository().fetchDocumentRoot();
Audit audit = root.fetchAudit();
```

```java
// Execute query
Documents documents = nuxeoClient.repository().query("SELECT * " + "From Note");

import org.nuxeo.client.api.objects.RecordSet;
// With RecordSets
RecordSet documents = nuxeoClient.automation().param("query", "SELECT * FROM Document").execute("Repository.ResultSetQuery");
```

```java
import retrofit2.Callback;
// Fetch document asynchronously with callback
nuxeoClient.repository().fetchDocumentRoot(new Callback<Document>() {
            @Override
            public void onResponse(Call<Document> call, Response<Document>
                    response) {
                if (!response.isSuccessful()) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    NuxeoClientException nuxeoClientException;
                    try {
                        nuxeoClientException = objectMapper.readValue(response.errorBody().string(),
                                NuxeoClientException.class);
                    } catch (IOException reason) {
                        throw new NuxeoClientException(reason);
                    }
                    fail(nuxeoClientException.getRemoteStackTrace());
                }
                Document folder = response.body();
                assertNotNull(folder);
                assertEquals("Folder", folder.getType());
                assertEquals("document", folder.getEntityType());
                assertEquals("/folder_2", folder.getPath());
                assertEquals("Folder 2", folder.getTitle());
            }

            @Override
            public void onFailure(Call<Document> call, Throwable t) {
                fail(t.getMessage());
            }
        });
```

####Permissions

To manage permission, please look inside package  `org.nuxeo.client.api.objects.acl` to handle ACP, ACL and ACE:

```java
// Fetch Permissions of the current document
Document folder = nuxeoClient.repository().fetchDocumentByPath("/folder_2");
ACP acp = folder.fetchPermissions();
assertTrue(acp.getAcls().size() != 0);
assertEquals("inherited", acp.getAcls().get(0).getName());
assertEquals("Administrator", acp.getAcls().get(0).getAces().get(0).getUsername());
```

```java
// Create permission on the current document
GregorianCalendar begin = new GregorianCalendar(2015, Calendar.JUNE, 20, 12, 34, 56);
GregorianCalendar end = new GregorianCalendar(2015, Calendar.JULY, 14, 12, 34, 56);
ACE ace = new ACE();
ace.setUsername("user0");
ace.setPermission("Write");
ace.setCreator("Administrator");
ace.setBegin(begin);
ace.setEnd(end);
ace.setBlockInheritance(true);
folder.addPermission(ace);
```

```java
// Remove permissions in 'local' on the current document for a given name
folder.removePermission("user0");
// Remove permissions on the current document for those given parameters
folder.removePermission(idACE, "user0", "local");
```

####Batch Upload

Batch uploads are executed through the `org.nuxeo.client.api.objects.upload.BatchUpload`.

```java
// Batch Upload Initialization
BatchUpload batchUpload = nuxeoClient.fetchUploadManager();
assertNotNull(batchUpload.getBatchId());
```

```java
// Upload File
File file = FileUtils.getResourceFileFromContext("sample.jpg");
batchUpload = batchUpload.upload(file.getName(), file.length(), "jpg", batchUpload.getBatchId(), "1", file);

// Fetch this file
BatchFile batchFile = batchUpload.fetchBatchFile("1");

import org.nuxeo.client.api.objects.upload.BatchFile;
// Upload another file and check files
file = FileUtils.getResourceFileFromContext("blob.json");
batchUpload.upload(file.getName(), file.length(), "json", batchUpload.getBatchId(), "2", file);
List<BatchFile> batchFiles = batchUpload.fetchBatchFiles();
```
Batch upload can be executed in a [chunk mode](https://doc.nuxeo.com/display/NXDOC/Blob+Upload+for+Batch+Processing?src=search#BlobUploadforBatchProcessing-UploadingaFilebyChunksUploadingaFilebyChunks).

```java
// Upload file chunks
BatchUpload batchUpload = nuxeoClient.fetchUploadManager().enableChunk();
File file = FileUtils.getResourceFileFromContext("sample.jpg");
batchUpload = batchUpload.upload(file.getName(), file.length(), "jpg", batchUpload.getBatchId(), "1", file);
```

Chunk size is by default 1MB (int 1024*1024). You can update this value with:

```java
nuxeoClient.fetchUploadManager().enableChunk().chunkSize(1024);
```

Attach batch to a document:

```java
Document doc = new Document("file", "File");
doc.set("dc:title", "new title");
doc = nuxeoClient.repository().createDocumentByPath("/folder_1", doc);
doc.set("file:content", batchUpload.getBatchBlob());
doc = doc.updateDocument();
```

or via Automation:

```java
Document doc = new Document("file", "File");
doc.set("dc:title", "new title");
doc = nuxeoClient.repository().createDocumentByPath("/folder_1", doc);
Operation operation = nuxeoClient.automation("Blob.AttachOnDocument").param("document", doc);
Blob blob = batchUpload.execute(operation);
```

####Directories

```java
import org.nuxeo.client.api.objects.directory.Directory;
// Fetch a directory
Directory directory = nuxeoClient.getDirectoryManager().fetchDirectory("continent");
```

####Users/Groups

```java
import org.nuxeo.client.api.objects.user.CurrentUser;
// Fetch current user
CurrentUser currentUser = nuxeoClient.fetchCurrentUser();
```

```java
import org.nuxeo.client.api.objects.user.User;
// Fetch user
User user = nuxeoClient.getUserManager().fetchUser("Administrator");
```

```java
import org.nuxeo.client.api.objects.user.Group;
// Fetch group
Group group = nuxeoClient.getUserManager().fetchGroup("administrators");
```

```java
// Create User/Group

UserManager userManager = nuxeoClient.getUserManager();
User newUser = new User();
newUser.setUserName("toto");
newUser.setCompany("Nuxeo");
newUser.setEmail("toto@nuxeo.com");
newUser.setFirstName("to");
newUser.setLastName("to");
newUser.setPassword("totopwd");
List<String> groups = new ArrayList<>();
groups.add("members");
newUser.setGroups(groups);
User user = userManager.createUser(newUser);

UserManager userManager = nuxeoClient.getUserManager();
Group group = new Group();
group.setGroupName("totogroup");
group.setGroupLabel("Toto Group");
List<String> users = new ArrayList<>();
users.add("Administrator");
group.setMemberUsers(users);
group = userManager.createGroup(group);
```

```java
// Update User/Group
User updatedUser = userManager.updateUser(user);
Group updatedGroup = userManager.updateGroup(group);
```

```java
// Remove User/Group
userManager.deleteUser("toto");
userManager.deleteGroup("totogroup");
```

```java
// Add User to Group
userManager.addUserToGroup("Administrator", "totogroup");
userManager.attachGroupToUser("members", "Administrator");
```

####Workflow

```java
import org.nuxeo.client.api.objects.workflow.Workflows;
// Fetch current user workflow instances
Workflows workflows = nuxeoClient.fetchCurrentUser().fetchWorkflowInstances();
```

```java
// Fetch document workflow instances
Workflows workflows = nuxeoClient.repository().fetchDocumentRoot().fetchWorkflowInstances();
```

####Manual REST Calls

`NuxeoClient` allows manual rest calls with the 4 main methods GET,POST,PUT,DELETE and provides json (de)serializer helpers:

```java
import okhttp3.Response;

// GET Method and Deserialize Json Response Payload
Response response = nuxeoClient.get("NUXEO_URL/path/");
assertEquals(true, response.isSuccessful());
String json = response.body().string();
Document document = (Document) nuxeoClient.getConverterFactory().readJSON(json, Document.class);
```

```java
// PUT Method and Deserialize Json Response Payload
Response response = nuxeoClient.put("NUXEO_URL/path/", "{\"entity-type\": \"document\",\"properties\": {\"dc:title\": \"new title\"}}");
assertEquals(true, response.isSuccessful());
String json = response.body().string();
Document document = (Document) nuxeoClient.getConverterFactory().readJSON(json, Document.class);
```

####Async/Callbacks

All APIs from the client are executable in Asynchronous way.

All apis are duplicated with an additional parameter `retrofit2.Callback<T>`.

When no response is needed (204 No Content Status for example), use `retrofit2.Callback<ResponseBody>` (`okhttp3.ResponseBody`). This object can be introspected like the response headers or status for instance.

####Custom Endpoints/Marshallers

`nuxeo-java-client` is using [retrofit](https://github.com/square/retrofit) to deploy the endpoints and [FasterXML](https://github.com/FasterXML) to create marshallers.

Here an example:

- Create a custom interface `com.CustomAPI`:

```
package com;

import com.Custom

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CustomAPI {

    @GET("custom/path")
    Call<Custom> fetchCustom(@Path("example") String example);
    ...
```

- Then create the custom object to fetch `com.Custom`:

```
package com;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Custom {

	protected String path;
	
	@JsonIgnore
	protected transient String other;
    ...
```

- Finally the fetch service by extending `org.nuxeo.client.api.objects.NuxeoEntity`:

```
package com;

public class CustomService extends NuxeoEntity {

	public Custom fetchCustom(String example) {
		return (Custom) getResponse(example);
    }
	...
```

And it's done!

Pay attention to:

- get exactly the same method name, both on the API and Service

- provide inside `getResponse(...)` the same parameters found on the API

####Cache

The default built-in cache in `nuxeo-java-client` is "in memory" (`org.nuxeo.client.api.cache.ResultCacheInMemory`).

- It can be activated by `nuxeoClient.enableDefaultCache()`

- It will store all results from requests and will restore them regarding to their signatures.

- The cache invalidation is triggered after 10 minutes and has a maximum capacity of 1 MB.

######Customization

- Customization can be done with different invalidation parameters by using `org.nuxeo.client.api.NuxeoClient#setCache`

- `org.nuxeo.client.api.NuxeoClient#setCache` can be used to instantiate a custom cache implementing the interface `org.nuxeo.client.api.cache.NuxeoResponseCache`.

*If you have specific needs, don't hesitate to create an issue on this repository, all feedbacks are welcome!*

####Errors/Exceptions

The main exception manager for the `nuxeo-java-client` is `org.nuxeo.client.internals.spi.NuxeoClientException` and contains:

- The http error status code (666 for internal errors)

- An info message

- The remote exception with stack trace (depending on the [exception mode](https://doc.nuxeo.com/x/JQI5AQ) activated on Nuxeo server side


## Testing

The Testing suite or TCK can be found in this project [`nuxeo-java-client-test`](https://github.com/nuxeo/nuxeo-java-client/tree/master/nuxeo-java-client-test).

# History

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
- Report issues on this GitHub repository (see [issues link](http://github.com/nuxeo/nuxeo-java-client/issues) on the right)
- Contribute: Send pull requests!

##About third party libraries

- Thanks a lot to the Square team for their [retrofit/okhttp](http://square.github.io/retrofit/) client libraries


# About Nuxeo

Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Netflix, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris. More information is available at [www.nuxeo.com](http://www.nuxeo.com/).
