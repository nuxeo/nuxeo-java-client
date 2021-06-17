/*
 * (C) Copyright 2017-2018 Nuxeo (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Kevin Leturc <kleturc@nuxeo.com>
 */
package org.nuxeo.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.nuxeo.client.Operations.BLOB_ATTACH_ON_DOCUMENT;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.client.objects.Document;
import org.nuxeo.client.objects.Repository;
import org.nuxeo.client.objects.blob.FileBlob;
import org.nuxeo.client.objects.user.Group;
import org.nuxeo.client.objects.user.User;
import org.nuxeo.client.objects.user.UserManager;
import org.nuxeo.client.objects.workflow.Workflow;
import org.nuxeo.client.objects.workflow.Workflows;
import org.nuxeo.client.spi.NuxeoClientRemoteException;
import org.nuxeo.client.spi.auth.BasicAuthInterceptor;
import org.nuxeo.client.spi.auth.JWTAuthInterceptor;
import org.nuxeo.client.spi.auth.PortalSSOAuthInterceptor;
import org.nuxeo.common.utils.FileUtils;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Tests the basic operation of client. This test is isolated from test framework because it unit tests the operation
 * used in framework to init server and to clean it.
 *
 * @since 3.0
 */
public class ITBase {

    public static final String BASE_URL = "http://localhost:8080/nuxeo";

    public static final String LOGIN = "Administrator";

    public static final String PASSWORD = "Administrator";

    /**
     * Token for Administration which never expires.
     *
     * @since 3.3
     */
    public static final String JWT = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBZG1pbmlzdHJhdG9yIiwiaXNzIjoibnV4ZW8ifQ.mRxcrdaRtzYqoINJjGJZVZIRuij6yevVxmH3NdVU4IzpJh4PDDFbfzOpsruimUWauAWAeZKLBi4bekicrN5jKQ";

    public static final String DEFAULT_USER_LOGIN = "toto";

    public static final String DEFAULT_USER_PASSWORD = "totopwd";

    public static final String DEFAULT_USER_EMAIL = "toto@nuxeo.com";

    public static final String DEFAULT_GROUP_NAME = "totogroup";

    @Test
    @SuppressWarnings("deprecation")
    public void itCanFetchServerVersion() {
        NuxeoClient client = createClient();
        NuxeoVersion version = client.getServerVersion();
        assertNotNull(version);
        // First version compatible with this client
        assertTrue(version.isGreaterThan(NuxeoVersion.LTS_7_10));
    }

    @Test
    public void itCanFetchRoot() {
        NuxeoClient client = createClient();
        Document root = client.repository().fetchDocumentRoot();
        assertNotNull(root);
        assertEquals("Root", root.getType());
        assertEquals("document", root.getEntityType());
        assertEquals("/", root.getParentRef());
        assertEquals("/", root.getPath());
    }

    @Test
    public void itCanFetchRootWithRepositoryName() {
        NuxeoClient client = createClient();
        Document root = client.repository().fetchDocumentRoot();
        root = client.repository(root.getRepositoryName()).fetchDocumentRoot();
        assertNotNull(root);
        assertEquals("Root", root.getType());
        assertEquals("document", root.getEntityType());
        assertEquals("/", root.getParentRef());
        assertEquals("/", root.getPath());
    }

    @Test
    public void itCanCreateFetchUpdateDeleteDocument() {
        Repository repository = createClient().schemas("*").repository();

        Document root = repository.fetchDocumentRoot();

        // Create
        Document document = Document.createWithName("note", "Note");
        document.setPropertyValue("dc:title", "note");
        document = repository.createDocumentById(root.getId(), document);
        assertEquals("Note", document.getType());
        assertEquals("default", document.getRepositoryName());
        assertEquals("project", document.getState());
        assertEquals("note", document.getTitle());
        assertEquals("note", document.getPropertyValue("dc:title"));

        Document documentUpdated = Document.createWithId(document.getId(), "Note");
        documentUpdated.setPropertyValue("dc:title", "note updated");

        // Update
        documentUpdated = repository.updateDocument(documentUpdated);
        assertNotNull(documentUpdated);
        assertEquals("note updated", documentUpdated.getPropertyValue("dc:title"));

        // Fetch
        // Check if the document in the repository has been changed.
        Document result = repository.fetchDocumentById(documentUpdated.getId());
        assertNotNull(result);
        assertEquals("note updated", result.getPropertyValue("dc:title"));

        // Delete
        repository.deleteDocument(result);
        try {
            repository.fetchDocumentByPath(result.getPath());
            fail("Document should have been deleted");
        } catch (NuxeoClientRemoteException nce) {
            assertEquals(404, nce.getStatus());
        }
    }

