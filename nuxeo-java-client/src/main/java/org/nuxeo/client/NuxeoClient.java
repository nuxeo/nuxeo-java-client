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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.nuxeo.client.cache.NuxeoResponseCache;
import org.nuxeo.client.cache.ResultCacheInMemory;
import org.nuxeo.client.marshaller.NuxeoConverterFactory;
import org.nuxeo.client.marshaller.NuxeoMarshaller;
import org.nuxeo.client.objects.Connectable;
import org.nuxeo.client.objects.Operation;
import org.nuxeo.client.objects.Repository;
import org.nuxeo.client.objects.blob.Blob;
import org.nuxeo.client.objects.blob.Blobs;
import org.nuxeo.client.objects.directory.DirectoryManager;
import org.nuxeo.client.objects.task.TaskManager;
import org.nuxeo.client.objects.upload.BatchUpload;
import org.nuxeo.client.objects.user.CurrentUser;
import org.nuxeo.client.objects.user.UserManager;
import org.nuxeo.client.spi.NuxeoClientException;
import org.nuxeo.client.spi.auth.BasicAuthInterceptor;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * @since 0.1
 */
public class NuxeoClient {

    protected final OkHttpClient.Builder okhttpBuilder;

    protected final Repository repository;

    protected final Operation automation;

    protected final BatchUpload batchUpload;

    protected final UserManager userManager;

    protected final DirectoryManager directoryManager;

    protected final TaskManager taskManager;

    protected final NuxeoConverterFactory converterFactory;

    protected final Retrofit.Builder retrofitBuilder;

    protected CurrentUser currentUser;

    protected NuxeoResponseCache nuxeoCache;

    protected Retrofit retrofit;

    public NuxeoClient(String url, String userName, String password) {
        // okhttp builder
        okhttpBuilder = new OkHttpClient.Builder();
        if (okhttpBuilder.interceptors().isEmpty()) {
            if (userName != null && password != null) {
                setAuthenticationMethod(new BasicAuthInterceptor(userName, password));
            } else {
                throw new NuxeoClientException("Define credentials");
            }
        }
        // retrofit builder
        converterFactory = NuxeoConverterFactory.create();
        retrofitBuilder = new Retrofit.Builder().baseUrl(url + ConstantsV1.API_PATH)
                                                .addConverterFactory(converterFactory);
        // client builder
        retrofit();
        // nuxeo builders
        automation = new Operation(this);
        repository = new Repository(this);
        userManager = new UserManager(this);
        directoryManager = new DirectoryManager(this);
        batchUpload = new BatchUpload(this);
        taskManager = new TaskManager(this);
    }

    public NuxeoClient registerMarshaller(NuxeoMarshaller<?> marshaller) {
        converterFactory.registerMarshaller(marshaller);
        return this;
    }

    public NuxeoClient clearMarshaller() {
        converterFactory.clearMarshaller();
        return this;
    }

    public NuxeoClient enableDefaultCache() {
        nuxeoCache = new ResultCacheInMemory();
        return this;
    }

    public void logout() {
        okhttpBuilder.interceptors().clear();
        retrofit();
    }

    public NuxeoConverterFactory getConverterFactory() {
        return converterFactory;
    }

    public NuxeoClient header(String header, String value) {
        okhttpBuilder.interceptors().add(chain -> {
            Request request = chain.request();
            request = request.newBuilder().addHeader(header, value).build();
            return chain.proceed(request);
        });
        retrofit();
        return this;
    }

    public NuxeoClient enrichers(String... enrichers) {
        header(ConstantsV1.HEADER_ENRICHERS, StringUtils.join(enrichers, ","));
        return this;
    }

    public NuxeoClient voidOperation(boolean value) {
        header(ConstantsV1.HEADER_VOID_OPERATION, Boolean.toString(value));
        return this;
    }

    public NuxeoClient transactionTimeout(long timeout) {
        header(ConstantsV1.HEADER_TX_TIMEOUT, String.valueOf(timeout));
        return this;
    }

