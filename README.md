# Java Client Library 3.8.1-SNAPSHOT for the Nuxeo Platform REST APIs

The Nuxeo Java Client is a Java client library for Nuxeo Automation and REST API.

This is supported by Nuxeo and compatible with Nuxeo LTS 2015, Nuxeo LTS 2016 and latest Fast Tracks.

Here is the [Documentation Website](https://doc.nuxeo.com/client-java).

[![Jenkins master vs master](https://img.shields.io/jenkins/s/https/qa.nuxeo.org/jenkins/job/Client/job/nuxeo-java-client-vs-master/job/master.svg?label=Nuxeo%2010.x)](https://qa.nuxeo.org/jenkins/job/Client/job/nuxeo-java-client-vs-master/job/master/)
[![Jenkins master vs 9.10](https://img.shields.io/jenkins/s/https/qa2.nuxeo.org/jenkins/job/9.10/job/nuxeo-java-client-vs-9.10/job/master.svg?label=Nuxeo%209.10)](https://qa2.nuxeo.org/jenkins/job/9.10/job/nuxeo-java-client-vs-9.10/job/master/)
[![Jenkins master vs 8.10](https://img.shields.io/jenkins/s/https/qa2.nuxeo.org/jenkins/job/8.10/job/nuxeo-java-client-vs-8.10/job/master.svg?label=Nuxeo%208.10)](https://qa2.nuxeo.org/jenkins/job/8.10/job/nuxeo-java-client-vs-8.10/job/master/)
[![Jenkins master vs 7.10](https://img.shields.io/jenkins/s/https/qa2.nuxeo.org/jenkins/job/7.10/job/nuxeo-java-client-vs-7.10/job/master.svg?label=Nuxeo%207.10)](https://qa2.nuxeo.org/jenkins/job/7.10/job/nuxeo-java-client-vs-7.10/job/master/)
[![Sonar coverage](https://sonarcloud.io/api/project_badges/measure?project=org.nuxeo.client:nuxeo-java-client-parent&metric=coverage)](https://sonarcloud.io/dashboard?id=org.nuxeo.client%3Anuxeo-java-client)
[![Sonar LoC](https://sonarcloud.io/api/project_badges/measure?project=org.nuxeo.client:nuxeo-java-client-parent&metric=ncloc)](https://sonarcloud.io/dashboard?id=org.nuxeo.client%3Anuxeo-java-client)

## Building

`mvn clean install`

## Getting Started

### Server

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

#### Compatible with all Nuxeo versions as of LTS 2015

Nuxeo Java Client is compatible with:
- Nuxeo LTS 2015 - Nuxeo 7.10
- Nuxeo LTS 2016 - Nuxeo 8.10
- Ongoing Fast Tracks - Nuxeo 9.x

You can download the client on our Nexus: [Nuxeo Client Library 3.8.1-SNAPSHOT](https://maven.nuxeo.org/nexus/#nexus-search;gav~org.nuxeo.client~nuxeo-java-client~3.8.1-SNAPSHOT~jar~)

#### Import Nuxeo Java Client with:

Maven:

```xml
<dependency>
  <groupId>org.nuxeo.client</groupId>
  <artifactId>nuxeo-java-client</artifactId>
  <version>3.8.1-SNAPSHOT</version>
</dependency>
...
<repository>
  <id>public-releases</id>
  <url>http://maven.nuxeo.com/nexus/content/repositories/public-releases/</url>
</repository>
<repository>
  <id>public-snapshots</id>
  <url>http://maven.nuxeo.com/nexus/content/repositories/public-snapshots/</url>
</repository>
```

Gradle:

```
compile 'org.nuxeo.client:nuxeo-java-client:3.8.1-SNAPSHOT'
```

Ivy:

```
<dependency org="org.nuxeo.client" name="nuxeo-java-client" rev="3.8.1-SNAPSHOT" />

```

SBT:

```
libraryDependencies += "org.nuxeo.client" % "nuxeo-java-client" % "3.8.1-SNAPSHOT"
```

### Sub-Modules Organization

- `nuxeo-java-client`: Nuxeo Java Client Library.
- `nuxeo-java-client-cache`: Nuxeo Java Client Cache Implementation Library.
- `nuxeo-java-client-test`: Nuxeo Java Client Suite Test.
- `NuxeoJavaClientSample`: Nuxeo Java Client Android Application Sample And Suite Test. (work in progress)

### Usage

#### Creating a Client

For a given `url`:

```java
String url = "http://localhost:8080/nuxeo";
```

And given credentials (by default using the Basic Auth) :

```java
import org.nuxeo.client.NuxeoClient;

NuxeoClient nuxeoClient = new NuxeoClient.Builder()
                                         .url(url)
                                         .authentication("Administrator", "Administrator")
                                         .connect();
```

#### Options

Options can be set on builder, client or API objects. This ensure inheritance and isolation of options on the object
whose options are applied. As it, the builder gives its options to client and client gives them to API objects.

Some options are only available on some objects. This is the case for `cache` or `voidOperation` options.

```java
// To set a cache on client (this line needs the import of nuxeo-java-client-cache)
nuxeoClient = new NuxeoClient.Builder().cache(new ResultCacheInMemory());
```

```java
// To set read and connect timeout on http client
nuxeoClient = new NuxeoClient.Builder().readTimeout(60).connectTimeout(60);
```

```java
// To define session and transaction timeout in http headers
nuxeoClient = nuxeoClient.timeout(60).transactionTimeout(60);
```

```java
// To define global schemas, global enrichers and global headers in general
// Headers customization works by overriding - when you set a header which exist previously, client
// will remove the previous one
nuxeoClient = nuxeoClient.schemas("dublincore", "common")
                         .enrichersForDocument("acls", "preview")
                         .header(key1, value1)
                         .header(key2, value2);
```

```java
// To fetch all schemas
nuxeoClient = nuxeoClient.schemas("*");
```

```java
// To shutdown  the client
nuxeoClient = nuxeoClient.disconnect();
```

#### APIs

General rule:

- When using `fetch` methods, `NuxeoClient` is making remote calls.
- When using `get` methods, objects are retrieved from memory.

#### Operation API

To use the Operation API, `org.nuxeo.client.NuxeoClient#operation(String)` is the entry point for all calls:

```java
import org.nuxeo.client.objects.Document;

// Fetch the root document
Document doc = nuxeoClient.operation(Operations.REPOSITORY_GET_DOCUMENT).param("value", "/").execute();
```

```java
import org.nuxeo.client.objects.Documents;

// Execute query
Documents docs = nuxeoClient.operation("Repository.Query")
                            .param("query", "SELECT * FROM Document")
                            .execute();
```

```java
import org.nuxeo.client.objects.blob.Blob;
import org.nuxeo.client.objects.blob.Blobs;
import org.nuxeo.client.objects.blob.FileBlob;
import org.nuxeo.client.objects.blob.StreamBlob;

// To upload|download blob(s)

Blob fileBlob = new FileBlob(File file);
nuxeoClient.operation(Operations.BLOB_ATTACH_ON_DOCUMENT)
           .voidOperation(true)
           .param("document", "/folder/file")
           .input(fileBlob)
           .execute();

// or with stream
Blob streamBlob = new StreamBlob(InputStream stream, String filename);
nuxeoClient.operation(Operations.BLOB_ATTACH_ON_DOCUMENT)
           .voidOperation(true)
           .param("document", "/folder/file")
           .input(streamBlob)
           .execute();

Blobs inputBlobs = new Blobs();
inputBlobs.add(File file1);
inputBlobs.add(new StreamBlob(InputStream stream, String filename2));
Blobs blobs = nuxeoClient.operation(Operations.BLOB_ATTACH_ON_DOCUMENT)
                         .voidOperation(true)
                         .param("xpath", "files:files")
                         .param("document", "/folder/file")
                         .input(inputBlobs)
                         .execute();

// you need to close the stream or to get the file
Blob blob = nuxeoClient.operation(Operations.DOCUMENT_GET_BLOB)
                       .input("folder/file")
                       .execute();
```

#### Repository API

```java
import org.nuxeo.client.objects.Document;

// Fetch the root document
Document root = nuxeoClient.repository().fetchDocumentRoot();
```

```java
// Fetch document in a specific repository
root = nuxeoClient.repository("other_repo").fetchDocumentRoot();
```

```java
// Fetch document by path
Document folder = nuxeoClient.repository().fetchDocumentByPath("/folder_2");
```

```java
// Create a document
Document document = Document.createWithName("file", "File");
document.setPropertyValue("dc:title", "new title");
document = nuxeoClient.repository().createDocumentByPath("/folder_1", document);
```

```java
// Handle date using ISO 8601 format
Document document = Document.createWithName("file", "File");
document.setPropertyValue("dc:issued", "2017-02-09T00:00:00.000+01:00");
```

When handling date object, such as `java.time.ZonedDateTime` or `java.util.Calendar`, it should be converted to string
as ISO 8601 date format "yyyy-MM-dd'T'HH:mm:ss.SSSXXX" before calling the constructor or any setter method, e.g.
`Document#setPropertyValue(String, Object)`. Otherwise, an exception will be thrown by the document.

```java
// Update a document
Document document = nuxeoClient.repository().fetchDocumentByPath("/folder_1/note_0");
Document documentUpdated = Document.createWithId(document.getId(), "Note");
documentUpdated.setPropertyValue("dc:title", "note updated");
documentUpdated.setPropertyValue("dc:nature", "test");
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
Documents documents = nuxeoClient.repository().query("SELECT * From Note");

import org.nuxeo.client.api.objects.RecordSet;
// With RecordSets
RecordSet documents = nuxeoClient.operation("Repository.ResultSetQuery")
                                 .param("query", "SELECT * FROM Document")
                                 .execute();
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

#### Permissions

To manage permission, please look inside package `org.nuxeo.client.api.objects.acl` to handle ACP, ACL and ACE:

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

#### Batch Upload

Batch uploads are executed through the `org.nuxeo.client.objects.upload.BatchUploadManager`.

```java
// Batch Upload Manager
BatchUploadManager batchUploadManager = nuxeoClient.uploadManager();
BatchUpload batchUpload = batchUploadManager.createBatch();
```

```java
// Upload File
File file = FileUtils.getResourceFileFromContext("sample.jpg");
batchUpload = batchUpload.upload("1", file);

// Fetch/Refresh the batch file information from server
batchUpload = batchUpload.fetchBatchUpload("1");

// Directly from the manager
batchUpload = batchUpload.fetchBatchUpload(batchUpload.getBatchId(), "1");

// Upload another file and check files
file = FileUtils.getResourceFileFromContext("blob.json");
batchUpload.upload("2", file);
List<BatchUpload> batchFiles = batchUpload.fetchBatchUploads();
```
Batch upload can be executed in a [chunk mode](https://doc.nuxeo.com/display/NXDOC/Blob+Upload+for+Batch+Processing?src=search#BlobUploadforBatchProcessing-UploadingaFilebyChunksUploadingaFilebyChunks).

```java
// Upload file chunks
BatchUploadManager batchUploadManager = nuxeoClient.uploadManager();
BatchUpload batchUpload = batchUploadManager.createBatch();
batchUpload.enableChunk();
File file = FileUtils.getResourceFileFromContext("sample.jpg");
batchUpload = batchUpload.upload("1", file);
```

Chunk size is by default 1MB (int 1024*1024). You can update this value with:

```java
batchUpload.chunkSize(1024);
```

Attach batch to a document:

```java
Document doc = new Document("file", "File");
doc.set("dc:title", "new title");
doc = nuxeoClient.repository().createDocumentByPath("/folder_1", doc);
doc.set("file:content", batchUpload.getBatchBlob());
doc = doc.updateDocument();
```

or with operation:

```java
Document doc = new Document("file", "File");
doc.set("dc:title", "new title");
doc = nuxeoClient.repository().createDocumentByPath("/folder_1", doc);
Blob blob = batchUpload.operation(Operations.BLOB_ATTACH_ON_DOCUMENT).param("document", doc).execute();
```

#### Directories

```java
import org.nuxeo.client.objects.directory.DirectoryEntries;
// Fetch a directory entries
DirectoryEntries entries = nuxeoClient.directoryManager().fetchDirectoryEntries("continent");
```

#### Users/Groups

```java
import org.nuxeo.client.objects.user.User;
// Get current user used to connect to Nuxeo Server
User currentUser = nuxeoClient.getCurrentUser();

// Fetch current user from sever
User currentUser = nuxeoClient.userManager().fetchCurrentUser();
```

```java
import org.nuxeo.client.objects.user.User;
// Fetch user
User user = nuxeoClient.userManager().fetchUser("Administrator");
```

```java
import org.nuxeo.client.objects.user.Group;
// Fetch group
Group group = nuxeoClient.userManager().fetchGroup("administrators");
```

```java
// Create User/Group

UserManager userManager = nuxeoClient.userManager();
User newUser = new User();
newUser.setUserName("toto");
newUser.setCompany("Nuxeo");
newUser.setEmail("toto@nuxeo.com");
newUser.setFirstName("to");
newUser.setLastName("to");
newUser.setPassword("totopwd");
newUser.setTenantId("mytenantid");
List<String> groups = new ArrayList<>();
groups.add("members");
newUser.setGroups(groups);
User user = userManager.createUser(newUser);

UserManager userManager = nuxeoClient.userManager();
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

#### Workflow

```java
import org.nuxeo.client.objects.workflow.Workflows;
// Fetch current user workflow instances
Workflows workflows = nuxeoClient.userManager().fetchWorkflowInstances();
```

```java
// Fetch document workflow instances
Workflows workflows = nuxeoClient.repository().fetchDocumentRoot().fetchWorkflowInstances();
```

#### Manual REST Calls

`NuxeoClient` allows manual REST calls with the 4 main methods GET, POST, PUT, DELETE and provides JSON (de)serializer helpers:

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

#### Authentication

By default, Nuxeo java client is using the basic authentication via the okhttp interceptor `org.nuxeo.client.spi.auth.BasicAuthInterceptor`.

##### The other available interceptors are:

- `org.nuxeo.client.spi.auth.PortalSSOAuthInterceptor`
- `org.nuxeo.client.spi.auth.TokenAuthInterceptor`

##### To use different interceptor(s):

Use `NuxeoClientBuilder#authentication(BasicAuthInterceptor)` instead of basic authentication.

##### To create a new interceptor:

Create a new java class implementing the interface `okhttp3.Interceptor` - see the okhttp [documentation](https://github.com/square/okhttp/wiki/Interceptors).

#### Async/Callbacks

All APIs from the client are executable in Asynchronous way.

All APIs are duplicated with an additional parameter `retrofit2.Callback<T>`.

When no response is needed (204 No Content Status for example), use `retrofit2.Callback<ResponseBody>` (`okhttp3.ResponseBody`). This object can be introspected like the response headers or status for instance.

#### Operation & Business Objects

In Operation, to use Plain Old Java Object client side for mapping custom objects server side (like document model adapter or simply a custom structure sent back by the server), it is possible to manage "business objects":

- Server side, a custom JSON object will be built in the Operation operation and sent
- Client side, a mapper will be available to match the custom response JSON payload to represent the structure by a POJO

Example:

Custom server side operation:

```java
@Operation(id = CustomOperationJSONBlob.ID, category = "Document", label = "CustomOperationJSONBlob")
public class CustomOperationJSONBlob {

    public static final String ID = "CustomOperationJSONBlob";

    @OperationMethod
    public Blob run() {

        JSONObject attributes = new JSONObject();
        attributes.put("entity-type", "custom-json-object")
        attributes.put("userId", "1");
        attributes.put("token", "token");

        return Blobs.createBlob(attributes.toString(), "application/json");
    }
}
```

This operation will create this request json payload:

```java
{
  "entity-type": "custom-json-object";
  "userId": "1",
  "token": "token"
}
```

On the client side, we will have to provide:

- This simple pojo:

```java
// Extending Entity is optional
import org.nuxeo.client.objects.Entity;

public class CustomJSONObject extends Entity {

    public static final String ENTITY_TYPE = "custom-json-object";

    private String userId;

    private String token;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    public CustomJSONObject() {
        super(ENTITY_TYPE);
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
```

- This code to register the pojo and use the operation:

```java
NuxeoClient client = new NuxeoClient.Builder()
                                       .url(url)
                                       .authentication(username, password)
                                       .registerEntity(CustomJSONObject.ENTITY_TYPE, CustomJSONObject.class)
                                       .connect();
CustomJSONObject customJSONObject = nuxeoClient.operation("CustomOperationJSONBlob").execute();
```

#### Custom Endpoints/Marshallers

`nuxeo-java-client` is using [retrofit](https://github.com/square/retrofit) to deploy the endpoints and [FasterXML](https://github.com/FasterXML) to create marshallers.

Here an example:

- Create a custom interface `com.CustomAPI`:

```java
package com;

import com.Custom

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CustomAPI {

    @GET("custom/path")
    Call<Custom> fetchCustom(@Path("example") String example);
```

- Then create the custom object to fetch `com.Custom`:

```java
package com;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Custom {

  protected String path;

  @JsonIgnore
  protected transient String other;
```

- Finally the fetch service by extending `org.nuxeo.client.objects.AbstractConnectable`:

```java
package com;

import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.objects.AbstractConnectable;

public class CustomService extends AbstractConnectable<CustomAPI> {

  public CustomService(NuxeoClient client) {
    super(CustomAPI.class, client);
  }

  public Custom fetchCustom(String example) {
    return fetchResponse(api.fetchCustom(example));
  }
```

And it's done!

#### Cache

We provide a "in memory" cache implementation using [Guava](https://github.com/google/guava). In order to use it, you need to add as dependency `nuxeo-java-client-cache`.

- You can use this cache with `new NuxeoClient.Builder().cache(new ResultCacheInMemory())`

- It will store all results from requests and will restore them regarding to their signatures.

- The cache invalidation is triggered after 10 minutes and has a maximum capacity of 1 MB.

To use it, just set the cache during client construction:
```java
import org.nuxeo.client.NuxeoClient;

NuxeoClient client = new NuxeoClient.Builder()
                                       .url(url).authentication(username, password)
                                       .cache(new ResultCacheInMemory)
                                       .connect();
```

##### Customization

- `org.nuxeo.client.cache.ResultCacheInMemory` provide some parameters for invalidations mechanism

- `org.nuxeo.client.NuxeoClient.Builder#cache` can be used to give any cache implementing the interface `org.nuxeo.client.cache.NuxeoResponseCache`.

#### Errors/Exceptions

The main exception manager for the `nuxeo-java-client` is `org.nuxeo.client.spi.NuxeoClientException` and contains:

- The HTTP error status code (666 for internal errors)

- An info message

- The remote exception with stack trace (depending on the [exception mode](https://doc.nuxeo.com/x/JQI5AQ) activated on Nuxeo server side


## Testing

The Testing suite or TCK can be found in this project [`nuxeo-java-client-test`](https://github.com/nuxeo/nuxeo-java-client/tree/master/nuxeo-java-client-test).

# History

The initial `nuxeo-automation-client` is now old:

 - Client design was based on Automation API before REST endpoints where available in Nuxeo
 - A lot of features around upload & download are missing
 - Marshalling and exception management are sometimes bad  

The `nuxeo-automation-client` was then forked to build a Android version with some caching.

## Constraints

**JVM & Android**

The `nuxeo-java-client` must works on both a standard JVM and Android Dalvik VM.

** Java 7**

Library must work on older Java versions.
The goal is to be able to use `nuxeo-java-client` from application running in Java 7.

**Light dependencies**

The library should be easy to embed so we want to have as few dependencies as possible.
That's why the in memory cache is in a separated module. It depends on Guava which is a heavy library.

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
- Custom JSON generators and parsers.

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

##### Pending questions: Invalidations

----> What would be a default timeout for each cache?

**Potential rules offline:**

- When listing documents, check the document transient store
- then check the document response store
- then check the server response

##### Synchronisation

- Should we apply those [rules](https://doc.nuxeo.com/display/NXDOC/Android+Connector+and+Caching#AndroidConnectorandCaching-TransientState)?
- Should we use ETag And/Or If-Modified-Since with HEAD method?

##### Potential Stores

Depending on client:
- "In memory" - Guava for Java
- "Database" - SQlite for Android
- Local storage for JS
- On disk for both
- Others?

##### Miscellaneous

- For the dirty properties of objects (like dirty properties of automation client for documents) - out of scope of caching


**Error & Logging**

The `NuxeoClientException` within `nuxeo-java-client` is consuming the default and the extended rest exception response by the server. Here the [documentation](https://doc.nuxeo.com/x/JQI5AQ)

## Reporting Issues

We are glad to welcome new developers on this initiative, and even simple usage feedback is great.

- Ask your questions on [Nuxeo Answers](http://answers.nuxeo.com)
- Report issues on this GitHub repository (see [issues link](http://github.com/nuxeo/nuxeo-java-client/issues) on the right)
- Contribute: Send pull requests!

## About third party libraries

- Thanks a lot to the Square team for their [retrofit/okhttp](http://square.github.io/retrofit/) client libraries


# About Nuxeo

Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris. More information is available at [www.nuxeo.com](http://www.nuxeo.com/).
