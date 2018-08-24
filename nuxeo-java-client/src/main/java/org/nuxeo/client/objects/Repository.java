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
package org.nuxeo.client.objects;

import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.methods.RepositoryAPI;
import org.nuxeo.client.objects.acl.ACP;
import org.nuxeo.client.objects.audit.Audit;
import org.nuxeo.client.objects.blob.FileBlob;
import org.nuxeo.client.objects.blob.StreamBlob;
import org.nuxeo.client.objects.workflow.Graph;
import org.nuxeo.client.objects.workflow.Workflow;
import org.nuxeo.client.objects.workflow.Workflows;

import okhttp3.ResponseBody;
import retrofit2.Callback;

/**
 * @since 0.1
 */
public class Repository extends RepositoryEntity<RepositoryAPI, Repository> {

    public Repository(NuxeoClient nuxeoClient) {
        super(EntityTypes.DOCUMENT, RepositoryAPI.class, nuxeoClient);
    }

    public Repository(NuxeoClient nuxeoClient, String repositoryName) {
        this(nuxeoClient);
        this.repositoryName = repositoryName;
    }

    public Document fetchDocumentRoot() {
        if (repositoryName == null) {
            return fetchResponse(api.fetchDocumentRoot());
        }
        return fetchResponse(api.fetchDocumentRoot(repositoryName));
    }

    public void fetchDocumentRoot(Callback<Document> callback) {
        if (repositoryName == null) {
            fetchResponse(api.fetchDocumentRoot(), callback);
        } else {
            fetchResponse(api.fetchDocumentRoot(repositoryName), callback);
        }
    }

    /* By Id - Sync */

    public Document fetchDocumentById(String documentId) {
        if (repositoryName == null) {
            return fetchResponse(api.fetchDocumentById(documentId));
        }
        return fetchResponse(api.fetchDocumentById(documentId, repositoryName));
    }

    public Document createDocumentById(String parentId, Document document) {
        if (repositoryName == null) {
            return fetchResponse(api.createDocumentById(parentId, document));
        }
        return fetchResponse(api.createDocumentById(parentId, document, repositoryName));
    }

    public Document updateDocument(Document document) {
        document.setProperties(document.getDirtyProperties());
        if (repositoryName == null) {
            return fetchResponse(api.updateDocument(document.getId(), document));
        }
        return fetchResponse(api.updateDocument(document.getId(), document, repositoryName));
    }

    public void deleteDocument(Document document) {
        deleteDocument(document.getId());
    }

    public void deleteDocument(String docId) {
        if (repositoryName == null) {
            fetchResponse(api.deleteDocument(docId));
        } else {
            fetchResponse(api.deleteDocument(docId, repositoryName));
        }
    }

    /* By Id - Async */

    public void fetchDocumentById(String documentId, Callback<Document> callback) {
        if (repositoryName == null) {
            fetchResponse(api.fetchDocumentById(documentId), callback);
        } else {
            fetchResponse(api.fetchDocumentById(documentId, repositoryName), callback);
        }
    }

    public void createDocumentById(String parentId, Document document, Callback<Document> callback) {
        document.setProperties(document.getDirtyProperties());
        if (repositoryName == null) {
            fetchResponse(api.createDocumentById(parentId, document), callback);
        } else {
            fetchResponse(api.createDocumentById(parentId, document, repositoryName), callback);
        }
    }

    public void updateDocument(Document document, Callback<Document> callback) {
        document.setProperties(document.getDirtyProperties());
        if (repositoryName == null) {
            fetchResponse(api.updateDocument(document.getId(), document), callback);
        } else {
            fetchResponse(api.updateDocument(document.getId(), document, repositoryName), callback);
        }
    }

    public void deleteDocument(Document document, Callback<ResponseBody> callback) {
        if (repositoryName == null) {
            fetchResponse(api.deleteDocument(document.getId()), callback);
        } else {
            fetchResponse(api.deleteDocument(document.getId(), repositoryName), callback);
        }
    }

    /* By Path - Sync */

    public Document fetchDocumentByPath(String documentPath) {
        if (repositoryName == null) {
            return fetchResponse(api.fetchDocumentByPath(documentPath));
        }
        return fetchResponse(api.fetchDocumentByPath(documentPath, repositoryName));
    }

    public Document createDocumentByPath(String parentPath, Document document) {
        if (repositoryName == null) {
            return fetchResponse(api.createDocumentByPath(parentPath, document));
        }
        return fetchResponse(api.createDocumentByPath(parentPath, document, repositoryName));
    }