    public NuxeoClient fetch(String... fetchs) {
        for (String fetch : fetchs) {
            header(ConstantsV1.HEADER_FETCH, fetch);
        }
        return this;
    }

    /**
     * Sets the depth.
     * <p />
     * Possible values are: `root`, `children` and `max`.
     * <p />
     * @see org.nuxeo.ecm.core.io.registry.context.DepthValues
     */
    public NuxeoClient depth(String value) {
        header(ConstantsV1.HEADER_DEPTH, value);
        return this;
    }

    public NuxeoClient version(String value) {
        header(ConstantsV1.HEADER_VERSIONING, value);
        return this;
    }

    public NuxeoClient schemas(String... properties) {
        header(ConstantsV1.HEADER_PROPERTIES, String.join(",", properties));
        return this;
    }

    public NuxeoClient setCache(NuxeoResponseCache nuxeoCache) {
        this.nuxeoCache = nuxeoCache;
        return this;
    }

    public NuxeoClient setAuthenticationMethod(Interceptor interceptor) {
        okhttpBuilder.interceptors().add(interceptor);
        if (retrofitBuilder != null) {
            retrofit();
        }
        return this;
    }

    public NuxeoClient timeout(long timeout) {
        okhttpBuilder.connectTimeout(timeout, TimeUnit.SECONDS);
        okhttpBuilder.readTimeout(timeout, TimeUnit.SECONDS);
        retrofit();
        return this;
    }

    public NuxeoClient readTimeout(long timeout) {
        okhttpBuilder.readTimeout(timeout, TimeUnit.SECONDS);
        retrofit();
        return this;
    }

    public String getBaseUrl() {
        return retrofit.baseUrl().toString();
    }

