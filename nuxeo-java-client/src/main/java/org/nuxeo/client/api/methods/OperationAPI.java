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

import java.util.Map;

import okhttp3.RequestBody;

import org.nuxeo.client.api.objects.Operation;
import org.nuxeo.client.api.objects.operation.OperationBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

/**
 * @since 0.1
 */
public interface OperationAPI {

    @POST("automation/{operationId}")
    Call<Object> execute(@Path("operationId") String operationId, @Body OperationBody body);

    @Multipart
    @POST("automation/{operationId}")
    Call<Object> execute(@Path("operationId") String operationId, @Part("request") OperationBody body, @PartMap Map<String, RequestBody> inputs);

    @POST("upload/{batchId}/{fileIdx}/execute/{operationId}")
    Call<Object> execute(@Path("batchId") String batchId, @Path("fileIdx") String fileIdx,
            @Path("operationId") String operationId, @Body OperationBody body);

    @GET("automation/{operationId}")
    Call<Operation> fetchOperation(@Path("operationId") String operationId);

}
