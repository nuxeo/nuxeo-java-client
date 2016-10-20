/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *          Nuxeo
 */
package org.nuxeo.client.api;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.apache.commons.lang3.StringUtils;
import org.nuxeo.client.api.marshaller.NuxeoConverterFactory;
import org.nuxeo.client.api.marshaller.NuxeoMarshaller;
import org.nuxeo.client.api.objects.directory.DirectoryManager;
import org.nuxeo.client.api.objects.user.CurrentUser;
import org.nuxeo.client.api.objects.task.TaskManager;
import org.nuxeo.client.internals.spi.NuxeoClientException;
import org.nuxeo.client.api.cache.NuxeoResponseCache;
import org.nuxeo.client.api.cache.ResultCacheInMemory;
import org.nuxeo.client.api.objects.Operation;
import org.nuxeo.client.api.objects.Repository;
import org.nuxeo.client.api.objects.upload.BatchUpload;
import org.nuxeo.client.api.objects.user.UserManager;
import org.nuxeo.client.internals.spi.auth.BasicAuthInterceptor;

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

    @Override
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
        header(ConstantsV1.HEADER_ENRICHERS, StringUtils.join(Arrays.asList(enrichers), ","));
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
        header(ConstantsV1.HEADER_PROPERTIES, StringUtils.join(Arrays.asList(properties), ","));
        return this;
    }

    @Override
    public NuxeoClient setCache(NuxeoResponseCache nuxeoCache) {
        this.nuxeoCache = nuxeoCache;
        return this;
    }

    @Override
    public NuxeoClient setAuthenticationMethod(Interceptor interceptor) {
        okhttpBuilder.interceptors().add(interceptor);
        return this;
    }

    @Override
    public NuxeoClient timeout(long timeout) {
        okhttpBuilder.connectTimeout(timeout, TimeUnit.SECONDS);
        retrofit();
        return this;
    }

    @Override
    public String getBaseUrl() {
        return retrofit.baseUrl().toString();
    }

    @Override
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

    @Override
    public NuxeoResponseCache getNuxeoCache() {
        return nuxeoCache;
    }

    @Override
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

    @Override
    public Response get(String url) {
        Request request = new Request.Builder().url(url).build();
        try {
            return retrofit.callFactory().newCall(request).execute();
        } catch (IOException e) {
            throw new NuxeoClientException(e);
        }
    }

    @Override
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

    @Override
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

    @Override
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
