/*
 * (C) Copyright 2017 Nuxeo (http://nuxeo.com/) and others.
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

import java.io.File;
import java.util.Collections;
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
import org.nuxeo.client.spi.NuxeoClientException;
import org.nuxeo.common.utils.FileUtils;

/**
 * Tests the basic operation of client. This test is isolated from test framework because it unit tests the operation
 * used in framework to init server and to clean it.
 *
 * @since 3.0.0
 */
public class ITBase {

    public static final String BASE_URL = "http://localhost:8080/nuxeo";

    public static final String LOGIN = "Administrator";

    public static final String PASSWORD = "Administrator";

    public static final String DEFAULT_USER_LOGIN = "toto";

    public static final String DEFAULT_USER_PASSWORD = "totopwd";

    public static final String DEFAULT_USER_EMAIL = "toto@nuxeo.com";

    public static final String DEFAULT_GROUP_NAME = "totogroup";

    @Test
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
        Document document = new Document("note", "Note");
        document.setPropertyValue("dc:title", "note");
        document = repository.createDocumentById(root.getId(), document);
        assertEquals("Note", document.getType());
        assertEquals("default", document.getRepositoryName());
        assertEquals("project", document.getState());
        assertEquals("note", document.getTitle());
        assertEquals("note", document.getPropertyValue("dc:title"));

        Document documentUpdated = new Document("test update", "Note");
        documentUpdated.setId(document.getId());
        documentUpdated.setPropertyValue("dc:title", "note updated");
        documentUpdated.setTitle("note updated");

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
        } catch (NuxeoClientException nce) {
            assertEquals(404, nce.getStatus());
        }
    }

    @Test
    public void itCanAttachBlob() {
        NuxeoClient client = createClient();
        // Create a file
        Document doc = new Document("file", "File");
        doc.setPropertyValue("dc:title", "File");
        doc = client.repository().createDocumentByPath("/", doc);
        // Attach a blob
        File file = FileUtils.getResourceFileFromContext("sample.jpg");
        FileBlob fileBlob = new FileBlob(file);
        client.automation("Blob.AttachOnDocument").param("document", "/file").input(fileBlob).execute();

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
        assertEquals("to", user.getLastName());
        assertEquals(DEFAULT_USER_EMAIL, user.getEmail());

        // Fetch
        user = userManager.fetchUser(DEFAULT_USER_LOGIN);
        assertNotNull(user);
        assertEquals(DEFAULT_USER_LOGIN, user.getId());
        assertEquals("to", user.getLastName());
        assertEquals(DEFAULT_USER_EMAIL, user.getEmail());

        // Delete
        userManager.deleteUser(DEFAULT_USER_LOGIN);
        try {
            userManager.fetchUser(DEFAULT_USER_LOGIN);
            fail("User should not exist");
        } catch (NuxeoClientException reason) {
            Assert.assertEquals(404, reason.getStatus());
            Assert.assertEquals("user does not exist", reason.getException());
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
        assertEquals("Toto Group", group.getGroupLabel());
        User user = userManager.fetchUser("Administrator");
        List<String> groups = user.getGroups();
        assertEquals(DEFAULT_GROUP_NAME, groups.get(1));

        // Fetch
        group = userManager.fetchGroup(DEFAULT_GROUP_NAME);
        assertNotNull(group);
        assertEquals(DEFAULT_GROUP_NAME, group.getGroupName());
        assertEquals("Toto Group", group.getGroupLabel());

        // Delete
        userManager.deleteGroup(DEFAULT_GROUP_NAME);
        try {
            userManager.fetchGroup(DEFAULT_GROUP_NAME);
            fail("Group should not exist");
        } catch (NuxeoClientException reason) {
            Assert.assertEquals(404, reason.getStatus());
            Assert.assertEquals("group does not exist", reason.getException());
        }
    }

    @Test
    public void itCanFetchWorkflowModelsFromRepository() {
        NuxeoClient client = createClient();
        Workflows workflows = client.repository().fetchWorkflowModels();
        assertNotNull(workflows);
        // Assert basic server workflow definitions
        assertEquals(2, workflows.size());
        Workflow workflow = workflows.get(0);
        assertEquals("ParallelDocumentReview", workflow.getName());
        workflow = workflows.get(1);
        assertEquals("SerialDocumentReview", workflow.getName());
    }

    public static NuxeoClient createClient() {
        return createClient(LOGIN, PASSWORD);
    }

    public static NuxeoClient createClient(String login, String password) {
        return createClientBuilder(login, password).connect();
    }

    public static NuxeoClient.Builder createClientBuilder() {
        return createClientBuilder(LOGIN, PASSWORD);
    }

    public static NuxeoClient.Builder createClientBuilder(String login, String password) {
        return new NuxeoClient.Builder().url(BASE_URL).authentication(login, password).timeout(60);
    }

    public static User createUser() {
        User user = new User();
        user.setUserName(DEFAULT_USER_LOGIN);
        user.setCompany("Nuxeo");
        user.setEmail(DEFAULT_USER_EMAIL);
        user.setFirstName("to");
        user.setLastName("to");
        user.setPassword(DEFAULT_USER_PASSWORD);
        user.setTenantId("mytenantid");
        user.setGroups(Collections.singletonList("members"));
        return user;
    }

    public static Group createGroup() {
        Group group = new Group();
        group.setGroupName(DEFAULT_GROUP_NAME);
        group.setGroupLabel("Toto Group");
        return group;
    }

}
