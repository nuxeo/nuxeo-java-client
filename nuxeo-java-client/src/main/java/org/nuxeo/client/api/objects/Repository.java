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
package org.nuxeo.client.api.objects;

import okhttp3.ResponseBody;

import org.nuxeo.client.api.ConstantsV1;
import org.nuxeo.client.api.NuxeoClient;
import org.nuxeo.client.api.methods.RepositoryAPI;
import org.nuxeo.client.api.objects.acl.ACP;
import org.nuxeo.client.api.objects.audit.Audit;
import org.nuxeo.client.api.objects.blob.Blob;
import org.nuxeo.client.api.objects.workflow.Graph;
import org.nuxeo.client.api.objects.workflow.Workflow;
import org.nuxeo.client.api.objects.workflow.Workflows;

import retrofit2.Callback;

/**
 * @since 0.1
 */
public class Repository extends NuxeoEntity {

    protected Document root;

    public Repository(NuxeoClient nuxeoClient) {
        super(ConstantsV1.ENTITY_TYPE_DOCUMENT, nuxeoClient, RepositoryAPI.class);
    }

    public Repository repositoryName(String repositoryName) {
        super.repositoryName = repositoryName;
        return this;
    }

    /**
     * Force the cache refresh.
     */
    public Repository refreshCache() {
        this.refreshCache = true;
        return this;
    }

    public Document getDocumentRoot() {
        return root;
    }

    /* By Id - Sync */

    public Document fetchDocumentById(String documentId) {
        return (Document) getResponse(documentId);
    }

    public Document createDocumentById(String parentId, Document document) {
        return (Document) getResponse(parentId, document);
    }

    public Document updateDocument(Document document) {
        document.setProperties(document.getDirtyProperties());
        return (Document) getResponse(document.getId(), document);
    }

    public void deleteDocument(Document document) {
        getResponse(document.getId());
    }

    public void deleteDocument(String docId) {
        getResponse(docId);
    }

    /* By Id - Async */

    public void fetchDocumentById(String documentId, Callback<Document> callback) {
        execute(callback, documentId);
    }

    public void createDocumentById(String parentId, Document document, Callback<Document> callback) {
        execute(callback, parentId, document);
    }

    public void updateDocument(Document document, Callback<Document> callback) {
        document.setProperties(document.getDirtyProperties());
        execute(callback, document.getId(), document);
    }

    public void deleteDocument(Document document, Callback<ResponseBody> callback) {
        execute(callback, document.getId());
    }

    /* By Path - Sync */

    public Document fetchDocumentRoot() {
        root = (Document) getResponse();
        return root;
    }

    public Document fetchDocumentByPath(String documentPath) {
        return (Document) getResponse(documentPath);
    }

    public Document createDocumentByPath(String parentPath, Document document) {
        return (Document) getResponse(parentPath, document);
    }

    /* By Path - Async */

    public void fetchDocumentRoot(Callback<Document> callback) {
        execute(callback);
    }

    public void fetchDocumentByPath(String documentPath, Callback<Document> callback) {
        execute(callback, documentPath);
    }

    public void createDocumentByPath(String parentPath, Document document, Callback<Document> callback) {
        execute(callback, parentPath, document);
    }

    /* Query - Sync */

    public Documents query(String query) {
        return (Documents) getResponse(query);
    }

    public Documents query(String query, String pageSize, String currentPageIndex, String maxResults, String sortBy,
            String sortOrder, String queryParams) {
        return (Documents) getResponse(query, pageSize, currentPageIndex, maxResults, sortBy, sortOrder, queryParams);
    }

    public Documents queryByProvider(String providerName, String pageSize, String currentPageIndex, String maxResults,
            String sortBy, String sortOrder, String queryParams) {
        return (Documents) getResponse(providerName, pageSize, currentPageIndex, maxResults, sortBy, sortOrder,
                queryParams);
    }

    /* Query - Async */

    public void query(String query, Callback<Documents> callback) {
        execute(callback, query);
    }

    public void query(String query, String pageSize, String currentPageIndex, String maxResults, String sortBy,
            String sortOrder, String queryParams, Callback<Documents> callback) {
        execute(callback, query, pageSize, currentPageIndex, maxResults, sortBy, sortOrder, queryParams);
    }

    public void queryByProvider(String providerName, String pageSize, String currentPageIndex, String maxResults,
            String sortBy, String sortOrder, String queryParams, Callback<Documents> callback) {
        execute(callback, providerName, pageSize, currentPageIndex, maxResults, sortBy, sortOrder, queryParams);
    }

    /* Audit - Sync */

    public Audit fetchAuditByPath(String documentPath) {
        return (Audit) getResponse(documentPath);
    }

    public Audit fetchAuditById(String documentId) {
        return (Audit) getResponse(documentId);
    }