    @Test
    public void itCanAttachBlob() {
        NuxeoClient client = createClient();
        // Create a file
        Document doc = Document.createWithName("file", "File");
        doc.setPropertyValue("dc:title", "File");
        doc = client.repository().createDocumentByPath("/", doc);
        // Attach a blob
        File file = FileUtils.getResourceFileFromContext("sample.jpg");
        FileBlob fileBlob = new FileBlob(file);
        client.operation(BLOB_ATTACH_ON_DOCUMENT)
              .voidOperation(true)
              .param("document", "/file")
              .input(fileBlob)
              .execute();

        client.repository().deleteDocument(doc);
    }

    @Test
    public void itCanCreateFetchDeleteAUser() {
        NuxeoClient client = createClient();
        UserManager userManager = client.userManager();
        assertNotNull(userManager);

        // Create
        User user = createUser();
        user = userManager.createUser(user);
        assertNotNull(user);
        assertEquals(DEFAULT_USER_LOGIN, user.getId());
        assertEquals("last toto", user.getLastName());
        assertEquals(DEFAULT_USER_EMAIL, user.getEmail());

        // Fetch
        user = userManager.fetchUser(DEFAULT_USER_LOGIN);
        assertNotNull(user);
        assertEquals(DEFAULT_USER_LOGIN, user.getId());
        assertEquals("last toto", user.getLastName());
        assertEquals(DEFAULT_USER_EMAIL, user.getEmail());

        // Delete
        userManager.deleteUser(DEFAULT_USER_LOGIN);
        try {
            userManager.fetchUser(DEFAULT_USER_LOGIN);
            fail("User should not exist");
        } catch (NuxeoClientRemoteException reason) {
            Assert.assertEquals(404, reason.getStatus());
            Assert.assertEquals("user does not exist", reason.getMessage());
        }
    }

    @Test
    public void itCanCreateFetchDeleteAGroup() {
        NuxeoClient client = createClient();
        UserManager userManager = client.userManager();

        // Create
        Group group = createGroup();
        group.setMemberUsers(Collections.singletonList("Administrator"));
        group = userManager.createGroup(group);
        assertNotNull(group);
        assertNotNull(group);
        assertEquals(DEFAULT_GROUP_NAME, group.getGroupName());
        assertEquals("Label totogroup", group.getGroupLabel());
        User user = userManager.fetchUser("Administrator");
        List<String> groups = user.getGroups();
        assertEquals(DEFAULT_GROUP_NAME, groups.get(1));

        // Fetch
        group = userManager.fetchGroup(DEFAULT_GROUP_NAME);
        assertNotNull(group);
        assertEquals(DEFAULT_GROUP_NAME, group.getGroupName());
        assertEquals("Label totogroup", group.getGroupLabel());

        // Delete
        userManager.deleteGroup(DEFAULT_GROUP_NAME);
        try {
            userManager.fetchGroup(DEFAULT_GROUP_NAME);
            fail("Group should not exist");
        } catch (NuxeoClientRemoteException reason) {
            Assert.assertEquals(404, reason.getStatus());
            Assert.assertEquals("group does not exist", reason.getMessage());
        }
    }

    @Test
    public void itCanFetchWorkflowModelsFromRepository() {
        NuxeoClient client = createClient();
        Workflows workflows = client.repository().fetchWorkflowModels();
        assertNotNull(workflows);
        // Assert basic server workflow definitions
        assertEquals(2, workflows.size());
        workflows.sort(Comparator.comparing(Workflow::getName));
        Workflow workflow = workflows.getEntry(0);
        assertEquals("ParallelDocumentReview", workflow.getName());
        workflow = workflows.getEntry(1);
        assertEquals("SerialDocumentReview", workflow.getName());
    }

