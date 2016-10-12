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
package org.nuxeo.client.api.methods;

import okhttp3.ResponseBody;

import org.nuxeo.client.api.objects.Document;
import org.nuxeo.client.api.objects.blob.Blob;
import org.nuxeo.client.api.objects.Documents;
import org.nuxeo.client.api.objects.acl.ACP;
import org.nuxeo.client.api.objects.audit.Audit;
import org.nuxeo.client.api.objects.task.Task;
import org.nuxeo.client.api.objects.workflow.Graph;
import org.nuxeo.client.api.objects.workflow.Workflow;
import org.nuxeo.client.api.objects.workflow.Workflows;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RepositoryAPI {

    /* Documents */

    @GET("repo/{repositoryName}/path/")
    Call<Document> fetchDocumentRoot(@Path("repositoryName") String repositoryName);

    @GET("path/")
    Call<Document> fetchDocumentRoot();

    @GET("repo/{repositoryName}/id/{documentId}")
    Call<Document> fetchDocumentById(@Path("documentId") String documentId, @Path("repositoryName") String repositoryName);

    @POST("repo/{repositoryName}/id/{parentId}")
    Call<Document> createDocumentById(@Path("parentId") String parentId,
            @Body Document document, @Path("repositoryName") String repositoryName);

    @PUT("repo/{repositoryName}/id/{documentId}")
    Call<Document> updateDocument(@Path("documentId") String documentId,
            @Body Document document, @Path("repositoryName") String repositoryName);

    @DELETE("repo/{repositoryName}/id/{documentId}")
    Call<ResponseBody> deleteDocument(@Path("documentId") String documentId, @Path("repositoryName") String repositoryName);

    @GET("id/{documentId}")
    Call<Document> fetchDocumentById(@Path("documentId") String documentId);

    @POST("id/{parentId}")
    Call<Document> createDocumentById(@Path("parentId") String parentId, @Body Document document);

    @PUT("id/{documentId}")
    Call<Document> updateDocument(@Path("documentId") String documentId, @Body Document document);

    @DELETE("id/{documentId}")
    Call<ResponseBody> deleteDocument(@Path("documentId") String documentId);

    @GET("repo/{repositoryName}/path{docPath}")
    Call<Document> fetchDocumentByPath(@Path(value = "docPath", encoded = true) String docPath, @Path("repositoryName") String repositoryName);

    @POST("repo/{repositoryName}/path{parentPath}")
    Call<Document> createDocumentByPath(@Path("parentPath") String parentPath, @Body Document document, @Path("repositoryName") String repositoryName);

    @GET("path{documentPath}")
    Call<Document> fetchDocumentByPath(@Path(value = "documentPath", encoded = true) String docPath);

    @POST("path{parentPath}")
    Call<Document> createDocumentByPath(@Path(value = "parentPath", encoded = true) String parentPath, @Body Document document);

    /* Query */

    @GET("query")
    Call<Documents> query(@Query("query") String query);

    @GET("query")
    Call<Documents> query(@Query("query") String query, @Query("pageSize") String pageSize,
            @Query("currentPageIndex") String currentPageIndex, @Query("maxResults") String maxResults,
            @Query("sortBy") String sortBy, @Query("sortOrder") String sortOrder,
            @Query("queryParams") String queryParams);

    @GET("query/{providerName}")
    Call<Documents> queryByProvider(@Path("providerName") String providerName, @Query("pageSize") String pageSize,
            @Query("currentPageIndex") String currentPageIndex, @Query("maxResults") String maxResults,
            @Query("sortBy") String sortBy, @Query("sortOrder") String sortOrder,
            @Query("queryParams") String queryParams);

    /* Audit */

    @GET("path{documentPath}/@audit")
    Call<Audit> fetchAuditByPath(@Path(value = "documentPath", encoded = true) String documentPath);

    @GET("id/{documentId}/@audit")
    Call<Audit> fetchAuditById(@Path("documentId") String documentId);

    @GET("repo/{repositoryName}/path{documentPath}/@audit")
    Call<Audit> fetchAuditByPath(@Path(value = "documentPath", encoded = true) String documentPath, @Path("repositoryName") String repositoryName);

    @GET("repo/{repositoryName}/id/{documentId}/@audit")
    Call<Audit> fetchAuditById(@Path("documentId") String documentId, @Path("repositoryName") String repositoryName);

    /* ACP */

    @GET("path{documentPath}/@acl")
    Call<ACP> fetchPermissionsByPath(@Path(value = "documentPath", encoded = true) String documentPath);

    @GET("id/{documentId}/@acl")
    Call<ACP> fetchPermissionsById(@Path("documentId") String documentId);

    @GET("repo/{repositoryName}/path{documentPath}/@acl")
    Call<ACP> fetchPermissionsByPath(@Path(value = "documentPath", encoded = true) String documentPath, @Path("repositoryName") String repositoryName);

    @GET("repo/{repositoryName}/id/{documentId}/@acl")
    Call<ACP> fetchPermissionsById(@Path("documentId") String documentId, @Path("repositoryName") String repositoryName);

    /* Children */

    @GET("path{parentPath}/@children")
    Call<Documents> fetchChildrenByPath(@Path(value = "parentPath", encoded = true) String parentPath);

    @GET("id/{parentId}/@children")
    Call<Documents> fetchChildrenById(@Path("parentId") String parentId);

    @GET("repo/{repositoryName}/path{parentPath}/@children")
    Call<Documents> fetchChildrenByPath(@Path(value = "parentPath", encoded = true) String parentPath, @Path("repositoryName") String repositoryName);

    @GET("repo/{repositoryName}/id/{parentId}/@children")
    Call<Documents> fetchChildrenById(@Path("parentId") String parentId, @Path("repositoryName") String repositoryName);

    /* Blobs */

    @GET("path{documentPath}/@blob/{fieldPath}")
    Call<Blob> fetchBlobByPath(@Path(value = "documentPath", encoded = true) String documentPath, @Path("fieldPath") String fieldPath);

    @GET("id/{documentId}/@blob/{fieldPath}")
    Call<Blob> fetchBlobById(@Path("documentId") String documentId, @Path("fieldPath") String fieldPath);

    @GET("repo/{repositoryName}/path{documentPath}/@blob/{fieldPath}")
    Call<Blob> fetchBlobByPath(@Path(value = "documentPath", encoded = true) String documentPath, @Path("fieldPath") String fieldPath, @Path("repositoryName") String repositoryName);

    @GET("repo/{repositoryName}/id/{documentId}/@blob/{fieldPath}")
    Call<Blob> fetchBlobById(@Path("documentId") String documentId, @Path("fieldPath") String fieldPath, @Path("repositoryName") String repositoryName);


    /* Workflows */

    @POST("id/{documentId}/@workflow")
    Call<Workflow> startWorkflowInstanceWithDocId(@Path("documentId") String documentId, @Body Workflow workflow);

    @POST("repo/{repositoryName}/id/{documentId}/@workflow")
    Call<Workflow> startWorkflowInstanceWithDocId(@Path("documentId") String documentId, @Body Workflow workflow, @Path("repositoryName") String repositoryName);

    @POST("path{documentPath}/@workflow")
    Call<Workflow> startWorkflowInstanceWithDocPath(@Path(value = "documentPath", encoded = true) String documentPath, @Body Workflow workflow);

    @POST("repo/{repositoryName}/path{documentPath}/@workflow")
    Call<Workflow> startWorkflowInstanceWithDocPath(@Path(value = "documentPath", encoded = true) String documentPath, @Body Workflow workflow, @Path("repositoryName") String repositoryName);

    @GET("id/{documentId}/@workflow")
    Call<Workflows> fetchWorkflowInstances(@Path("documentId") String documentId);

    @GET("repo/{repositoryName}/id/{documentId}/@workflow")
    Call<Workflows> fetchWorkflowInstances(@Path("documentId") String documentId, @Path("repositoryName") String repositoryName);

    @GET("path{documentPath}/@workflow")
    Call<Workflows> fetchWorkflowInstancesByDocPath(@Path(value = "documentPath", encoded = true) String documentPath);

    @GET("repo/{repositoryName}/id/{documentId}/@workflow")
    Call<Workflows> fetchWorkflowInstancesByDocPath(@Path("documentPath") String documentPath, @Path("repositoryName") String repositoryName);

    @GET("workflow/{workflowInstanceId}")
    Call<Workflow> fetchWorkflowInstance(@Path("workflowInstanceId") String workflowInstanceId);

    @DELETE("workflow/{workflowInstanceId}")
    Call<ResponseBody> cancelWorkflowInstance(@Path("workflowInstanceId") String workflowInstanceId);

    @GET("workflow/{workflowInstanceId}/graph")
    Call<Graph> fetchWorkflowInstanceGraph(@Path("workflowInstanceId") String workflowInstanceId);

    @GET("workflowModel/{workflowModelName}")
    Call<Workflow> fetchWorkflowModel(@Path("workflowModelName") String workflowModelName);

    @GET("workflowModel/{workflowModelName}/graph")
    Call<Graph> fetchWorkflowModelGraph(@Path("workflowModelName") String workflowModelName);

    @GET("workflowModel")
    Call<Workflows> fetchWorkflowModels();

    /* Tasks */

    @GET("id/{documentId}/@task")
    Call<Task> fetchTaskById(@Path("documentId") String documentId);

    @GET("repo/{repositoryName}/id/{documentId}/@task")
    Call<Task> fetchTaskById(@Path("documentId") String documentId, @Path("repositoryName") String repositoryName);

}
