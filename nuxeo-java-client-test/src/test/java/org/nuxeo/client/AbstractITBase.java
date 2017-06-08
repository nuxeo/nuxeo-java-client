/*
 * (C) Copyright 2016-2017 Nuxeo (http://nuxeo.com/) and others.
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

import java.io.File;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.nuxeo.client.objects.Document;
import org.nuxeo.client.objects.Repository;
import org.nuxeo.client.objects.blob.FileBlob;
import org.nuxeo.client.objects.operation.DocRefs;
import org.nuxeo.client.objects.user.UserManager;
import org.nuxeo.common.utils.FileUtils;

/**
 * @since 0.1
 */
public abstract class AbstractITBase {

    protected final NuxeoClient nuxeoClient = ITBase.createClient().schemas("*");

    protected final RepositoryInterceptor repositoryInterceptor = new RepositoryInterceptor();

    // TODO this is weird that deleting documents doesn't cancel workflow on them, maybe there's an asynchronous task
    // TODO which is also weird is: if we not cancel workflows it mess up ACP in ITRepository#itCanManagePermissions on
    // acp.getAcls().get(0).getAces().size() test
    protected final WorkflowInterceptor workflowInterceptor = new WorkflowInterceptor();

    protected final UserGroupInterceptor userGroupInterceptor = new UserGroupInterceptor();

    @Before
    public void init() {
        // Create client and bind interceptors to register creation/deletion on server
        nuxeoClient.okhttpBuilder.addInterceptor(repositoryInterceptor);
        nuxeoClient.okhttpBuilder.addInterceptor(workflowInterceptor);
        nuxeoClient.okhttpBuilder.addInterceptor(userGroupInterceptor);
        nuxeoClient.retrofit();
    }

    public void initDocuments() {
        // Create documents
        for (int i = 1; i < 3; i++) {
            Document doc = new Document("folder_" + i, "Folder");
            doc.setPropertyValue("dc:title", "Folder " + i);
            nuxeoClient.repository().createDocumentByPath("/", doc);
        }

        for (int i = 0; i < 1; i++) {
            Document doc = new Document("note_" + i, "Note");
            doc.setPropertyValue("dc:title", "Note " + i);
            doc.setPropertyValue("note:note", "Note " + i);
            nuxeoClient.repository().createDocumentByPath("/folder_1", doc);
        }

        // Create a file
        Document doc = new Document("file", "File");
        doc.setPropertyValue("dc:title", "File");
        nuxeoClient.repository().createDocumentByPath("/folder_2", doc);
        // Attach a light blob
        File file = FileUtils.getResourceFileFromContext("blob.json");
        FileBlob fileBlob = new FileBlob(file);
        nuxeoClient.automation("Blob.AttachOnDocument").param("document", "/folder_2/file").input(fileBlob).execute();
    }

    @After
    public void tearDown() throws InterruptedException {
        Repository repository = nuxeoClient.repository();
        // First remove proxies before removing documents
        Collection<String> docIdsToDelete = repositoryInterceptor.getDocumentIdsToDelete();
        if (!docIdsToDelete.isEmpty()) {
            nuxeoClient.automation("Document.RemoveProxies").input(new DocRefs(docIdsToDelete)).execute();
            docIdsToDelete.forEach(repository::deleteDocument);
        }
        workflowInterceptor.getWorkflowIdsToDelete().forEach(repository::cancelWorkflowInstance);
        UserManager userManager = nuxeoClient.userManager();
        userGroupInterceptor.getUsersToDelete().forEach(userManager::deleteUser);
        userGroupInterceptor.getGroupsToDelete().forEach(userManager::deleteGroup);
    }

}
