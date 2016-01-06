/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and others.
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
 *         Vladimir Pasquier <vpasquier@nuxeo.com>
 */
package org.nuxeo.java.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.restapi.test.RestServerFeature;
import org.nuxeo.ecm.restapi.test.RestServerInit;
import org.nuxeo.java.client.api.objects.Document;
import org.nuxeo.java.client.api.objects.Documents;
import org.nuxeo.java.client.api.objects.RecordSet;
import org.nuxeo.java.client.internals.spi.NuxeoClientException;
import org.nuxeo.java.client.marshallers.DocumentMarshaller;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.Jetty;

/**
 * @since 1.0
 */
@RunWith(FeaturesRunner.class)
@Features({ RestServerFeature.class })
@Jetty(port = 18090)
@RepositoryConfig(cleanup = Granularity.METHOD, init = RestServerInit.class)
public class TestRepository extends TestBase {

    @Before
    public void authentication() {
        login();
    }

    @Test
    public void itCanFetchRoot() {
        Document root = nuxeoClient.repository().getDocumentRoot();
        assertNotNull(root);
        assertEquals("Root", root.getType());
        assertEquals("document", root.getEntityType());
        assertEquals("/", root.getParentRef());
        assertEquals("/", root.getPath());
    }

    @Test
    public void itCanFetchRootWithRepositoryName() {
        Document root = nuxeoClient.repository().getDocumentRoot();
        root = nuxeoClient.repository().repositoryName(root.getRepositoryName()).getDocumentRoot();
        assertNotNull(root);
        assertEquals("Root", root.getType());
        assertEquals("document", root.getEntityType());
        assertEquals("/", root.getParentRef());
        assertEquals("/", root.getPath());
    }

    @Test
    public void itCanFetchFolder() {
        Document root = nuxeoClient.repository().getDocumentRoot();
        Document folder = nuxeoClient.repository().getDocumentByPath("folder_2");
        assertNotNull(folder);
        assertEquals("Folder", folder.getType());
        assertEquals("document", folder.getEntityType());
        assertEquals(root.getUid(), folder.getParentRef());
        assertEquals("/folder_2", folder.getPath());
        assertEquals("Folder 2", folder.getTitle());
    }

    @Test
    public void itCanFetchFolderWithRepositoryName() {
        Document root = nuxeoClient.repository().getDocumentRoot();
        Document folder = nuxeoClient.repository()
                                     .repositoryName(root.getRepositoryName())
                                     .getDocumentByPath("folder_2");
        assertNotNull(folder);
        assertEquals("Folder", folder.getType());
        assertEquals("document", folder.getEntityType());
        assertEquals(root.getUid(), folder.getParentRef());
        assertEquals("/folder_2", folder.getPath());
        assertEquals("Folder 2", folder.getTitle());
    }

    @Test
    public void itCanFetchNote() {
        Document folder = nuxeoClient.repository().getDocumentByPath("folder_1");
        Document note = nuxeoClient.repository().getDocumentByPath("folder_1/note_1");
        assertNotNull(note);
        assertEquals("Note", note.getType());
        assertEquals("document", note.getEntityType());
        assertEquals(folder.getUid(), note.getParentRef());
        assertEquals("/folder_1/note_1", note.getPath());
        assertEquals("Note 1", note.getTitle());
    }

    @Test
    public void itCanCreateDocument() {
        Document folder = nuxeoClient.repository().getDocumentByPath("folder_1");
        Document document = new Document("file", "File");
        document.set("dc:title", "new title");
        document = nuxeoClient.repository().createDocumentByPath("folder_1", document);
        assertNotNull(document);
        assertEquals("File", document.getType());
        assertEquals("document", document.getEntityType());
        assertEquals(folder.getUid(), document.getParentRef());
        assertEquals("/folder_1/file", document.getPath());
        assertEquals("new title", document.getTitle());
        assertEquals("new title", document.get("dc:title"));
    }

    @Test
    public void itCanQuery() {
        Documents documents = nuxeoClient.repository().query("SELECT * " + "From Note");
        assertTrue(documents.getDocuments().size() != 0);
        Document document = documents.getDocuments().get(0);
        assertEquals("Note", document.getType());
        assertEquals("test", document.getRepositoryName());
        assertEquals("project", document.getState());
    }

