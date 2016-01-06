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
package org.nuxeo.java.client.api.methods;

import okhttp3.ResponseBody;

import org.nuxeo.java.client.api.objects.Document;
import org.nuxeo.java.client.api.objects.Documents;
import org.nuxeo.java.client.api.objects.Workflow;
import org.nuxeo.java.client.api.objects.Workflows;
import org.nuxeo.java.client.api.objects.workflow.Graph;

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

    @GET("repo/{repositoryName}/path/{docPath}")
    Call<Document> fetchDocumentByPath(@Path("docPath") String docPath, @Path("repositoryName") String repositoryName);

    @POST("repo/{repositoryName}/path/{parentPath}")
    Call<Document> createDocumentByPath(@Path("parentPath") String parentPath, @Body Document document, @Path("repositoryName") String repositoryName);

    @GET("path/{docPath}")
    Call<Document> fetchDocumentByPath(@Path(value = "docPath", encoded = false) String docPath);

    @POST("path/{parentPath}")
    Call<Document> createDocumentByPath(@Path("parentPath") String parentPath, @Body Document document);

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


    /* Workflows */

    @POST("id/{documentId}/@workflow")
    Call<Workflow> startWorkflowInstanceWithDocId(@Path("documentId") String documentId, @Body Workflow workflow);

    @POST("repo/{repositoryName}/id/{documentId}/@workflow")
    Call<Workflow> startWorkflowInstanceWithDocId(@Path("documentId") String documentId, @Body Workflow workflow, @Path("repositoryName") String repositoryName);

    @POST("path/{documentPath}/@workflow")
    Call<Workflow> startWorkflowInstanceWithDocPath(@Path("documentPath") String documentPath, @Body Workflow workflow);

    @POST("repo/{repositoryName}/path/{documentPath}/@workflow")
    Call<Workflow> startWorkflowInstanceWithDocPath(@Path("documentPath") String documentPath, @Body Workflow workflow, @Path("repositoryName") String repositoryName);

    @GET("id/{documentId}/@workflow")
    Call<Workflows> fetchWorkflowInstances(@Path("documentId") String documentId);

    @GET("repo/{repositoryName}/id/{documentId}/@workflow")
    Call<Workflows> fetchWorkflowInstances(@Path("documentId") String documentId, @Path("repositoryName") String repositoryName);

    @GET("path/{documentPath}/@workflow")
    Call<Workflows> fetchWorkflowInstancesByDocPath(@Path("documentPath") String documentPath);

    @GET("repo/{repositoryName}/id/{documentId}/@workflow")
    Call<Workflows> fetchWorkflowInstancesByDocPath(@Path("documentPath") String documentPath, @Path("repositoryName") String repositoryName);

    @GET("workflow/{workflowInstanceId}")
    Call<Workflow> fetchWorkflowInstance(@Path("workflowInstanceId") String workflowInstanceId);

    @DELETE("workflow/{workflowInstanceId}")
    Call<ResponseBody> deleteWorkflowInstance(@Path("workflowInstanceId") String workflowInstanceId);

    @GET("workflow/{workflowInstanceId}/graph")
    Call<Graph> fetchWorkflowInstanceGraph(@Path("workflowInstanceId") String workflowInstanceId);

    @GET("workflowModel/{workflowModelName}")
    Call<Workflow> fetchWorkflowModel(@Path("workflowModelName") String workflowModelName);

    @GET("workflowModel/{workflowModelName}/graph")
    Call<Graph> fetchWorkflowModelGraph(@Path("workflowModelName") String workflowModelName);

    @GET("workflowModel")
    Call<Workflows> fetchWorkflowModels();

}
