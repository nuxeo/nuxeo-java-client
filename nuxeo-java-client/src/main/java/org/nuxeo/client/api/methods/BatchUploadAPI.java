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

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import org.nuxeo.client.api.ConstantsV1;
import org.nuxeo.client.api.objects.upload.BatchFile;
import org.nuxeo.client.api.objects.upload.BatchUpload;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * @since 0.1
 */
public interface BatchUploadAPI {

    @POST("upload")
    Call<BatchUpload> createBatch();

    @GET("upload/{batchId}")
    Call<List<BatchFile>> fetchBatchFiles(@Path("batchId") String batchId);

    @GET("upload/{batchId}/{fileIdx}")
    Call<BatchFile> fetchBatchFile(@Path("batchId") String batchId, @Path("fileIdx") String fileIdx);

    @DELETE("upload/{batchId}")
    Call<ResponseBody> cancel(@Path("batchId") String batchId);

    @Headers(ConstantsV1.CONTENT_TYPE_APPLICATION_OCTET_STREAM)
    @POST("upload/{batchId}/{fileIdx}")
    Call<BatchUpload> upload(@Header("X-File-Name") String fileName, @Header("X-File-Size") String fileSize,
            @Header("X-File-Type") String fileType, @Header("X-Upload-Type") String uploadType,
            @Header("X-Upload-Chunk-Index") String uploadChunkIndex,
            @Header("X-Upload-Chunk-Count") String totalChunkCount, @Path("batchId") String batchId,
            @Path("fileIdx") String fileIdx, @Body RequestBody file);
}