    @Test
    public void itCanUseCaching() {
        // Retrieve a document from query
        Document document = nuxeoClient.enableDefaultCache().repository().getDocumentByPath("folder_1/note_3");
        assertEquals("Note 3", document.get("dc:title"));
        assertTrue(nuxeoClient.getNuxeoCache().size() == 1);

        // Update this document
        Document documentUpdated = new Document("test update", "Note");
        documentUpdated.setId(document.getId());
        documentUpdated.set("dc:title", "note updated");
        documentUpdated = nuxeoClient.repository().updateDocument(documentUpdated);
        assertEquals("note updated", documentUpdated.get("dc:title"));

        // Retrieve again this document within cache
        document = nuxeoClient.repository().getDocumentByPath("folder_1/note_3");
        assertEquals("Note 3", document.get("dc:title"));
        assertTrue(nuxeoClient.getNuxeoCache().size() == 2);

        // Refresh the cache and check the update has been recovered.
        document = nuxeoClient.repository().refreshCache().getDocumentByPath("folder_1/note_3");
        assertEquals("note updated", document.get("dc:title"));
        assertTrue(nuxeoClient.getNuxeoCache().size() == 1);
    }

    @Test
    public void itCanUpdateDocument() {
        Document document = nuxeoClient.repository().getDocumentByPath("folder_1/note_0");
        assertEquals("Note", document.getType());
        assertEquals("test", document.getRepositoryName());
        assertEquals("project", document.getState());
        assertEquals("Note 0", document.getTitle());
        assertEquals("Note 0", document.get("dc:title"));

        Document documentUpdated = new Document("test update", "Note");
        documentUpdated.setId(document.getId());
        documentUpdated.set("dc:title", "note updated");
        documentUpdated.setTitle("note updated");
        documentUpdated.set("dc:nature", "test");

        documentUpdated = nuxeoClient.repository().updateDocument(documentUpdated);
        assertNotNull(documentUpdated);
        assertEquals("note updated", documentUpdated.get("dc:title"));
        assertEquals("test", documentUpdated.get("dc:nature"));

        // Check if the document in the repository has been changed.
        Document result = nuxeoClient.repository().getDocumentById(documentUpdated.getId());
        assertNotNull(result);
        assertEquals("note updated", result.get("dc:title"));
        assertEquals("test", result.get("dc:nature"));
    }

    @Test
    public void itCanDeleteDocument() {
        Document documentToDelete = nuxeoClient.repository().getDocumentByPath("folder_1/note_1");
        assertNotNull(documentToDelete);
        assertTrue(session.exists(new IdRef(documentToDelete.getId())));
        nuxeoClient.repository().deleteDocument(documentToDelete);
        fetchInvalidations();
        assertTrue(!session.exists(new IdRef(documentToDelete.getId())));
    }

    @Test
    public void itCanUseCustomMarshallers() {
        Document folder = nuxeoClient.registerMarshaller(new DocumentMarshaller())
                                     .repository()
                                     .getDocumentByPath("folder_1");
        assertNotNull(folder);
        assertEquals(folder.getPath(), "/folder_1");
        assertEquals(folder.getState(), "project");
        assertEquals(folder.getType(), "Folder");
    }

    @Test
    public void itCanUseQueriesAndResultSet(){
        RecordSet documents = (RecordSet) nuxeoClient.automation().param("query", "SELECT * FROM Document").execute("Repository.ResultSetQuery");
        assertTrue(documents.getUuids().size() != 0);
    }

    @Test
    public void itCanFail(){
        try {
            nuxeoClient.repository().getDocumentByPath("folder_1/wrong");
            fail("Should be not found");
        }catch(NuxeoClientException reason){
            assertEquals(404, reason.getStatus());
        }
    }

    // TODO JAVACLIENT-22 AND JAVACLIENT-20
    @Test
    public void itCanFetchDocumentWithCallback() throws InterruptedException {
//        nuxeoClient.getRepository().getDocumentByPath("folder_2", new Callback<Document>() {
//            @Override
//            public void onResponse(Response<Document> response, Retrofit retrofit) {
//                if (!response.isSuccess()) {
//                    ObjectMapper objectMapper = new ObjectMapper();
//                    NuxeoClientException nuxeoClientException;
//                    try {
//                        nuxeoClientException = objectMapper.readValue(response.errorBody().string(),
//                                NuxeoClientException.class);
//                    } catch (IOException reason) {
//                        throw new NuxeoClientException(reason);
//                    }
//                    fail(nuxeoClientException.getRemoteStackTrace());
//                }
//                Document folder = response.body();
//                assertNotNull(folder);
//                assertEquals("Folder", folder.getType());
//                assertEquals("document", folder.getEntityType());
//                assertEquals("/folder_2", folder.getPath());
//                assertEquals("Folder 2", folder.getTitle());
//            }
//
//            @Override
//            public void onFailure(Throwable reason) {
//                fail(reason.getMessage());
//            }
//        });
    }
}