    public void shutdown() {
        logout();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public <A> A getApi(Class<A> apiClass) {
        return retrofit.create(apiClass);
    }

    protected void retrofit() {
        OkHttpClient okHttpClient = okhttpBuilder.build();
        retrofit = retrofitBuilder.callFactory(okHttpClient).build();
    }

    public NuxeoResponseCache getNuxeoCache() {
        return nuxeoCache;
    }

    public boolean isCacheEnabled() {
        return nuxeoCache != null;
    }

    /** Services **/

    public Repository repository() {
        return repository;
    }

    public CurrentUser getCurrentUser() {
        return currentUser;
    }

    public CurrentUser fetchCurrentUser() {
        this.currentUser = new CurrentUser(this);
        return currentUser.getCurrentUser();
    }

    public Repository repositoryName(String repositoryName) {
        return repository(repositoryName);
    }

    public Repository repository(String repositoryName) {
        repository.repositoryName(repositoryName);
        return repository;
    }

    public Operation automation() {
        return automation;
    }

    public Operation automation(String operationId) {
        automation.setOperationId(operationId);
        return automation;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public DirectoryManager getDirectoryManager() {
        return directoryManager;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public Response get(String url) {
        return request(url, Request.Builder::get);
    }

    public Response delete(String url) {
        return request(url, Request.Builder::delete);
    }

    public Response delete(String url, String json) {
        RequestBody body = RequestBody.create(ConstantsV1.APPLICATION_JSON_CHARSET_UTF_8, json);
        return request(url, builder -> builder.delete(body));
    }

    public Response put(String url, String json) {
        RequestBody body = RequestBody.create(ConstantsV1.APPLICATION_JSON_CHARSET_UTF_8, json);
        return request(url, builder -> builder.put(body));
    }

    public Response post(String url, String json) {
        RequestBody body = RequestBody.create(ConstantsV1.APPLICATION_JSON_CHARSET_UTF_8, json);
        return request(url, builder -> builder.post(body));
    }

    private Response request(String url, UnaryOperator<Builder> method) {
        try {
            Request.Builder requestBuilder = new Builder().url(url);
            Request request = method.apply(requestBuilder).build();
            return retrofit.callFactory().newCall(request).execute();
        } catch (IOException e) {
            throw new NuxeoClientException(e);
        }
    }

    public BatchUpload fetchUploadManager() {
        return batchUpload.createBatch();
    }

    public BatchUpload batchUpload() {
        return batchUpload;
    }

    /******************************
     * NEW API
     ******************************/

    public <T> T fetchResponse(Call<T> call) {
        // TODO hardcoded for now - check if it's useful, we might want to refresh cache through client
        boolean refreshCache = false;
        if (isCacheEnabled()) {
            if (refreshCache) {
                refreshCache = false;
                nuxeoCache.invalidateAll();
            } else {
                String cacheKey = computeCacheKey(call);
                T result = (T) nuxeoCache.getBody(cacheKey);
                if (result != null) {
                    return result;
                }
            }
        }
        try {
            retrofit2.Response<T> response = call.execute();
            response = handleResponse(call, response);
            return response.body();
        } catch (IOException reason) {
            throw new NuxeoClientException(reason);
        }
    }

    public <T> void fetchResponse(Call<T> call, Callback<T> callback) {
        call.enqueue(new Callback<T>() {

            @Override
            public void onResponse(Call<T> call, retrofit2.Response<T> response) {
                try {
                    callback.onResponse(call, handleResponse(call, response));
                } catch (NuxeoClientException nce) {
                    callback.onFailure(call, nce);
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                callback.onFailure(call, t);
            }

        });
    }

    protected <T> retrofit2.Response<T> handleResponse(Call<T> call, retrofit2.Response<T> response) {
        try {
            // For redirect 308 -> the response should be success
            if (!response.isSuccessful() && response.code() != 308) {
                NuxeoClientException nuxeoClientException;
                String errorBody = response.errorBody().string();
                if (Strings.EMPTY.equals(errorBody)) {
                    nuxeoClientException = new NuxeoClientException(response.code(), response.message());
                } else if (!ConstantsV1.APPLICATION_JSON.equals(response.raw().body().contentType())) {
                    nuxeoClientException = new NuxeoClientException(response.code(), errorBody);
                } else {
                    nuxeoClientException = converterFactory.readJSON(errorBody, NuxeoClientException.class);
                }
                throw nuxeoClientException;
            }
            if (isCacheEnabled()) {
                nuxeoCache.put(computeCacheKey(call), response);
            }
            T body = response.body();
            if (body instanceof ResponseBody) {
                throw new IllegalStateException("Internal client error, everything should be mapped to a type.");
            } else if (body == null) {
                if (response.code() == 204
                        && ConstantsV1.APPLICATION_NUXEO_EMPTY_LIST.equals(response.headers().get("Content-Type"))) {
                    return retrofit2.Response.success((T) new Blobs(), response.raw());
                }
            } else if (body instanceof Connectable) {
                ((Connectable) body).reconnectWith(this);
            } else if (body instanceof List<?>) {
                for (Object item : (List<?>) body) {
                    if (item instanceof Connectable) {
                        ((Connectable) item).reconnectWith(this);
                    }
                }
                // TODO: currently no way to read headers in converter, find a generic way to provide this feature and extract it from client
            } else if (body instanceof Blob) {
                String contentDisposition = response.headers().get("Content-Disposition");
                if (contentDisposition != null) {
                    String fileName = contentDisposition.replaceFirst(".*filename\\*?=(UTF-8'')?(.*)", "$2");
                    try {
                        fileName = URLDecoder.decode(fileName, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        // May not happen
                    }
                    ((Blob) body).setFileName(fileName);
                }
            }
            // No need to wrap the response
            return response;
        } catch (IOException ioe) {
            throw new NuxeoClientException(ioe);
        }
    }

    /**
     * Compute the cache key with request
     */
    protected String computeCacheKey(Call<?> methodResult) {
        Request originalRequest = methodResult.request();
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(ConstantsV1.MD_5);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        digest.update((originalRequest.toString() + originalRequest.headers().toString()).getBytes());
        byte messageDigest[] = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte msg : messageDigest) {
            hexString.append(Integer.toHexString(0xFF & msg));
        }
        return hexString.toString();
    }

}
