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
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.nuxeo.client.cache.NuxeoResponseCache;
import org.nuxeo.client.cache.ResultCacheInMemory;
import org.nuxeo.client.marshaller.NuxeoConverterFactory;
import org.nuxeo.client.marshaller.NuxeoMarshaller;
import org.nuxeo.client.objects.Operation;
import org.nuxeo.client.objects.Repository;
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
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;

/**
 * @since 0.1
 */
public class NuxeoClient implements Client {

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
        retrofitBuilder = new Retrofit.Builder().baseUrl(url + ConstantsV1.API_PATH).addConverterFactory(
                converterFactory);
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
            Response response = chain.proceed(request);
            return response;
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

    public NuxeoClient depth(String value) {
        header(ConstantsV1.HEADER_DEPTH, value);
        return this;
    }

    public NuxeoClient version(String value) {
        header(ConstantsV1.HEADER_VERSIONING, value);
        return this;
    }

    public NuxeoClient schemas(String... properties) {
        header(ConstantsV1.HEADER_PROPERTIES, StringUtils.join(properties, ","));
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

    @Override
    public NuxeoClient readTimeout(long timeout){
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
        Request request = new Request.Builder().url(url).build();
        try {
            return retrofit.callFactory().newCall(request).execute();
        } catch (IOException e) {
            throw new NuxeoClientException(e);
        }
    }

    public Response delete(String url, String json) {
        Request request;
        if (json != null) {
            RequestBody body = RequestBody.create(ConstantsV1.APPLICATION_JSON_CHARSET_UTF_8, json);
            request = new Request.Builder().url(url).delete(body).build();
        } else {
            request = new Request.Builder().url(url).build();
        }
        try {
            return retrofit.callFactory().newCall(request).execute();
        } catch (IOException e) {
            throw new NuxeoClientException(e);
        }
    }

    public Response put(String url, String json) {
        Request request;
        if (json != null) {
            RequestBody body = RequestBody.create(ConstantsV1.APPLICATION_JSON_CHARSET_UTF_8, json);
            request = new Request.Builder().url(url).put(body).build();
        } else {
            request = new Request.Builder().url(url).build();
        }
        try {
            return retrofit.callFactory().newCall(request).execute();
        } catch (IOException e) {
            throw new NuxeoClientException(e);
        }
    }

    public Response post(String url, String json) {
        Request request;
        if (json != null) {
            RequestBody body = RequestBody.create(ConstantsV1.APPLICATION_JSON_CHARSET_UTF_8, json);
            request = new Request.Builder().url(url).post(body).build();
        } else {
            request = new Request.Builder().url(url).build();
        }
        try {
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

}