    /* By Path - Async */

    public void fetchDocumentByPath(String documentPath, Callback<Document> callback) {
        if (repositoryName == null) {
            fetchResponse(api.fetchDocumentByPath(documentPath), callback);
        } else {
            fetchResponse(api.fetchDocumentByPath(documentPath, repositoryName), callback);
        }
    }

    public void createDocumentByPath(String parentPath, Document document, Callback<Document> callback) {
        if (repositoryName == null) {
            fetchResponse(api.createDocumentByPath(parentPath, document), callback);
        } else {
            fetchResponse(api.createDocumentByPath(parentPath, document, repositoryName), callback);
        }
    }

    /* Query - Sync */

    public Documents query(String query) {
        return fetchResponse(api.query(query));
    }

    public Documents query(String query, String pageSize, String currentPageIndex, String maxResults, String sortBy,
            String sortOrder, String queryParams) {
        return fetchResponse(api.query(query, pageSize, currentPageIndex, maxResults, sortBy, sortOrder, queryParams));
    }

    public Documents queryByProvider(String providerName, String pageSize, String currentPageIndex, String maxResults,
            String sortBy, String sortOrder, String queryParams) {
        return fetchResponse(api.queryByProvider(providerName, pageSize, currentPageIndex, maxResults, sortBy,
                sortOrder, queryParams));
    }

    /* Query - Async */

    public void query(String query, Callback<Documents> callback) {
        fetchResponse(api.query(query), callback);
    }

    public void query(String query, String pageSize, String currentPageIndex, String maxResults, String sortBy,
            String sortOrder, String queryParams, Callback<Documents> callback) {
        fetchResponse(api.query(query, pageSize, currentPageIndex, maxResults, sortBy, sortOrder, queryParams),
                callback);
    }

    public void queryByProvider(String providerName, String pageSize, String currentPageIndex, String maxResults,
            String sortBy, String sortOrder, String queryParams, Callback<Documents> callback) {
        fetchResponse(api.queryByProvider(providerName, pageSize, currentPageIndex, maxResults, sortBy, sortOrder,
                queryParams), callback);
    }

    /* Audit - Sync */

    public Audit fetchAuditByPath(String documentPath) {
        if (repositoryName == null) {
            return fetchResponse(api.fetchAuditByPath(documentPath));
        }
        return fetchResponse(api.fetchAuditByPath(documentPath, repositoryName));
    }

    public Audit fetchAuditById(String documentId) {
        if (repositoryName == null) {
            return fetchResponse(api.fetchAuditById(documentId));
        }
        return fetchResponse(api.fetchAuditById(documentId, repositoryName));
    }

    /* Audit - Async */

    public void fetchAuditByPath(String documentPath, Callback<Audit> callback) {
        if (repositoryName == null) {
            fetchResponse(api.fetchAuditByPath(documentPath), callback);
        } else {
            fetchResponse(api.fetchAuditByPath(documentPath, repositoryName), callback);
        }
    }

    public void fetchAuditById(String documentId, Callback<Audit> callback) {
        if (repositoryName == null) {
            fetchResponse(api.fetchAuditById(documentId), callback);
        } else {
            fetchResponse(api.fetchAuditById(documentId, repositoryName), callback);
        }
    }

    /* ACP - Sync */

    public ACP fetchACPByPath(String documentPath) {
        if (repositoryName == null) {
            return fetchResponse(api.fetchPermissionsByPath(documentPath));
        }
        return fetchResponse(api.fetchPermissionsByPath(documentPath, repositoryName));
    }

    public ACP fetchACPById(String documentId) {
        if (repositoryName == null) {
            return fetchResponse(api.fetchPermissionsById(documentId));
        }
        return fetchResponse(api.fetchPermissionsById(documentId, repositoryName));
    }

    /* ACP - Async */

    public void fetchACPByPath(String documentPath, Callback<ACP> callback) {
        if (repositoryName == null) {
            fetchResponse(api.fetchPermissionsByPath(documentPath), callback);
        } else {
            fetchResponse(api.fetchPermissionsByPath(documentPath, repositoryName), callback);
        }
    }

    public void fetchACPById(String documentId, Callback<ACP> callback) {
        if (repositoryName == null) {
            fetchResponse(api.fetchPermissionsById(documentId), callback);
        } else {
            fetchResponse(api.fetchPermissionsById(documentId, repositoryName), callback);
        }
    }

    /* Children - Sync */

