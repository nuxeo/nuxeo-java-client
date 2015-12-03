package org.nuxeo.java.client.api.methods;

import java.util.List;

import org.nuxeo.java.client.api.objects.Document;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface RepositoryAPI {

    @GET("repo/{repositoryName}/path/")
    Call<Document> getDocumentRoot(@Path("repositoryName") String repositoryName);

    @GET("path/")
    Call<Document> getDocumentRoot();

    @GET("repo/{repositoryName}/id/{documentId}")
    Call<Document> getDocumentById(@Path("documentId") String documentId, @Path("repositoryName") String repositoryName);

    @POST("repo/{repositoryName}/id/{parentId}")
    Call<Document> createDocumentById(@Path("parentId") String parentId,
            @Body Document document, @Path("repositoryName") String repositoryName);

    @POST("repo/{repositoryName}/id/{documentId}")
    Call<Document> updateDocumentById(@Path("documentId") String documentId,
            @Body Document document, @Path("repositoryName") String repositoryName);

    @DELETE("repo/{repositoryName}/id/{documentId}")
    Call<Document> deleteDocumentById(@Path("documentId") String documentId, @Path("repositoryName") String repositoryName);

    @GET("id/{documentId}")
    Call<Document> getDocumentById(@Path("documentId") String documentId);

    @POST("id/{parentId}")
    Call<Document> createDocumentById(@Path("parentId") String parentId, @Body Document document);

    @POST("id/{documentId}")
    Call<Document> updateDocumentById(@Path("documentId") String documentId, @Body Document document);

    @DELETE("id/{documentId}")
    Call<Document> deleteDocumentById(@Path("documentId") String documentId);

    @GET("repo/{repositoryName}/path/{docPath}")
    Call<Document> getDocumentByPath(@Path("docPath") String docPath, @Path("repositoryName") String repositoryName);

    @POST("repo/{repositoryName}/path/{parentPath}")
    Call<Document> createDocumentByPath(@Path("parentPath") String parentPath, @Body Document document, @Path("repositoryName") String repositoryName);

    @POST("repo/{repositoryName}/path/{docPath}")
    Call<Document> updateDocumentByPath(@Path("docPath") String docPath,
            @Body Document document, @Path("repositoryName") String repositoryName);

    @DELETE("repo/{repositoryName}/path/{docPath}")
    Call<Document> deleteDocumentByPath(@Path("docPath") String docPath, @Path("repositoryName") String repositoryName);

    @GET("path/{docPath}")
    Call<Document> getDocumentByPath(@Path("docPath") String docPath);

    @POST("path/{parentPath}")
    Call<Document> createDocumentByPath(@Path("parentPath") String parentPath, @Body Document document);

    @POST("path/{docPath}")
    Call<Document> updateDocumentByPath(@Path("docPath") String docPath, @Body Document document);

    @DELETE("path/{docPath}")
    Call<Document> deleteDocumentByPath(@Path("docPath") String docPath);

    @GET("query")
    Call<List<Document>> query(@Query("query") String query, @Query("pageSize") String pageSize,
            @Query("currentPageIndex") String currentPageIndex, @Query("maxResults") String maxResults,
            @Query("sortBy") String sortBy, @Query("sortOrder") String sortOrder,
            @Query("queryParams") String queryParams);

    @GET("query/{providerName}")
    Call<List<Document>> queryByProvider(@Path("providerName") String providerName, @Query("pageSize") String pageSize,
            @Query("currentPageIndex") String currentPageIndex, @Query("maxResults") String maxResults,
            @Query("sortBy") String sortBy, @Query("sortOrder") String sortOrder,
            @Query("queryParams") String queryParams);

}