    /* Audit - Async */

    public void fetchAuditByPath(String documentPath, Callback<Audit> callback) {
        execute(callback, documentPath);
    }

    public void fetchAuditById(String documentId, Callback<Audit> callback) {
        execute(callback, documentId);
    }

    /* ACP - Sync */

    public ACP fetchACPByPath(String documentPath) {
        return (ACP) getResponse(documentPath);
    }

    public ACP fetchACPById(String documentId) {
        return (ACP) getResponse(documentId);
    }

    /* ACP - Async */

    public void fetchACPByPath(String documentPath, Callback<ACP> callback) {
        execute(callback, documentPath);
    }

    public void fetchACPById(String documentId, Callback<ACP> callback) {
        execute(callback, documentId);
    }

    /* Children - Sync */

    public Documents fetchChildrenByPath(String parentPath) {
        return (Documents) getResponse(parentPath);
    }

    public Documents fetchChildrenById(String parentId) {
        return (Documents) getResponse(parentId);
    }

    /* Children - Async */

    public void fetchChildrenByPath(String parentPath, Callback<Documents> callback) {
        execute(callback, parentPath);
    }

    public void fetchChildrenById(String parentId, Callback<Documents> callback) {
        execute(callback, parentId);
    }

    /* Blobs - Sync */

    public Blob fetchBlobByPath(String documentPath, String fieldPath) {
        return (Blob) getResponse(documentPath, fieldPath);
    }

    public Blob fetchBlobById(String documentId, String fieldPath) {
        return (Blob) getResponse(documentId, fieldPath);
    }

    /* Blobs - Async */

    public void fetchBlobByPath(String documentPath, String fieldPath, Callback<Blob> callback) {
        execute(callback, documentPath, fieldPath);
    }

    public void fetchBlobById(String documentId, String fieldPath, Callback<Blob> callback) {
        execute(callback, documentId, fieldPath);
    }

    /* Workflows - Sync */

    public Workflow startWorkflowInstanceWithDocPath(String documentPath, Workflow workflow) {
        return (Workflow) getResponse(documentPath, workflow);
    }

    public Workflow startWorkflowInstanceWithDocId(String documentId, Workflow workflow) {
        return (Workflow) getResponse(documentId, workflow);
    }

    public Workflows fetchWorkflowInstancesByDocId(String documentId) {
        return (Workflows) getResponse(documentId);
    }

    public Workflows fetchWorkflowInstancesByDocPath(String documentPath) {
        return (Workflows) getResponse(documentPath);
    }

    public Workflow fetchWorkflowInstance(String workflowInstanceId) {
        return (Workflow) getResponse(workflowInstanceId);
    }

    public void cancelWorkflowInstance(String workflowInstanceId) {
        getResponse(workflowInstanceId);
    }

    public Graph fetchWorkflowInstanceGraph(String workflowInstanceId) {
        return (Graph) getResponse(workflowInstanceId);
    }

    public Graph fetchWorkflowModelGraph(String workflowModelName) {
        return (Graph) getResponse(workflowModelName);
    }

    public Workflow fetchWorkflowModel(String workflowModelName) {
        return (Workflow) getResponse(workflowModelName);
    }

    public Workflows fetchWorkflowModels() {
        return (Workflows) getResponse();
    }

    /* Workflows - Async */

    public void startWorkflowInstanceWithDocPath(String documentPath, Workflow workflow, Callback<Workflow> callback) {
        execute(callback, documentPath, workflow);
    }

    public void startWorkflowInstanceWithDocId(String documentId, Workflow workflow, Callback<Workflow> callback) {
        execute(callback, documentId, workflow);
    }

    public void fetchWorkflowInstancesByDocId(String documentId, Callback<Workflow> callback) {
        execute(callback, documentId);
    }

    public void fetchWorkflowInstancesByDocPath(String documentPath, Callback<Workflow> callback) {
        execute(callback, documentPath);
    }

    public void fetchWorkflowInstance(String workflowInstanceId, Callback<Workflow> callback) {
        execute(callback, workflowInstanceId);
    }

    public void deleteWorkflowInstance(String workflowInstanceId, Callback<ResponseBody> callback) {
        execute(callback, workflowInstanceId);
    }

    public void fetchWorkflowInstanceGraph(String workflowInstanceId, Callback<Graph> callback) {
        execute(callback, workflowInstanceId);
    }

    public void fetchWorkflowModelGraph(String workflowModelName, Callback<Graph> callback) {
        execute(callback, workflowModelName);
    }

    public void fetchWorkflowModel(String workflowModelName, Callback<Workflow> callback) {
        execute(callback, workflowModelName);
    }

    public void fetchWorkflowModels(Callback<Workflows> callback) {
        execute(callback);
    }
}