    @Test
    public void itCanFailServerSide() {
        NuxeoClient client = createClient();
        // create a document under a non existent parent
        Document doc = Document.createWithName("file", "File");
        doc.setPropertyValue("dc:title", "File");
        try {
            client.repository().createDocumentByPath("/absent", doc);
            fail("Previous call should have failed.");
        } catch (NuxeoClientRemoteException e) {
            assertEquals(404, e.getStatus());
            assertEquals("/absent", e.getMessage());
        }
    }

    @Test
    public void itCanSendClientVersionAsUserAgent() {
        NuxeoClient client = createClient();
        // bind an interceptor in order to get the user agent
        UserAgentInterceptor interceptor = new UserAgentInterceptor();
        client.addOkHttpInterceptor(interceptor);
        // do a call to intercept header
        client.repository().fetchDocumentRoot();

        String userAgent = interceptor.userAgent;
        assertNotNull(userAgent);
        assertTrue("User-Agent is not correct=" + userAgent, userAgent.startsWith("okhttp/3.12.13 NuxeoJavaClient/3."));
    }

    /**
     * @return A {@link NuxeoClient} filled with Nuxeo Server URL and default basic authentication.
     */
    public static NuxeoClient createClient() {
        return createClient(LOGIN, PASSWORD);
    }

    /**
     * @return A {@link NuxeoClient} filled with Nuxeo Server URL and input basic authentication.
     */
    public static NuxeoClient createClient(String login, String password) {
        return createClientBuilder(login, password).connect();
    }

    /**
     * @return A {@link NuxeoClient} filled with Nuxeo Server URL and default Portal SSO authentication.
     */
    public static NuxeoClient createClientPortalSSO() {
        return createClientBuilder(new PortalSSOAuthInterceptor(LOGIN, "nuxeo5secretkey")).connect();
    }

    /**
     * @return A {@link NuxeoClient} filled with Nuxeo Server URL and JWT authentication.
     */
    public static NuxeoClient createClientJWT() {
        return createClientBuilder(new JWTAuthInterceptor(JWT)).connect();
    }

    /**
     * @return A {@link NuxeoClient.Builder} filled with Nuxeo Server URL and default basic authentication.
     */
    public static NuxeoClient.Builder createClientBuilder() {
        return createClientBuilder(LOGIN, PASSWORD);
    }

    /**
     * @return A {@link NuxeoClient.Builder} filled with Nuxeo Server URL and input basic authentication.
     */
    public static NuxeoClient.Builder createClientBuilder(String login, String password) {
        return createClientBuilder(new BasicAuthInterceptor(login, password));
    }

    /**
     * @return A {@link NuxeoClient.Builder} filled with Nuxeo Server URL and given authentication.
     */
    protected static NuxeoClient.Builder createClientBuilder(Interceptor authenticationMethod) {
        return new NuxeoClient.Builder().url(BASE_URL).authentication(authenticationMethod).timeout(60);
    }

    public static User createUser() {
        return createUser(DEFAULT_USER_LOGIN);
    }

    /**
     * @since 3.11.0
     */
    public static User createUser(String userName) {
        User user = new User();
        user.setUserName(userName);
        user.setCompany("Nuxeo");
        user.setEmail(userName + "@nuxeo.com");
        user.setFirstName("first " + userName);
        user.setLastName("last " + userName);
        user.setPassword(DEFAULT_USER_PASSWORD);
        user.setTenantId("mytenantid");
        user.setGroups(Collections.singletonList("members"));
        return user;
    }

    public static Group createGroup() {
        return createGroup(DEFAULT_GROUP_NAME);
    }

    /**
     * @since 3.11.0
     */
    public static Group createGroup(String groupName) {
        Group group = new Group();
        group.setGroupName(groupName);
        group.setGroupLabel("Label " + groupName);
        group.setMemberGroups(Collections.singletonList("members"));
        return group;
    }

    public static class UserAgentInterceptor implements Interceptor {

        public String userAgent;

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            userAgent = request.header(HttpHeaders.USER_AGENT);
            return chain.proceed(request);
        }

    }

}