    public Documents fetchChildrenByPath(String parentPath) {
        if (repositoryName == null) {
            return fetchResponse(api.fetchChildrenByPath(parentPath));
        }
        return fetchResponse(api.fetchChildrenByPath(parentPath, repositoryName));
    }

    public Documents fetchChildrenById(String parentId) {
        if (repositoryName == null) {
            return fetchResponse(api.fetchChildrenById(parentId));
        }
        return fetchResponse(api.fetchChildrenById(parentId, repositoryName));
    }

    /* Children - Async */

    public void fetchChildrenByPath(String parentPath, Callback<Documents> callback) {
        if (repositoryName == null) {
            fetchResponse(api.fetchChildrenByPath(parentPath), callback);
        } else {
            fetchResponse(api.fetchChildrenByPath(parentPath, repositoryName), callback);
        }
    }

    public void fetchChildrenById(String parentId, Callback<Documents> callback) {
        if (repositoryName == null) {
            fetchResponse(api.fetchChildrenById(parentId), callback);
        } else {
            fetchResponse(api.fetchChildrenById(parentId, repositoryName), callback);
        }
    }

    /* Blobs - Sync */

    /**
     * @deprecated since 3.1, use {@link #streamBlobByPath(String, String)} instead
     */
    @Deprecated
    public FileBlob fetchBlobByPath(String documentPath, String fieldPath) {
        if (repositoryName == null) {
            return fetchResponse(api.fetchBlobByPath(documentPath, fieldPath));
        }
        return fetchResponse(api.fetchBlobByPath(documentPath, fieldPath, repositoryName));
    }

    /**
     * @deprecated since 3.1, use {@link #streamBlobById(String, String)} instead
     */
    @Deprecated
    public FileBlob fetchBlobById(String documentId, String fieldPath) {
        if (repositoryName == null) {
            return fetchResponse(api.fetchBlobById(documentId, fieldPath));
        }
        return fetchResponse(api.fetchBlobById(documentId, fieldPath, repositoryName));
    }

    public StreamBlob streamBlobByPath(String documentPath, String fieldPath) {
        if (repositoryName == null) {
            return fetchResponse(api.streamBlobByPath(documentPath, fieldPath));
        }
        return fetchResponse(api.streamBlobByPath(documentPath, fieldPath, repositoryName));
    }

    public StreamBlob streamBlobById(String documentId, String fieldPath) {
        if (repositoryName == null) {
            return fetchResponse(api.streamBlobById(documentId, fieldPath));
        }
        return fetchResponse(api.streamBlobById(documentId, fieldPath, repositoryName));
    }

    /* Blobs - Async */

    /**
     * @deprecated since 3.1, use {@link #streamBlobByPath(String, String, Callback)} instead
     */
    @Deprecated
    public void fetchBlobByPath(String documentPath, String fieldPath, Callback<FileBlob> callback) {
        if (repositoryName == null) {
            fetchResponse(api.fetchBlobByPath(documentPath, fieldPath), callback);
        } else {
            fetchResponse(api.fetchBlobByPath(documentPath, fieldPath, repositoryName), callback);
        }
    }

    /**
     * @deprecated since 3.1, use {@link #streamBlobById(String, String, Callback)} instead
     */
    @Deprecated
    public void fetchBlobById(String documentId, String fieldPath, Callback<FileBlob> callback) {
        if (repositoryName == null) {
            fetchResponse(api.fetchBlobById(documentId, fieldPath), callback);
        } else {
            fetchResponse(api.fetchBlobById(documentId, fieldPath, repositoryName), callback);
        }
    }

    public void streamBlobByPath(String documentPath, String fieldPath, Callback<StreamBlob> callback) {
        if (repositoryName == null) {
            fetchResponse(api.streamBlobByPath(documentPath, fieldPath), callback);
        } else {
            fetchResponse(api.streamBlobByPath(documentPath, fieldPath, repositoryName), callback);
        }
    }

    public void streamBlobById(String documentId, String fieldPath, Callback<StreamBlob> callback) {
        if (repositoryName == null) {
            fetchResponse(api.streamBlobById(documentId, fieldPath), callback);
        } else {
            fetchResponse(api.streamBlobById(documentId, fieldPath, repositoryName), callback);
        }
    }

    /* Workflows - Sync */

    public Workflow startWorkflowInstanceWithDocPath(String documentPath, Workflow workflow) {
        if (repositoryName == null) {
            return fetchResponse(api.startWorkflowInstanceWithDocPath(documentPath, workflow));
        }
        return fetchResponse(api.startWorkflowInstanceWithDocPath(documentPath, workflow, repositoryName));
    }

