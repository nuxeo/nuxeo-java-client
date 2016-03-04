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

import org.nuxeo.client.api.objects.directory.Directory;
import org.nuxeo.client.api.objects.directory.DirectoryEntry;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @since 0.1
 */
public interface DirectoryManagerAPI {

    @GET("directory/{directoryName}")
    Call<Directory> fetchDirectory(@Path("directoryName") String directoryName);

    @GET("directory/{directoryName}")
    Call<Directory> fetchDirectory(@Path("directoryName") String directoryName,
            @Query("currentPageIndex") String currentPageIndex, @Query("pageSize") String pageSize,
            @Query("maxResults") String maxResults, @Query("sortBy") String sortBy, @Query("sortOrder") String sortOrder);

    @POST("directory/{directoryName}")
    Call<DirectoryEntry> createDirectoryEntry(@Path("directoryName") String directoryName,
            @Body DirectoryEntry directoryEntry);

    @PUT("directory/{directoryName}/{entryId}")
    Call<DirectoryEntry> updateDirectoryEntry(@Path("directoryName") String directoryName,
            @Path("entryId") String directoryEntryId, @Body DirectoryEntry directoryEntry);

    @DELETE("directory/{directoryName}/{entryId}")
    Call<ResponseBody> deleteDirectoryEntry(@Path("directoryName") String directoryName,
            @Path("entryId") String directoryEntryId);

}
