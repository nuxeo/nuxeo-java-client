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
package org.nuxeo.java.client.api.objects;

import org.nuxeo.java.client.api.ConstantsV1;
import org.nuxeo.java.client.api.NuxeoClient;
import org.nuxeo.java.client.api.methods.RepositoryAPI;
import org.nuxeo.java.client.api.objects.workflow.Graph;

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

    /* By Id - Async */

//    public void getDocumentById(String documentId, Callback<Document> callback) {
//        if (repositoryName == null) {
//            repositoryAPI.getDocumentById(documentId).enqueue(callback);
//        } else {
//            repositoryAPI.getDocumentById(documentId, repositoryName).enqueue(callback);
//        }
//    }
//
//    public void createDocumentById(String parentId, Document document, Callback<Document> callback) {
//        if (repositoryName == null) {
//            repositoryAPI.createDocumentById(parentId, document).enqueue(callback);
//        } else {
//            repositoryAPI.createDocumentById(parentId, document).enqueue(callback);
//        }
//    }
//
//    public void updateDocument(Document document, Callback<Document> callback) {
//        document.setProperties(document.getDirtyProperties());
//        if (repositoryName == null) {
//            repositoryAPI.updateDocument(document.getId(), document).enqueue(callback);
//        } else {
//            repositoryAPI.updateDocument(document.getId(), document, repositoryName).enqueue(callback);
//        }
//    }
//
//    public void deleteDocument(Document document, Callback<ResponseBody> callback) {
//        if (repositoryName == null) {
//            repositoryAPI.deleteDocument(document.getId()).enqueue(callback);
//        } else {
//            repositoryAPI.deleteDocument(document.getId(), repositoryName).enqueue(callback);
//        }
//    }

    /* By Path - Sync */

    public Document fetchDocumentRoot() {
        root = (Document) getResponse();
        return root;
    }

    public Document getDocumentRoot() {
        return root;
    }

    public Document fetchDocumentByPath(String documentPath) {
        return (Document) getResponse(documentPath);
    }

    public Document createDocumentByPath(String parentPath, Document document) {
        return (Document) getResponse(parentPath, document);
    }

    public void deleteDocument(Document document) {
        getResponse(document.getId());
    }

    /* By Path - Async */

//    public void getDocumentRoot(Callback<Document> callback) {
//        // TODO: JAVACLIENT-20
//        // executeAsync(getCurrentMethodName(), callback);
//        if (repositoryName == null) {
//            repositoryAPI.getDocumentRoot().enqueue(callback);
//        } else {
//            repositoryAPI.getDocumentRoot(repositoryName).enqueue(callback);
//        }
//    }
//
//    public void getDocumentByPath(String documentPath, Callback<Document> callback) {
//        if (repositoryName == null) {
//            repositoryAPI.getDocumentByPath(documentPath).enqueue(callback);
//        } else {
//            repositoryAPI.getDocumentByPath(documentPath, repositoryName).enqueue(callback);
//        }
//    }
//
//    public void createDocumentByPath(String parentPath, Document document, Callback<Document> callback) {
//        if (repositoryName == null) {
//            repositoryAPI.createDocumentByPath(parentPath, document).enqueue(callback);
//        } else {
//            repositoryAPI.createDocumentByPath(parentPath, document).enqueue(callback);
//        }
//    }

    /* Query - Sync */

    public Documents query(String query) {
        return (Documents) getResponse(query);
    }

    public Documents query(String query, String pageSize, String currentPageIndex, String maxResults, String sortBy,
            String sortOrder, String queryParams) {
        return (Documents) getResponse(query, pageSize, currentPageIndex,
                maxResults, sortBy, sortOrder, queryParams);
    }

    public Documents queryByProvider(String providerName, String pageSize, String currentPageIndex, String maxResults,
            String sortBy, String sortOrder, String queryParams) {
        return (Documents) getResponse(providerName, pageSize, currentPageIndex,
                maxResults, sortBy, sortOrder, queryParams);
    }

    /* Query - Async */

//    public void query(String query, Callback<Documents> callback) {
//        repositoryAPI.query(query).enqueue(callback);
//    }
//
//    public void query(String query, String pageSize, String currentPageIndex, String maxResults, String sortBy,
//            String sortOrder, String queryParams, Callback<Documents> callback) {
//        repositoryAPI.query(query, pageSize, currentPageIndex, maxResults, sortBy, sortOrder, queryParams).enqueue(
//                callback);
//    }
//
//    public void queryByProvider(String providerName, String pageSize, String currentPageIndex, String maxResults,
//            String sortBy, String sortOrder, String queryParams, Callback<Documents> callback) {
//        repositoryAPI.queryByProvider(providerName, pageSize, currentPageIndex, maxResults, sortBy, sortOrder,
//                queryParams).enqueue(callback);
//    }

    /* Internal */

    // TODO: JAVACLIENT-20
    // protected void executeAsync(String method, Callback<T> callback, Object... parametersArray) {
    // Call<?> methodResult = getCall(method, parametersArray);
    // methodResult.enqueue(callback);
    // }

    /* Children */

    public Documents fetchChildrenByPath(String parentPath){
        return (Documents) getResponse(parentPath);
    }

    public Documents fetchChildrenById(String parentId){
        return (Documents) getResponse(parentId);
    }

    /* Blobs */

    public Blob fetchBlobByPath(String documentPath, String fieldPath){
        return (Blob) getResponse(documentPath, fieldPath);
    }

    public Blob fetchBlobById(String documentId, String fieldPath) {
        return (Blob) getResponse(documentId, fieldPath);
    }

    /* Workflows */

    public Workflow startWorkflowInstanceWithDocPath(String documentPath, Workflow workflow){
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

    public void deleteWorkflowInstance(String workflowInstanceId) {
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
}