    public Workflow startWorkflowInstanceWithDocId(String documentId, Workflow workflow) {
        if (repositoryName == null) {
            return fetchResponse(api.startWorkflowInstanceWithDocId(documentId, workflow));
        }
        return fetchResponse(api.startWorkflowInstanceWithDocId(documentId, workflow, repositoryName));
    }

    public Workflows fetchWorkflowInstancesByDocId(String documentId) {
        if (repositoryName == null) {
            return fetchResponse(api.fetchWorkflowInstances(documentId));
        }
        return fetchResponse(api.fetchWorkflowInstances(documentId, repositoryName));
    }

    public Workflows fetchWorkflowInstancesByDocPath(String documentPath) {
        if (repositoryName == null) {
            return fetchResponse(api.fetchWorkflowInstancesByDocPath(documentPath));
        }
        return fetchResponse(api.fetchWorkflowInstancesByDocPath(documentPath, repositoryName));
    }

    public Workflow fetchWorkflowInstance(String workflowInstanceId) {
        return fetchResponse(api.fetchWorkflowInstance(workflowInstanceId));
    }

    public void cancelWorkflowInstance(String workflowInstanceId) {
        fetchResponse(api.cancelWorkflowInstance(workflowInstanceId));
    }

    public Graph fetchWorkflowInstanceGraph(String workflowInstanceId) {
        return fetchResponse(api.fetchWorkflowInstanceGraph(workflowInstanceId));
    }

    public Graph fetchWorkflowModelGraph(String workflowModelName) {
        return fetchResponse(api.fetchWorkflowModelGraph(workflowModelName));
    }

    public Workflow fetchWorkflowModel(String workflowModelName) {
        return fetchResponse(api.fetchWorkflowModel(workflowModelName));
    }

    public Workflows fetchWorkflowModels() {
        return fetchResponse(api.fetchWorkflowModels());
    }

    /* Workflows - Async */

    public void startWorkflowInstanceWithDocPath(String documentPath, Workflow workflow, Callback<Workflow> callback) {
        if (repositoryName == null) {
            fetchResponse(api.startWorkflowInstanceWithDocPath(documentPath, workflow), callback);
        } else {
            fetchResponse(api.startWorkflowInstanceWithDocPath(documentPath, workflow, repositoryName), callback);
        }
    }

    public void startWorkflowInstanceWithDocId(String documentId, Workflow workflow, Callback<Workflow> callback) {
        if (repositoryName == null) {
            fetchResponse(api.startWorkflowInstanceWithDocId(documentId, workflow), callback);
        } else {
            fetchResponse(api.startWorkflowInstanceWithDocId(documentId, workflow, repositoryName), callback);
        }
    }

    public void fetchWorkflowInstancesByDocId(String documentId, Callback<Workflows> callback) {
        if (repositoryName == null) {
            fetchResponse(api.fetchWorkflowInstances(documentId), callback);
        } else {
            fetchResponse(api.fetchWorkflowInstances(documentId, repositoryName), callback);
        }
    }

    public void fetchWorkflowInstancesByDocPath(String documentPath, Callback<Workflows> callback) {
        if (repositoryName == null) {
            fetchResponse(api.fetchWorkflowInstancesByDocPath(documentPath), callback);
        } else {
            fetchResponse(api.fetchWorkflowInstancesByDocPath(documentPath, repositoryName), callback);
        }
    }

    public void fetchWorkflowInstance(String workflowInstanceId, Callback<Workflow> callback) {
        fetchResponse(api.fetchWorkflowInstance(workflowInstanceId), callback);
    }

    public void deleteWorkflowInstance(String workflowInstanceId, Callback<ResponseBody> callback) {
        // TODO DOES IT WORK ?
        // execute(callback, workflowInstanceId);
    }

    public void fetchWorkflowInstanceGraph(String workflowInstanceId, Callback<Graph> callback) {
        fetchResponse(api.fetchWorkflowInstanceGraph(workflowInstanceId), callback);
    }

    public void fetchWorkflowModelGraph(String workflowModelName, Callback<Graph> callback) {
        fetchResponse(api.fetchWorkflowModelGraph(workflowModelName), callback);
    }

    public void fetchWorkflowModel(String workflowModelName, Callback<Workflow> callback) {
        fetchResponse(api.fetchWorkflowModel(workflowModelName), callback);
    }

    public void fetchWorkflowModels(Callback<Workflows> callback) {
        fetchResponse(api.fetchWorkflowModels(), callback);
    }

}
