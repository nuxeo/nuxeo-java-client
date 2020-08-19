/*
 * (C) Copyright 2016-2018 Nuxeo (http://nuxeo.com/) and others.
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
 *     Vladimir Pasquier <vpasquier@nuxeo.com>
 *     Kevin Leturc <kleturc@nuxeo.com>
 */
package org.nuxeo.client;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.nuxeo.client.Operations.BLOB_ATTACH_ON_DOCUMENT;
import static org.nuxeo.client.Operations.ES_WAIT_FOR_INDEXING;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.nuxeo.client.objects.Document;
import org.nuxeo.client.objects.Repository;
import org.nuxeo.client.objects.blob.Blob;
import org.nuxeo.client.objects.blob.FileBlob;
import org.nuxeo.client.objects.operation.DocRefs;
import org.nuxeo.client.objects.user.UserManager;
import org.nuxeo.common.utils.FileUtils;

/**
 * @since 0.1
 */
public abstract class AbstractITBase {

    public static final String FOLDER_2_FILE = "/folder_2/file";

    protected final NuxeoClient nuxeoClient = ITBase.createClient().schemas("*");

    protected final RepositoryInterceptor repositoryInterceptor = new RepositoryInterceptor();

    // TODO this is weird that deleting documents doesn't cancel workflow on them, maybe there's an asynchronous task
    // TODO which is also weird is: if we don't cancel workflows it mess up ACP in ITRepository#itCanManagePermissions
    // on acp.getAcls().get(0).getAces().size() test
    protected final WorkflowInterceptor workflowInterceptor = new WorkflowInterceptor();

    protected final UserGroupInterceptor userGroupInterceptor = new UserGroupInterceptor();

    @Before
    public void init() {
        // Create client and bind interceptors to register creation/deletion on server
        nuxeoClient.addOkHttpInterceptor(repositoryInterceptor);
        nuxeoClient.addOkHttpInterceptor(workflowInterceptor);
        nuxeoClient.addOkHttpInterceptor(userGroupInterceptor);
    }

    public void initDocuments() {
        // Create documents
        for (int i = 1; i < 3; i++) {
            Document doc = Document.createWithName("folder_" + i, "Folder");
            doc.setPropertyValue("dc:title", "Folder " + i);
            nuxeoClient.repository().createDocumentByPath("/", doc);
        }

        for (int i = 0; i < 1; i++) {
            Document doc = Document.createWithName("note_" + i, "Note");
            doc.setPropertyValue("dc:title", "Note " + i);
            doc.setPropertyValue("dc:source", "Source " + i);
            doc.setPropertyValue("note:note", "Note " + i);
            nuxeoClient.repository().createDocumentByPath("/folder_1", doc);
        }

        // Create a file
        Document doc = Document.createWithName("file", "File");
        doc.setPropertyValue("dc:title", "File");
        nuxeoClient.repository().createDocumentByPath("/folder_2", doc);
        // Attach a light blob
        File file = FileUtils.getResourceFileFromContext("blob.json");
        FileBlob fileBlob = new FileBlob(file, "blob.json", "text/plain");
        nuxeoClient.operation(BLOB_ATTACH_ON_DOCUMENT)
                   .voidOperation(true)
                   .param("document", FOLDER_2_FILE)
                   .input(fileBlob)
                   .execute();
        // page providers can leverage Elasticsearch so wait for indexing before starting tests
        nuxeoClient.operation(ES_WAIT_FOR_INDEXING).param("refresh", true).param("waitForAudit", true).execute();
    }

    @After
    public void tearDown() {
        Repository repository = nuxeoClient.repository();

        // First cleanup workflow
        // since NXP-29100 they are cleaned when document is deleted but we can't wait for the Work to be completed
        workflowInterceptor.getWorkflowIdsToDelete().forEach(repository::cancelWorkflowInstance);
        // Second delete documents
        Collection<String> docIdsToDelete = repositoryInterceptor.getDocumentIdsToDelete();
        if (!docIdsToDelete.isEmpty()) {
            // delete proxies before deleting documents - as we can not delete document having a proxy
            nuxeoClient.operation(Operations.DOCUMENT_REMOVE_PROXIES).input(new DocRefs(docIdsToDelete)).execute();
            docIdsToDelete.forEach(repository::deleteDocument);
        }
        // Finally delete users / groups
        UserManager userManager = nuxeoClient.userManager();
        userGroupInterceptor.getUsersToDelete().forEach(userManager::deleteUser);
        userGroupInterceptor.getGroupsToDelete().forEach(userManager::deleteGroup);
    }

    /**
     * @since 3.1
     */
    protected void assertContentEquals(String expectedFilePath, Blob blob) {
        try {
            File expectedBlobFile = FileUtils.getResourceFileFromContext(expectedFilePath);
            assertEquals(IOUtils.toString(blob.getStream(), UTF_8),
                    new String(Files.readAllBytes(expectedBlobFile.toPath())));
        } catch (IOException e) {
            fail("Unable to read response or expected file, e=" + e);
        } finally {
            try {
                blob.getStream().close();
            } catch (IOException e) {
                fail("Unable to close the stream, e=" + e);
            }
        }
    }

}
